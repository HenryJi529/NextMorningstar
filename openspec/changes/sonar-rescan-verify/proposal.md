## 为什么

需验证 AI 修复是否真正有效。VerifyAction 采用两道防线：
1. **SonarQube 重扫**：客观判定——SELECTED issue 是否已 CLOSED + 对比 `ScanResult.issueKeys` 基线检测回归
2. **Claude 语义验证**：读 fix diff + 原始 issue 信息，判定修改是否正确

两道都通过才放行。任一失败则整轮回退。SonarQube 做客观门槛，Claude 做语义判定。

## 变更内容

- `VerifyAction`:容器内 `mvn -q compile` → `sonar-scanner` 重扫 → 调 `/api/issues/search` 查 SELECTED issue 状态。
- 回归检测：对比 `ScanResult.issueKeys` 基线，新出现的 issue 视为回归。
- Claude review：读 fix diff + issue 字段，判定 PASSED/FAILED。
- 两道都通过 → 所有 issue `status=VERIFIED`。
- 任一失败 → 整轮 `VERIFY_FAILED` → RestoreAction 整轮回退（issue 回 `SELECTED`，**不标 FAILED**，决策 31）。
- MVP 不做逐 commit 保留（理由见 fix-runtime-container 决策 18）。

## 能力

### 新增能力
- `issue-verify`:两道防线（SonarQube 客观 + Claude 语义）判定修复有效性,失败自动整轮回退。

## 影响范围

- `VerifyAction`:替换 Mock。
- `dev_issue` 状态更新。
