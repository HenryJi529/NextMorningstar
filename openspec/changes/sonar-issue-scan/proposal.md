## 为什么

修复的输入是漏洞列表。需用 sonar-scanner 对同步下来的代码做分析(Java 需先编译产 class),再调 SonarQube API 拉取 OPEN issue,过滤密钥类规则后落库,作为后续修复的输入。

## 变更内容

- `ScanAction`:`mvn -q compile`(产 `target/classes`)→ `sonar-scanner` 推分析 → 调 `/api/issues/search` 拉 OPEN issue。
- 按 severity 排序,过规则黑名单(默认排除 `java:S2068`/`secrets:*` 凭据类;Hotspots 走独立 API 天然不返回),截断到 `maxFixesPerRun`。
- 落 `dev_issue`(issue_key/rule_key/severity/component/line/message)。
- 排除近期 FAILED 的 issue(查 `dev_issue` 历史,见 dev-plan 决策 14),避免每夜反复重试修不好的。

## 能力

### 新增能力
- `issue-scan`:从 SonarQube 获取并落库待修复漏洞,自动排除密钥类规则。

## 影响范围

- `ScanAction`:替换 Mock。
- `dev_issue` 写入。
