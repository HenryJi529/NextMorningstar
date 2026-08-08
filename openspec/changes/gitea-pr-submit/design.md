## 上下文

内网生产用 GitLab,演示用 Gitea;不做抽象层(见 dev-plan 决策 11)。

## 目标 / 非目标

**目标:** 演示期实现 Gitea 分支模式 PR + 诊断评论 + 自动清理。
**非目标:** 不实现 GitLab(内网部署阶段再做);不做跨平台抽象。

## 决策

### 决策 1:分支模式 + auto-delete
原仓库内开修复分支提 PR;合并/关闭后自动删源分支,平时仓库只见 main。失败主动删分支兜底。

### 决策 2:诊断报告即 PR 评论
复用各 issue 的 commit_message,拼含 sonar issue/rule 深度链接的 markdown,人 review 时可一键直达。
