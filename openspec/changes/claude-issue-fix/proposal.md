## 为什么

这是流水线核心:让 AI 真正修复漏洞。FixAction 对所有 issue 使用统一 prompt——Claude 从 `dev_issue` 的字段（`title`/`codeSnippet`/`metadata`）中获取完整诊断上下文，不需要 MCP 或外部 API。ScanAction 已在写入阶段存好了所有必要信息。平台控制 commit，保证"一漏洞一 commit"且 commit 可控。

## 变更内容

- `FixAction`:遍历 `dev_issue`(status=`SELECTED`),逐个 `docker exec claude --dangerously-skip-permissions --print "..."` 修复。
- 统一 prompt:读 issue 字段（title/codeSnippet/metadata.description/metadata.suggestion/metadata.filePath），不区分 source。
- 切修复分支 `switch -C fix/<runId>`，逐 issue：claude 改文件 → 临时 alpine/git 容器 `add -A && commit`(一漏洞一 commit,纯本地不需凭证) → `rev-parse HEAD` 取 commitSha。
- **commit_message**:Claude 按内嵌 text block 模板输出 JSON `{subject, body}`（中文），后端括号深度提取 + 反序列化 → 拼 `subject\n\nbody` 给 `git commit -m`，结构化对象回写 `dev_issue.commit_message`（供 PR 评论）。**不引用外部模板文件**。
- 回写 `dev_issue`(commitSha/commitMessage/status=`FIXED`)，按 source 累加 `FixResult.fixedSonarIssueNum`/`fixedAiIssueNum`。
- 任一 issue 修复失败 → `FixResult(FAILED)` → RestoreAction 整轮回退（git 7 步 + issue 状态还原，见 RestoreAction 决策）。
- **不做 per-action 超时**：复用全局 `CronTask`（`run-timeout-minutes` + 每 5min 检测）。

## 能力

### 新增能力
- `issue-fix`:由 AI 逐漏洞修复代码,统一 prompt 不依赖外部服务。

## 影响范围

- `FixAction`:替换 Mock。
- `RestoreAction`:新增 issue 状态还原（FIXED/VERIFIED → SELECTED，清 commit 字段）。
- `dev_issue`:`commit_message` 列改存 `{subject, body}` 结构（CommitMessageTypeHandler）；`Status` 枚举删 `FAILED`。
- `FixResult`:双计数 `fixedSonarIssueNum`/`fixedAiIssueNum`。
