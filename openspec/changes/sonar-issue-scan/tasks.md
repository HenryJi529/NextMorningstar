## 1. SonarQube 通道

- [ ] 1.1 `ScanAction` 容器内 `mvn -q compile` 产 `target/classes`。
- [ ] 1.2 容器内 `sonar-scanner` 推分析到 SonarQube。
- [ ] 1.3 调 `/api/issues/search`(翻页取全量)拉 OPEN issue → 记录全部 issueKey 进 `ScanResult.issueKeys`（基线快照）。
- [ ] 1.4 每个 issue 调 `/api/rules/show` 拿规则描述，存进 `metadata.description`。
- [ ] 1.5 应用规则黑名单（密钥/凭据类）+ severity 排序 + 读 project 的 `maxSonarIssuesPerRun` 截断（不 fallback 全局）。

## 2. AI Discovery 通道

- [ ] 2.1 `docker exec claude --dangerously-skip-permissions --print "..."` 自由探索项目，输出结构化 JSON。
- [ ] 2.2 解析 JSON → map to Issue（aiMetadata.type 从 AiIssueType 枚举选）。
- [ ] 2.3 `ai-discovery.enabled` 开关控制，关闭时跳过此通道。

## 3. 合并与入库

- [ ] 3.1 合并去重（文件+行号重叠优先留 SonarQube）。
- [ ] 3.2 跨 run 排除 FAILED 的 issue（查 `dev_issue` 历史）。
- [ ] 3.3 删除本 run 旧 issue → batch insert（status=`SELECTED`）。
- [ ] 3.4 `ScanResult` 写入 `issueKeys` + `sonarIssueNum` + `aiIssueNum`。

## 4. 验证

- [ ] 4.1 双通道各有产出，issue 真实入库，ScanResult 存有基线快照。
- [ ] 4.2 `ai-discovery.enabled=false` 时回退纯 SonarQube 模式。
