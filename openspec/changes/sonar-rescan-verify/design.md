## 上下文

FixAction 一轮批量修复多个 issue。VerifyAction 需要两道防线验证：SonarQube 重扫做客观门槛（数量对比检测回归），Claude review 读 `commitMessage` + 自己读代码做语义验证（修对了没，**不喂 diff**）。MVP 阶段不做逐 commit 精准回退（理由见 fix-runtime-container 决策 18）。

## 目标 / 非目标

**目标:** 两道防线全量判定——全部通过则放行，任意一个失败则整轮回退。
**非目标:** 不做逐 commit 保留；不做强制 SUBMIT 部分成果。

## 决策

### 决策 1: 两道防线 + 整轮回退

**第一道（SonarQube 客观判定）:**
- sonar-scanner 重扫 → 获取当前 in-scope issue 总数
- 数量对比检测回归：`currentIssueNum > scannedSonarIssueNum - fixedSonarIssueNum` → 存在未修复或引入新 issue
- 有回归/未修复 → 整轮 `VERIFY_FAILED` → RESTORING（issue 不标 FAILED，回 `SELECTED`，决策 31）

**第二道（Claude 语义验证）:**
- 读原始 issue 字段（title/metadata）+ FixAction 留下的 `commitMessage`（修复思路）+ Claude 自己读取修复后代码（**不喂 diff**，容器内 claude code CLI 能直接 `cat` 文件）
- 两维度判定：① commitMessage 的修复思路逻辑上能否解决原问题 ② 当前代码是否按该思路正确修改且消除原问题
- 逐条 review + fail-fast 短路：任一 issue 输出 `{"verified":false}` → 整轮 RESTORING

两道都通过 → VERIFIED。SonarQube 做客观门槛，Claude 做语义判定。

整轮回退、整轮重试。失败 issue 在下个夜间窗口被 dev-plan 决策 14（跨 run 记忆）自动排除。

### 决策 2: VerifyAction 不操作 git

VerifyAction 纯判定：`mvn -q compile` → `sonar-scanner` 重扫 → 查 issue 状态 → 回写 `dev_issue.status`。git 操作（reset/clean/switch/chown）全部由 RestoreAction 执行，职责分离。

### 决策 3: 第二道不喂 diff，让 Claude 自己读代码

不把 fix diff 塞进 prompt。容器内 claude code CLI（working dir `/workspace/repo`、`--dangerously-skip-permissions`）本就能自己读取文件，喂 diff 冗余。改用 FixAction 留下的 `commitMessage`（`{subject,body}`）承载"修复思路"：第二道先据 commitMessage 判思路逻辑正确性，再对照当前代码判实现是否到位——比喂 diff 更贴近"评审别人修复"的语义，也少一次取 diff 的开销。

### 决策 4: 第二道输出最小 JSON，砍 reason

Claude 只输出 `{"verified":true/false}`。失败即整轮回退（issue 回 SELECTED，决策 31）+ 下轮自动排除（dev-plan 决策 28），无人看 reason；调试需求由 `log.info(rawOutput)` 兜底。砍 reason 省 token、降输出复杂度，失败 message 用固定文案（"问题X未通过AI验证"）。解析走 `runClaude`（与 FixAction 共享 `--json-schema` + `--output-format json`）+ `VerifyVerdict`（VerifyAction 内部静态类，`verified` 用基本类型，Claude 漏字段默认 false 不 NPE）。

### 决策 5: 第二道逐条 review + fail-fast 短路

对每个 `FIXED` issue 单独跑一次 Claude review（聚焦、判得准），任一 `verified=false` 立即短路 return FAILED。结果等价 spec 的"整轮判定"——失败整轮回退、不逐条标 FAILED（决策 31）。全部通过则循环外统一回写 VERIFIED。
