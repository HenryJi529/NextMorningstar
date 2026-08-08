## 1. 逐漏洞修复

- [ ] 1.1 `FixAction` 遍历 `SELECTED` issue。
- [ ] 1.2 容器内 claude headless + sonarqube MCP 修复,捕获输出 rule key。

## 2. 提交与回写

- [ ] 2.1 平台 `git add -A && git commit`(一漏洞一 commit,规范 message)。
- [ ] 2.2 回写 `dev_issue`(commit_sha/commit_message/status=`FIXED`)。
- [ ] 2.3 claude 基于 sonar 数据 + `resources/dev/ai-report-template.md` 生成中文 `commit_message`,回写 `dev_issue`。

## 3. 超时与取消

- [ ] 3.1 单 issue `--max-turns` 与整 run wall-clock 超时。
- [ ] 3.2 取消请求在安全点终止。

## 4. 验证

- [ ] 4.1 demo 仓库某 issue 被 claude 真修复并产生独立 commit。
