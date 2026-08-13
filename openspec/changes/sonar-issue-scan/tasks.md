## 1. SonarQube 通道

- [x] 1.1 `ScanAction` 容器内 `find pom.xml` + `mvn -q compile` 产 `target/classes`。
- [x] 1.2 容器内 `sonar-scanner` 推分析到 SonarQube。
- [x] 1.3 调 `/api/issues/search`(翻页取全量)拉 OPEN issue → InScopeSeverities.sonar 过滤，记录 in-scope 总数进 `ScanResult.scannedSonarIssueNum`（供 VerifyAction 数量对比回归检测）。
- [x] 1.4 每个 issue 调 `/api/rules/show` 拿规则描述（introduction/root_cause → description，how_to_fix → suggestion），存进 `metadata`。
- [x] 1.5 随机打乱 → 按 project 的 `maxSonarIssuesPerRun` 截断。不做规则黑名单（随机选择天然分散）、不做 severity 排序（避免每次都选同一批最严重的老问题）。

## 2. AI Discovery 通道

- [x] 2.1 `docker exec claude --dangerously-skip-permissions --print "$(cat /tmp/prompt.txt)" --output-format json --json-schema "$(cat /tmp/schema.json)"` heredoc 写 prompt + schema 文件，后端从 `structured_output` 反序列化。
- [x] 2.2 解析 JSON → map to Issue（`AiIssue` BO Jackson 反序列化，type 从 `AiMetadata.Type` 枚举映射）。
- [x] ~~2.3 `ai-discovery.enabled` 开关~~ — 永不做。AI 通道始终开启，失败由外层 catch `ProcessExecutionException` 兜底使 ScanAction FAILED，不外挂开关。

## 3. 合并与入库

- [x] ~~3.1 合并去重~~ — 永不做。SonarQube 和 AI 发现的是不同类型问题（规则 vs 语义），重叠概率极低。
- [x] ~~3.2 跨 run 排除 FAILED 的 issue~~ — 永不做。当前阶段应验证修复能力，而非回避困难 issue。
- [x] 3.3 不同 runId 天然隔离，无需删除旧 issue → batch insert（status=`SELECTED`）。
- [x] 3.4 `ScanResult` 写入 `scannedSonarIssueNum` + `scannedAiIssueNum`。

## 4. 验证

- [x] 4.1 双通道各有产出，issue 真实入库，ScanResult 存有计数基线（`scannedSonarIssueNum`/`scannedAiIssueNum`）。
- [x] 4.2 联调端到端：SCAN → FIX → VERIFY → SUBMIT 全链路（8/13 端到端实测通过）。
