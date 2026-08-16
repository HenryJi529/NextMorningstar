# issue-scan 规格

## 目的

双通道并行扫描：SonarQube 规则引擎发现已知模式 + Claude 语义审查发现设计缺陷，统一落库为 `dev_issue`。

## 需求

### 需求：获取并落库待修复漏洞

#### 场景：双通道扫描并记录漏洞

- **WHEN** 运行进入扫描阶段
- **THEN** 先删除本 run 旧 issue（重试幂等），容器内 `mvn compile`（无 pom.xml 才跳过；有 pom 编译失败即抛异常响亮失败）→ `sonar-scanner` 推分析 → 后端调 `/api/issues/search` 拉取 OPEN issue → 调 `/api/rules/show` 拿规则描述 → `InScopeSeverities.sonar` 过滤 → 映射为 Issue(SONAR)
- **AND** 容器内 `claude --print` + `--json-schema` + `--output-format json` 自由探索项目 → 后端从 `structured_output` 反序列化 → `InScopeSeverities.ai` 过滤 → 映射为 Issue(AI)
- **AND** 两通道随机打乱后分别按项目 `maxSonarIssuesPerRun`/`maxAiIssuesPerRun` 截断 → batch insert 落 `dev_issue`（不做 severity 排序/规则黑名单/去重/跨 run 排除）
- **AND** `ScanResult` 记录 `scannedSonarIssueNum`/`scannedAiIssueNum`（截断前 in-scope 总数，供 VerifyAction 数量对比回归检测）与 `scannedSonarIssueKeys`（全量基线快照，供 Verify 失败时 key 差集明细，亦为前端漏斗"扫描发现"数据源）
