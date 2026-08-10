## 上下文

修复时 Claude 不需要再通过 MCP 查外部知识库——ScanAction 阶段已把 SonarQube rule 描述和 AI 诊断全存进 `dev_issue` 的 `metadata` JSON 和顶层字段（`title`/`codeSnippet`/三列 severity）。FixAction 只读 issue 字段，不分 source。

## 目标 / 非目标

**目标:** Claude 读 issue 字段即修，统一 prompt；平台控制 commit；一漏洞一 commit。
**非目标:** 不让 AI 自行 commit(平台控制,保证规范与可回滚)；不依赖 MCP 或外部 API；不做 per-action 超时(复用全局 CronTask)；不做跨 run 排除 FAILED。

## 决策

### 决策 1: 统一 prompt，读 issue 字段即修

FixAction 对所有 issue 使用同一 prompt 模板，Claude 从 `title`/`codeSnippet`/`metadata` 中获取完整诊断上下文（description/suggestion/filePath），不需要 MCP 或外部 API。ScanAction 已在写入阶段存好了所有必要信息。

### 决策 2: commit 归临时 alpine/git 容器

claude 只改 named volume 文件，**临时 alpine/git 容器** `add`/`commit`(纯本地操作,不需凭证),保证一漏洞一 commit + 规范 message。

### 决策 3: ~~双层超时~~ → 复用全局 CronTask（8/10 改）

~~单 issue 限深度(`--max-turns`),整 run 限时(wall-clock + CancelTracker)。~~ **8/10 砍掉**：不引入 per-action 超时，复用全局 `CronTask.cancelTimeoutRuns`（`run-timeout-minutes` + 每 5min `timeout-cron`）。FixAction 卡死由全局兜底，不重复造轮子。

### 决策 4: commit_message 由 AI 生成，结构 {subject, body}

Claude 基于 issue 字段用**中文**生成 commit message，结构 `{subject, body}`：

- `subject`：本次修复一句话总结（→ commit 第一行）
- `body`：修复思路与具体改动（→ commit 正文）

**只两个字段**（8/10 定）：去掉 `verification`（验证是 VerifyAction 的职责，AI 自述验证不可靠且 preemptive）、去掉 `risk`（同理）。AI 按内嵌 text block 模板输出 JSON `{subject, body}`，后端括号深度提取 `{...}` + `objectMapper` 反序列化成 `Issue.CommitMessage`，再拼 `subject\n\nbody` 交 `git commit -m`。**不引用外部模板文件**（`ai-report-template.md` 弃用，模板内嵌在 FixAction 代码里）。

### 决策 5: RestoreAction 管全部回退（git + issue）（8/10 定）

RestoreAction 不仅还原 git（决策 23 的 7 步），也还原 issue 状态：`FIXED/VERIFIED → SELECTED`，清空 `commitSha`/`commitMessage`。FixAction 只负责修，不做回退（幂等重入由 RestoreAction 保证）。issue 还原用 `LambdaUpdateWrapper` 直接写在 RestoreAction（不开 Mapper 自定义方法，符合项目「纯 BaseMapper + Wrapper」惯例）。

### 决策 6: issue 状态机简化（8/10 定）

`Issue.Status` 删除 `FAILED`。fail-fast 模型下 issue 只走 `SELECTED → FIXED → VERIFIED`，失败一律整轮回 `SELECTED`（不标 FAILED）。`ACCEPTED`/`REJECTED` 预留给 SUBMIT 后终态。verify 同 fix 一样 fail-fast：验不过直接抛异常整轮回退，不逐条标 FAILED。

### 决策 7: FixResult 双计数（8/10 定）

`FixResult` 记 `fixedSonarIssueNum` / `fixedAiIssueNum`（非单一 `fixedIssueNum`）。失败时 message 说明卡在哪个 issue，双计数说明修了几个、卡在哪种 source，便于诊断。

### 决策 8: clean -fdx 维持（8/10 定）

RestoreAction 的 `clean -fdx` 不改 `-fd`。重试回 FIXING 不走 SCAN，`target/` 等构建产物无复用价值（即便到 VERIFY 源码已变、target 过期要重编译）。`-x` 无误删风险（gitignore 的都是可重建产物，`maven-settings`/sonar workdir/claude 缓存都不在 repo）。

### 决策 9: catch 只兜 ProcessExecutionException（+ FixAction 的 JsonProcessingException）（8/10 定）

不补 `DataAccessException`（issue 还原 DB 失败概率极低），让全局 CronTask 兜底。维持 ScanAction 既有风格。

### 决策 10: FixAction 不需要 chown（8/10 定）

FixAction 的全部 git 操作都**不重写工作区文件**：`switch -C fix/<runId>` 创建新分支（基于当前 HEAD，内容相同，git 不 checkout）、`add -A`/`commit` 只写 `.git`。因此工作区源文件始终是 claude（bot）写的 bot 属主，整个 action 进出都不需要 `chown`。chown 是 SyncAction/RestoreAction 的事——它们的 `reset --hard`/`clean`/`switch` 会重写工作区为 root 属主，末尾必须 chown 还 bot。FixAction 夹在中间：进来时工作区已是 bot（上一个 sync/restore chown 过），自己又不重写，所以省掉 chown（`.git` 元数据变 root 无所谓——claude 只改源文件不碰 `.git`，git 操作都 root 跑）。
