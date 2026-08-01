## 为什么

修复需以 PR 形式交付给人评审,且 AI 无合并权。演示用 Gitea(分支模式 + 合并删源分支),把所有已 `VERIFIED` 修复汇总到一个修复分支,开 PR 并附 AI 诊断报告与 sonar 深度链接。

## 变更内容

- `SubmitAction`:**后端命令行 git** 推修复分支 `ai-fix/<project>/<runShort>`。
- 调 Gitea API 开 PR(目标=源分支),启用"合并后自动删除源分支"。
- 用各 issue 的 ai_report 拼 markdown 评论(含 `/issues?open=`、`/coding_rules?open=` 链接)。
- 失败/取消/PR 关闭:主动删除修复分支(不留垃圾)。

## 能力

### 新增能力
- `pr-submit`:将已验证修复以 PR + 诊断报告交付评审,合并即清理分支。

## 影响范围

- `SubmitAction`:替换 Mock。
- `GiteaProperties`(origin/token/bot)。
