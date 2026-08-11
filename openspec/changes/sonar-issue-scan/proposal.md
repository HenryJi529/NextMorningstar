## 为什么

修复的输入是漏洞列表。需双通道并行扫描：SonarQube 规则引擎发现已知模式 + Claude 语义审查发现设计缺陷和隐蔽 bug，统一落库为 `dev_issue`，作为后续修复的输入。

## 变更内容

- **SonarQube 通道**:容器内 `find pom.xml` + `mvn -q compile`(产 `target/classes`)→ `sonar-scanner` 推分析 → 调 `/api/issues/search`(翻页取全量)拉 OPEN issue → 调 `/api/rules/show` 拿规则描述存进 metadata → 随机打乱 → 按 `maxSonarIssuesPerRun` 截断 → batch insert。
- **Sonar 数据映射**：取 issue 和 rule 的 `impacts` 数组（非旧 `type`/`severity` 单字段）→ `RELIABILITY` → `reliabilitySeverity`、`SECURITY` → `securitySeverity`、`MAINTAINABILITY` → `maintainabilitySeverity`。`impacts` 的 severity 值为 BLOCKER/HIGH/MEDIUM/LOW/INFO。
- **AI Discovery 通道**:Claude 自由探索项目 → 识别代码问题 → 输出结构化 JSON → map to Issue。
- **记录基线**:SonarQube 通道 in-scope issue 总数存入 `ScanResult.scannedSonarIssueNum`，供 VerifyAction 数量对比回归检测。
- 合并去重不做（双通道发现问题类型不同、重叠概率极低），跨 run 排除 FAILED 不做（当前阶段应验证修复能力），规则黑名单不做（随机选择天然分散风险）。
- 截断数直接读 project 字段——**不 fallback 全局配置**。创建项目时如果前端未指定，后端将当前全局默认值写入 DB，之后全局配置变更不影响已有项目。
- `ScanResult` 加 `scannedSonarIssueNum`/`scannedAiIssueNum`。
- ~~`AiDiscoveryProperties` 配置类 + yml（enable 开关）~~ — 永不做。AI 通道始终开启，失败即 ScanAction FAILED，不外挂开关。

## 能力

### 新增能力
- `issue-scan`:双通道（SonarQube + AI Discovery）获取并落库待修复漏洞。

## 影响范围

- `ScanAction`:替换 Mock，双通道实现。
- `dev_issue` 写入。
- `ScanResult` 加双通道计数（`scannedSonarIssueNum`/`scannedAiIssueNum`）。
- ~~`AiDiscoveryProperties`~~ — 未创建。AI 通道始终开启。
