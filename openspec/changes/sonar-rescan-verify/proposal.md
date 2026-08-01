## 为什么

需客观判定 AI 修复是否真正消除漏洞。重新编译并扫描后,若该 issue 在 SonarQube 已关闭即视为修复成功;仍 OPEN 则回滚该 commit。SonarQube 既是出题人又是阅卷人。

## 变更内容

- `VerifyAction`:容器内 `mvn -q compile` → `sonar-scanner` 重扫 → 调 `/api/issues/search?issues=<id>` 查状态。
- 已关闭 → `dev_issue.status=VERIFIED`;仍 OPEN → `git revert <commit>` + `status=FAILED`。

## 能力

### 新增能力
- `issue-verify`:用 SonarQube 重扫客观判定修复有效性,失败自动回滚。

## 影响范围

- `VerifyAction`:替换 Mock。
- `dev_issue` 状态更新。
