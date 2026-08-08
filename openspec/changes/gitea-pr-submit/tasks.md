## 1. PR 提交

- [ ] 1.1 临时 alpine/git 容器推修复分支 `fix/<runId>`(凭证 `http.extraHeader` 注入,用完即毁)。
- [ ] 1.2 调 Gitea API 开 PR(目标=源分支,启用合并后删源分支)。

## 2. 统一格式诊断评论

- [ ] 2.1 每条 issue 拼装一段:title + 三维 severity + metadata.description + metadata.suggestion + codeSnippet。
- [ ] 2.2 所有信息自包含于 issue 字段，不依赖外部链接。

## 3. 清理

- [ ] 3.1 失败/取消/PR 关闭时删除修复分支。

## 4. 验证

- [ ] 4.1 Gitea 出现含统一格式诊断报告的 PR;合并后分支自动删除。
