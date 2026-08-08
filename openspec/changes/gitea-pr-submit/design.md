## 上下文

内网生产用 GitLab,演示用 Gitea;不做抽象层(见 dev-plan 决策 11)。SonarQube 对用户透明——issue 入库后不再区分 source，PR 评论统一用 issue 自带字段，不依赖外部链接。

## 目标 / 非目标

**目标:** 演示期实现 Gitea 分支模式 PR + 统一格式诊断评论 + 自动清理。
**非目标:** 不实现 GitLab(内网部署阶段再做);不做跨平台抽象;不展示 SonarQube 原始数据。

## 决策

### 决策 1:分支模式 + auto-delete
原仓库内开修复分支提 PR;合并/关闭后自动删源分支,平时仓库只见 main。失败主动删分支兜底。

### 决策 2:统一格式 PR 评论

每条 issue 一段，格式统一：
- **title**：问题标题
- **三维 severity**：reliability/security/maintainability
- **metadata.description**：问题诊断
- **metadata.suggestion**：修复建议
- **codeSnippet**：问题代码片段

所有信息自包含于 issue 字段，不区分 source，不依赖 SonarQube 外部链接。
