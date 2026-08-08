## 1. 第一道防线：SonarQube 重扫

- [ ] 1.1 `VerifyAction` 容器内 `mvn -q compile` + `sonar-scanner` 重扫。
- [ ] 1.2 调 API 查本 run SELECTED issue 是否全 CLOSED。
- [ ] 1.3 对比 `ScanResult.issueKeys` 基线，判断回归（新出现的 issue）。

## 2. 第二道防线：Claude 语义验证

- [ ] 2.1 读 fix diff + 原始 issue 字段（title/codeSnippet/metadata）。
- [ ] 2.2 Claude 判定修复是否正确（PASSED/FAILED）。

## 3. 结果处理

- [ ] 3.1 两道都通过 → 所有 issue `status=VERIFIED`。
- [ ] 3.2 任一失败 → 所有 issue `status=FAILED` → 状态机发 `VERIFY_FAILED` → RestoreAction 整轮回退。

## 4. 验证

- [ ] 4.1 故意改坏代码，Verify 两道防线各自能捕获。
- [ ] 4.2 回归检测：修复引入新 BLOCKER/CRITICAL 被判定 FAILED。
