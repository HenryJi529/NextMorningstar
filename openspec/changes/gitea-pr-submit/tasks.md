## 1. PR 提交

- [ ] 1.1 临时 alpine/git 容器推修复分支 `fix/<runId>`(凭证 `http.extraHeader` 注入,用完即毁)。
- [ ] 1.2 调 Gitea API 开 PR(目标=源分支,启用合并后删源分支)。

## 2. 诊断评论

- [ ] 2.1 拼装 markdown 评论(修复清单 + AI 诊断 + sonar 深度链接)。

## 3. 清理

- [ ] 3.1 失败/取消/PR 关闭时删除修复分支。

## 4. 验证

- [ ] 4.1 Gitea 出现带诊断报告与 sonar 链接的 PR;合并后分支自动删除。
