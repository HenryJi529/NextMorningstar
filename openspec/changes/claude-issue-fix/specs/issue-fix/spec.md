# issue-fix 规格

## 目的

由 AI 逐漏洞修复代码,产出一漏洞一 commit。

## 需求

### 需求:逐漏洞修复并提交

#### 场景:修复单个漏洞

- **WHEN** 修复阶段处理一个 `SELECTED` issue
- **THEN** AI 阅读其 rule 后修改代码,平台为该修复产生一个独立 commit
- **AND** 回写该 issue 的 commit、诊断报告与状态

#### 场景:超时或取消

- **WHEN** 单 issue 或整 run 超时,或收到取消
- **THEN** 在安全点终止,已提交的修复保留,未完成的标记失败
