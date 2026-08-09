# issue-scan 规格

## 目的

双通道并行扫描：SonarQube 规则引擎发现已知模式 + Claude 语义审查发现设计缺陷，统一落库为 `dev_issue`。

## 需求

### 需求:获取并落库待修复漏洞

#### 场景:双通道扫描并记录漏洞

- **WHEN** 运行进入扫描阶段
- **THEN** 容器内 `mvn compile`（有 pom.xml 时）→ `sonar-scanner` 推分析 → 后端调 `/api/issues/search` 拉取 OPEN issue → 调 `/api/rules/show` 拿规则描述 → 映射为 Issue(SONAR)
- **AND** 容器内 `claude --print` 自由探索项目 → 输出结构化 JSON → 映射为 Issue(AI)
- **AND** 两通道随机打乱后分别按项目 `maxSonarIssuesPerRun`/`maxAiIssuesPerRun` 截断 → batch insert 落 `dev_issue`
- **AND** SonarQube 通道记录全部 OPEN issue key 进 `ScanResult.sonarIssueKeys`（供 VerifyAction 回归检测基线）
