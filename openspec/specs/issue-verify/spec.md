# issue-verify 规格

## 目的

用 SonarQube 重扫客观判定 AI 修复是否消除漏洞，叠加 Claude 语义复核，失败自动整轮回退。

## 需求

### 需求：重扫判定修复

#### 场景：全部修复有效

- **WHEN** 重扫后数量对比无回归（当前 in-scope issue 数量 ≤ 扫描基线 `scannedSonarIssueNum` − 已修复数）且 Claude 逐条语义复核全部通过
- **THEN** 标记所有 issue 为 `VERIFIED`

#### 场景：存在修复无效或引入回归

- **WHEN** 重扫后存在 issue 仍为 OPEN 或有新增回归（数量对比异常），或 Claude 语义复核任一判定 false
- **THEN** 状态机发 `VERIFY_FAILED` → RESTORING → RestoreAction 整轮回退
- **AND** issue 回 `SELECTED`（**不标 FAILED**），RestoreAction 完整还原到 `origin/<branch>`（丢弃全部 fix commit）
- **AND** `VerifyResult` 按 key 求差集记录明细：未修复 = 当前扫描 ∩ 本轮 FIXED 的 key（`unfixedSonarIssueKeys`）、新引入 = 当前扫描 − 扫描基线 key（`introducedSonarIssueKeys`），同时写入 message（排障用，判定口径仍是数量对比，不变）
- **AND** 还原完成后 RestoredTrigger 判定重试或放弃（见 fix-runtime 规格）

#### 场景：修复导致编译失败

- **WHEN** 重扫前 `mvn compile` 失败（mavenBuild 不吞错：无 pom 才跳过，有 pom 编译失败即抛异常）
- **THEN** 整轮 `VERIFY_FAILED`，message 带编译报错，不再以旧字节码推进 sonar 扫描
