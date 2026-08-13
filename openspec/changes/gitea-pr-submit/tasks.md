## 1. PR 提交

- [x] 1.1 临时 alpine/git 容器推修复分支 `fix/<runId>`(凭证 `http.extraHeader` 注入,用完即毁)。
- [x] 1.2 调 Gitea API 开 PR(head=`fix/<runId>`, base=源分支, title + body)。

## 2. 统一格式诊断报告（作为 PR body）

- [x] 2.1 每条 issue 一段，Sonar/AI 分分支拼装：title + 三维 severity + 代码片段链接(跳转源码对应行 `getCodeSnippetLink`) + 修改记录链接(跳转 commit `getCommitLink`)；AI 分支额外展示 type + description。
- [x] 2.2 诊断报告直接作为 PR body（不再单独发 comment），所有信息自包含于 issue 字段。

## 3. 清理

- [x] 3.1 失败/取消/PR 关闭时删除修复分支 —— **决定不做**(8/13)：删除分支是危险操作（不可逆、误删风险），且分支管理属仓库所有者职责，平台只观测不删除；残留分支名带 runId 唯一不冲突，由人工在 Gitea 手动清理。

## 4. 验证

- [x] 4.1 Gitea 出现含统一格式诊断报告的 PR（8/13 实测通过，端到端从启动流水线到 PR 落成）。
