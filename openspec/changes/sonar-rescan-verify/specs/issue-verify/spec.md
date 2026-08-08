# issue-verify 规格

## 目的

用 SonarQube 重扫客观判定 AI 修复是否消除漏洞，失败自动整轮回退。

## 需求

### 需求:重扫判定修复

#### 场景:全部修复有效

- **WHEN** 重扫后所有 issue 在 SonarQube 均已关闭
- **THEN** 标记所有 issue 为 `VERIFIED`

#### 场景:存在修复无效

- **WHEN** 重扫后存在 issue 仍为 OPEN
- **THEN** 标记所有 issue 为 `FAILED`
- **AND** 状态机发 `VERIFY_FAILED` → RESTORING → RestoreAction 完整 7 步还原到 `origin/<branch>`（丢弃全部 fix commit）
- **AND** 还原完成后 RestoredTrigger 判定重试或放弃（见 fix-runtime-container 决策 16）
