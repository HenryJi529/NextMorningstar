## 1. 扫描与分析

- [ ] 1.1 `ScanAction` 容器内 `mvn -q compile` 产 `target/classes`。
- [ ] 1.2 容器内 `sonar-scanner` 推分析到 SonarQube(`sonar.java.binaries` 等)。

## 2. 拉取与过滤

- [ ] 2.1 调 `/api/issues/search` 拉 OPEN issue,按 severity 排序。
- [ ] 2.2 应用规则黑名单(密钥/凭据类)与 `maxFixesPerRun` 截断。
- [ ] 2.3 落 `dev_issue`(status=`SELECTED`)。
- [ ] 2.4 排除近期 FAILED 的 issue(查 `dev_issue` 历史)。

## 3. 验证

- [ ] 3.1 真实仓库扫描后,`dev_issue` 出现预期 issue;密钥类被排除。
