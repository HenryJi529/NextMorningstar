## 为什么

修复需以 PR 形式交付给人评审,且 AI 无合并权。演示用 Gitea(分支模式 + 合并删源分支),把所有已 `VERIFIED` 修复汇总到一个修复分支,开 PR 并附统一格式诊断报告。SonarQube 对用户透明——issue 入库后不再区分来源，一切信息自包含在 issue 字段中。

## 变更内容

- `SubmitAction`:临时 alpine/git 容器推修复分支 `fix/<runId>`(凭证 `http.extraHeader` 注入,用完即毁)。
- 调 Gitea API 开 PR(head=`fix/<runId>`, base=源分支, title + body)。
- 统一格式诊断报告直接作为 **PR body**（不再单独发 comment）:每条 issue 一段，Sonar/AI 分分支——title + 三维 severity + 代码片段链接(跳转源码对应行) + 修改记录链接(跳转 commit)，AI 分支额外 type/description。不依赖外部 SonarQube 链接。
- ~~失败/取消/PR 关闭:主动删除修复分支~~ → **决定不做**(8/13)：删除分支危险（不可逆、误删风险）且越界，平台只观测不删除；残留分支名带 runId 唯一不冲突，由人工在 Gitea 手动清理。

## 能力

### 新增能力
- `pr-submit`:将已验证修复以 PR + 统一格式诊断报告交付评审。

## 影响范围

- `SubmitAction`:替换 Mock。
- `GiteaUtil`:新增 `createPullRequest` + `getCommitLink`/`getFileLink`/`getCodeSnippetLink` + `PullRequest` DTO。
- `SubmitResult`:新增（prUrl/prTitle/prBody）。
- `GiteaProperties` 复用 `backendOrigin`/`botToken`。
