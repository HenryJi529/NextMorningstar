## 为什么

修复需以 PR 形式交付给人评审,且 AI 无合并权。演示用 Gitea(分支模式 + 合并删源分支),把所有已 `VERIFIED` 修复汇总到一个修复分支,开 PR 并附统一格式诊断报告。SonarQube 对用户透明——issue 入库后不再区分来源，一切信息自包含在 issue 字段中。

## 变更内容

- `SubmitAction`:临时 alpine/git 容器推修复分支 `fix/<runId>`(凭证 `http.extraHeader` 注入,用完即毁)。
- 调 Gitea API 开 PR(目标=源分支),启用"合并后自动删除源分支"。
- 统一格式 PR 评论:每条 issue 展示 title + 三维 severity + metadata.description + metadata.suggestion + codeSnippet，不依赖外部 SonarQube 链接。
- 失败/取消/PR 关闭:主动删除修复分支(不留垃圾)。

## 能力

### 新增能力
- `pr-submit`:将已验证修复以 PR + 统一格式诊断报告交付评审,合并即清理分支。

## 影响范围

- `SubmitAction`:替换 Mock。
- `GiteaProperties`(publicOrigin/token/bot)。
