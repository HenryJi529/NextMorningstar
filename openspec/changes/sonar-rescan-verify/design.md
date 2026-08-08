## 上下文

FixAction 一轮批量修复多个 issue。VerifyAction 需要在最新 commit 上扫描所有预期已修复的 issue，判定是否全部有效。MVP 阶段不做逐 commit 精准回退（理由见 fix-runtime-container 决策 18）。

## 目标 / 非目标

**目标:** 全量判定——全部通过则放行，任意一个失败则整轮回退。
**非目标:** 不做逐 commit 保留；不做强制 SUBMIT 部分成果。

## 决策

### 决策 1: 全量判定 + 整轮回退

VerifyAction 在 HEAD 上扫描全部预期已修复的 issue：

- **全部 CLOSED** → 所有 issue `status=VERIFIED`，进入 SUBMIT。
- **存在 OPEN** → 所有 issue `status=FAILED`，状态机发 `VERIFY_FAILED` → RESTORING → RestoreAction 完整 7 步还原到 `origin/<branch>`（丢弃全部 fix commit）→ RESTORED → RestoredTrigger 判定重试或放弃。

整轮回退、整轮重试。失败 issue 在下个夜间窗口被 dev-plan 决策 14（跨 run 记忆）自动排除。

### 决策 2: VerifyAction 不操作 git

VerifyAction 纯判定：`mvn -q compile` → `sonar-scanner` 重扫 → 查 issue 状态 → 回写 `dev_issue.status`。git 操作（reset/clean/switch/chown）全部由 RestoreAction 执行，职责分离。
