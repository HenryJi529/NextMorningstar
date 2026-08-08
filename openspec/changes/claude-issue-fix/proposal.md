## 为什么

这是流水线核心:让 AI 真正修复漏洞。采用 claude code CLI(headless)经 sonarqube MCP 自查 issue 与 rule 后修复,平台只喂 issueId 并控制 commit,保证"一漏洞一 commit"且 commit 可控。

## 变更内容

- `FixAction`:遍历 `dev_issue`(status=`SELECTED`),逐个 `docker exec claude --dangerously-skip-permissions --print "..."` 修复。
- prompt 让 claude 经 sonarqube MCP 查 issue+rule 后修复,仅输出 rule key。
- 临时 alpine/git 容器 `add -A && commit`(一漏洞一 commit,纯本地不需凭证),回写 `commit_sha`/status。
- **commit_message**:claude 基于 SonarQube 数据(rule/issue)用**中文**按 `resources/dev/ai-report-template.md` 模板生成诊断,回写 `dev_issue.commit_message`(供 PR 评论)。
- 单 issue(`--max-turns`)与整 run(wall-clock)超时;复用 CancelTracker。

## 能力

### 新增能力
- `issue-fix`:由 AI 逐漏洞修复代码,产出一漏洞一 commit。

## 影响范围

- `FixAction`:替换 Mock。
- `dev_issue` 状态/commit/报告回写。
