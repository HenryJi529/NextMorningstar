# issue-scan 规格

## 目的

从 SonarQube 获取待修复漏洞并落库,排除不宜修复的密钥类规则。

## 需求

### 需求:获取并落库待修复漏洞

#### 场景:扫描并记录漏洞

- **WHEN** 运行进入扫描阶段
- **THEN** 对工作区代码编译并扫描,将 OPEN issue 按 severity 落库到 `dev_issue`
- **AND** 数量按 `maxSonarIssuesPerRun` 截断

#### 场景:排除密钥类规则

- **WHEN** 某 issue 属于配置的规则黑名单(如凭据/密钥)
- **THEN** 该 issue 不进入待修复列表

#### 场景:排除近期修复失败的 issue

- **WHEN** 某 issue 在 `dev_issue` 近期记录为 FAILED
- **THEN** 该 issue 本次不进入待修复列表(避免每夜反复重试)
