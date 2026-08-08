## 1. 逐漏洞修复

- [ ] 1.1 `FixAction` 遍历 `SELECTED` issue。
- [ ] 1.2 统一 prompt：读 issue 字段（title/codeSnippet/metadata.description/metadata.suggestion/metadata.filePath），Claude 修复代码，不依赖 MCP。

## 2. 提交与回写

- [ ] 2.1 平台 `git add -A && git commit`(一漏洞一 commit,规范 message)。
- [ ] 2.2 回写 `dev_issue`(commit_sha/commit_message/status=`FIXED`)。
- [ ] 2.3 Claude 基于 issue 字段 + `resources/dev/ai-report-template.md` 生成中文 `commit_message`,回写 `dev_issue`。

## 3. 超时与取消

- [ ] 3.1 单 issue `--max-turns` 与整 run wall-clock 超时。
- [ ] 3.2 取消请求在安全点终止。

## 4. 验证

- [ ] 4.1 两种 source 的 issue 都能修掉并产出独立 commit。
