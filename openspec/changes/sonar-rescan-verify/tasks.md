## 1. 重扫裁判

- [ ] 1.1 `VerifyAction` 容器内 `mvn -q compile` + `sonar-scanner` 重扫。
- [ ] 1.2 调 API 查每个已修复 issue 的状态。

## 2. 结果处理

- [ ] 2.1 已关闭 → `status=VERIFIED`。
- [ ] 2.2 仍 OPEN → 后端命令行 git `revert <commit_sha>` + `status=FAILED`。

## 3. 验证

- [ ] 3.1 故意改坏代码,Verify 能捕获并 revert。
