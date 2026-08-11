## 1. 第一道防线：SonarQube 重扫

- [x] 1.1 `VerifyAction` 容器内 `mvn -q compile` + `sonar-scanner` 重扫（复用 `CommonSteps.mavenBuild`/`sonarScan`）。
- [x] 1.2 数量对比检测回归：`currentIssueNum > scannedSonarIssueNum - fixedSonarIssueNum` → 存在未修复或引入新 issue。

## 2. 第二道防线：Claude 语义验证

- [x] 2.1 读原始 issue 字段（title/metadata）+ `commitMessage`（修复思路）；**不喂 diff**，Claude 自己读取修复后代码。
- [x] 2.2 Claude 逐条两维度判定（思路正确性 + 实现到位），输出 `{"verified":true/false}`，任一 false 短路。

## 3. 结果处理

- [x] 3.1 两道都通过 → 所有 `FIXED` issue `status=VERIFIED`。
- [x] 3.2 任一失败 → 整轮 `VERIFY_FAILED` → RestoreAction 整轮回退（issue 回 `SELECTED`，**不标 FAILED**，决策 31）。

## 4. 验证

- [x] 4.1 故意改坏代码，Verify 两道防线各自能捕获（8/12 实测通过）。
- [x] 4.2 回归检测：修复引入新 in-scope issue 被判定 FAILED（8/12 实测通过）。
