## 上下文

内网生产用 GitLab,演示用 Gitea;不做抽象层(见 dev-plan 决策 11)。SonarQube 对用户透明——issue 入库后不再区分 source，PR body 统一用 issue 自带字段，不依赖外部链接。

## 目标 / 非目标

**目标:** 演示期实现 Gitea 分支模式 PR + 统一格式诊断报告(作为 PR body) + 回填 Run.prId/prStatus。
**非目标:** 不实现 GitLab(内网部署阶段再做);不做跨平台抽象;不展示 SonarQube 原始数据。

## 决策

### 决策 1:分支模式
原仓库内开修复分支提 PR。~~合并/关闭后自动删源分支 + 失败主动删分支兜底~~ → **决定不做**(8/13)：删除分支是危险操作（不可逆、误删风险），且分支管理属仓库所有者职责，平台只观测不删除；残留分支名带 runId 唯一不冲突，由人工在 Gitea 手动清理。

### 决策 2:统一格式诊断报告（PR body，非 comment）

诊断报告直接作为 PR body（8/13 决定不再单独发 comment——PR description 本就支持 markdown，省一步 API 调用）。每条 issue 一段，Sonar/AI 分分支：

- **title**：问题标题（Sonar 为英文 message，AI 为中文）
- **三维 severity**：reliability/security/maintainability（null 显示 N/A）
- **代码片段链接**：`getCodeSnippetLink` 跳转源码对应行（配合 startLine/endLine），不贴代码块
- **修改记录链接**：`getCommitLink` 跳转 commit
- **AI 分支额外**：type（问题类型）+ description（问题描述）

Sonar 分支不展示 description/suggestion（其为 HTML 规则文档、又长又泛化，title 已具体到代码）；AI 分支展示 description。所有信息自包含于 issue 字段，不依赖 SonarQube 外部链接。
