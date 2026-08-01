# issue-verify 规格

## 目的

用 SonarQube 重扫客观判定 AI 修复是否消除漏洞,失败自动回滚。

## 需求

### 需求:重扫判定修复

#### 场景:修复有效

- **WHEN** 重扫后某 issue 在 SonarQube 已关闭
- **THEN** 标记该 issue 为 `VERIFIED`

#### 场景:修复无效

- **WHEN** 重扫后某 issue 仍为 OPEN
- **THEN** 回滚其 commit 并标记 `FAILED`
