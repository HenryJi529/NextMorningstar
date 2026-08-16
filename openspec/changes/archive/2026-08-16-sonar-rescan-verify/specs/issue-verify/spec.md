# issue-verify 规格

## 目的

用 SonarQube 重扫客观判定 AI 修复是否消除漏洞，失败自动整轮回退。

## 需求

### 需求:重扫判定修复

#### 场景:全部修复有效

- **WHEN** 重扫后数量对比无回归（in-scope issue 数量 ≤ 扫描基线 − 已修复数）
- **THEN** 标记所有 issue 为 `VERIFIED`

#### 场景:存在修复无效或引入回归

- **WHEN** 重扫后存在 issue 仍为 OPEN 或有新增回归（数量对比异常）
- **THEN** 状态机发 `VERIFY_FAILED` → RESTORING → RestoreAction 整轮回退
- **AND** issue 回 `SELECTED`（**不标 FAILED**，决策 31），RestoreAction 完整 7 步还原到 `origin/<branch>`（丢弃全部 fix commit）
- **AND** `VerifyResult` 记录 key 差集明细：未修复 = 当前扫描 ∩ 本轮 FIXED 的 key、新引入 = 当前扫描 − 扫描基线 key，同时写入 message（排障用，判定口径不变）
- **AND** 还原完成后 RestoredTrigger 判定重试或放弃（见 fix-runtime-container 决策 16）

#### 场景:修复导致编译失败

- **WHEN** 重扫前 `mvn compile` 失败（mavenBuild 不吞错：无 pom 才跳过，有 pom 编译失败即抛异常）
- **THEN** 整轮 `VERIFY_FAILED`，message 带编译报错，不再以旧字节码推进 sonar 扫描
