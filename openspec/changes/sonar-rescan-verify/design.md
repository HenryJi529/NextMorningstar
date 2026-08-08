## 上下文

FixAction 一轮批量修复多个 issue。VerifyAction 需要两道防线验证：SonarQube 重扫做客观门槛（关闭+回归检测），Claude review 读 fix diff 做语义验证（修对了没）。MVP 阶段不做逐 commit 精准回退（理由见 fix-runtime-container 决策 18）。

## 目标 / 非目标

**目标:** 两道防线全量判定——全部通过则放行，任意一个失败则整轮回退。
**非目标:** 不做逐 commit 保留；不做强制 SUBMIT 部分成果。

## 决策

### 决策 1: 两道防线 + 整轮回退

**第一道（SonarQube 客观判定）:**
- sonar-scanner 重扫 → 查本 run SELECTED issue 是否全 CLOSED
- 对比 `ScanResult.issueKeys` 基线 → 判断是否有新 issue（回归）
- 任一未关闭 / 有回归 → FAILED + RESTORING

**第二道（Claude 语义验证）:**
- 读 fix diff + 原始 issue 信息（title/codeSnippet/metadata）
- Claude 判定修改是否正确（PASSED / FAILED）
- 有 FAILED → 整轮 RESTORING

两道都通过 → VERIFIED。SonarQube 做客观门槛，Claude 做语义判定。

整轮回退、整轮重试。失败 issue 在下个夜间窗口被 dev-plan 决策 14（跨 run 记忆）自动排除。

### 决策 2: VerifyAction 不操作 git

VerifyAction 纯判定：`mvn -q compile` → `sonar-scanner` 重扫 → 查 issue 状态 → 回写 `dev_issue.status`。git 操作（reset/clean/switch/chown）全部由 RestoreAction 执行，职责分离。
