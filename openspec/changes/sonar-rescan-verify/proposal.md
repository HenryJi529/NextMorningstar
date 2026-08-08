## 为什么

需客观判定 AI 修复是否真正消除漏洞。重新编译并扫描后,若该 issue 在 SonarQube 已关闭即视为修复成功;仍 OPEN 则整轮回退。SonarQube 既是出题人又是阅卷人。

## 变更内容

- `VerifyAction`:容器内 `mvn -q compile` → `sonar-scanner` 重扫 → 调 `/api/issues/search?issues=<id>` 查状态。
- 全部已关闭 → 所有 issue `status=VERIFIED`。
- 存在 OPEN → 所有 issue `status=FAILED` → 状态机驱动 RestoreAction 完整还原。
- MVP 不做逐 commit 保留（理由见 fix-runtime-container 决策 18）。

## 能力

### 新增能力
- `issue-verify`:用 SonarQube 重扫客观判定修复有效性,失败自动整轮回退。

## 影响范围

- `VerifyAction`:替换 Mock。
- `dev_issue` 状态更新。
