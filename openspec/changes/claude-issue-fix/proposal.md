## 为什么

这是流水线核心:让 AI 真正修复漏洞。FixAction 对所有 issue 使用统一 prompt——Claude 从 `dev_issue` 的字段（`title`/`codeSnippet`/`metadata`）中获取完整诊断上下文，不需要 MCP 或外部 API。ScanAction 已在写入阶段存好了所有必要信息。平台控制 commit，保证"一漏洞一 commit"且 commit 可控。

## 变更内容

- `FixAction`:遍历 `dev_issue`(status=`SELECTED`),逐个 `docker exec claude --dangerously-skip-permissions --print "..."` 修复。
- 统一 prompt:读 issue 字段（title/codeSnippet/metadata.description/metadata.suggestion/metadata.filePath），不区分 source。
- 临时 alpine/git 容器 `add -A && commit`(一漏洞一 commit,纯本地不需凭证),回写 `commit_sha`/status。
- **commit_message**:Claude 基于 issue 字段用**中文**按 `resources/dev/ai-report-template.md` 模板生成诊断,回写 `dev_issue.commit_message`(供 PR 评论)。
- 单 issue(`--max-turns`)与整 run(wall-clock)超时;复用 CancelTracker。

## 能力

### 新增能力
- `issue-fix`:由 AI 逐漏洞修复代码,统一 prompt 不依赖外部服务。

## 影响范围

- `FixAction`:替换 Mock。
- `dev_issue` 状态/commit/报告回写。
