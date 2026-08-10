## 0. 基础设施（已完成）

- [x] 0.1 `FixResult`：双计数 `fixedSonarIssueNum`/`fixedAiIssueNum`，注册 `@JsonSubTypes`。
- [x] 0.2 `Issue.CommitMessage`：`{subject, body}` + Lombok 注解（@Data/@NoArgsConstructor/@AllArgsConstructor/@Builder）。
- [x] 0.3 `CommitMessageTypeHandler`：`extends JsonTypeHandler<Issue.CommitMessage>`，@Component 注册。
- [x] 0.4 `Issue.Status` 删 `FAILED`（fail-fast，失败整轮回 SELECTED）。
- [x] 0.5 `FixAction` 骨架：继承 AbstractAction，5 依赖（SandboxProperties/RunMapper/IssueMapper/ProcessUtil/ObjectMapper），删 ProjectMapper（volumeName 直接 `run.getProjectId()`）。

## 1. 逐漏洞修复

- [ ] 1.1 `FixAction` 切修复分支 `switch -C fix/<runId>`。
- [ ] 1.2 遍历 `SELECTED` issue（LambdaQueryWrapper：runId + status=SELECTED）。
- [ ] 1.3 统一 prompt（内嵌 text block）：读 issue 字段（title/codeSnippet/metadata.description/suggestion/filePath），Claude 修复代码，不依赖 MCP。

## 2. 提交与回写

- [ ] 2.1 临时 alpine/git 容器 `git add -A && git commit -m "subject\n\nbody"`（一漏洞一 commit）+ `rev-parse HEAD` 取 commitSha。
- [ ] 2.2 Claude 输出 JSON `{subject, body}`，括号深度提取 `{...}` + `objectMapper` 反序列化成 `Issue.CommitMessage` → 拼 `subject\n\nbody`。
- [ ] 2.3 回写 `dev_issue`（commitSha/commitMessage/status=`FIXED`）。
- [ ] 2.4 按 source 累加 `FixResult.fixedSonarIssueNum`/`fixedAiIssueNum`。
- [ ] 2.5 `docker exec --user root chown -R bot:bot /workspace/repo`（属主修正）。

## 3. 失败处理

- [x] 3.1 `RestoreAction` 还原 issue 状态（FIXED/VERIFIED → SELECTED，清 commitSha/commitMessage），LambdaUpdateWrapper 直接写在 RestoreAction。
- [ ] 3.2 catch 补 `| JsonProcessingException`（D3 解析失败兜底；DataAccessException 不补，靠全局 CronTask）。
- [ ] 3.3 失败 message 带卡住的 issue 信息 + 已修双计数。

## 4. 验证

- [ ] 4.1 两种 source 的 issue 都能修掉并产出独立 commit。
