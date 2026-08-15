## 0. 基础设施（已完成）

- [x] 0.1 `FixResult`：双计数 `fixedSonarIssueNum`/`fixedAiIssueNum`，注册 `@JsonSubTypes`。
- [x] 0.2 `Issue.CommitMessage`：`{subject, body}` + Lombok 注解（@Data/@NoArgsConstructor/@AllArgsConstructor/@Builder）。
- [x] 0.3 `CommitMessageTypeHandler`：`extends JsonTypeHandler<Issue.CommitMessage>`，@Component 注册。
- [x] 0.4 `Issue.Status` 删 `FAILED`（fail-fast，失败整轮回 SELECTED）。
- [x] 0.5 `FixAction` 骨架：继承 AbstractAction，5 依赖（SandboxProperties/RunMapper/IssueMapper/ProcessUtil/ObjectMapper），删 ProjectMapper（volumeName 直接 `run.getProjectId()`）。

## 1. 逐漏洞修复

- [x] 1.1 `FixAction` 切修复分支 `switch -C fix/<runId>`。
- [x] 1.2 遍历 `SELECTED` issue（LambdaQueryWrapper：runId + status=SELECTED）。
- [x] 1.3 统一 prompt（内嵌 text block）：读 issue 字段（title/codeSnippet/metadata.description/suggestion/filePath），Claude 修复代码，修复后调用 sonarqube MCP `analyze_code_snippet` 自查避免引入新 issue。
- [x] 1.4 提示词防跨文件回归（8/16）：编译自查（与后端 `mavenBuild` 同口径命令，无 pom 容忍）+ 波及面自查（改动涉及被引用声明时检索引用方文件同样 `analyze_code_snippet`，只查直接引用不扩大范围）。**重试反馈提示词方案评估后放弃**（同日）：曾实现"上轮 verify 失败的 key 清单喂回下轮 fix 提示词"，后删除——message 契约是给人排错、喂模型是受众错位；回滚后"新引入"问题的位置信息失效；自检 + 回退环重试随机性已够，偏门 case 靠流水记录事后研究。

## 2. 提交与回写

- [x] 2.1 临时 alpine/git 容器 `git add -A && git commit -m "subject" -m "body"`（一漏洞一 commit）+ `rev-parse HEAD` 取 commitSha。
- [x] 2.2 Claude 输出通过 `--json-schema` + `--output-format json`，后端从 `structured_output` 拆封 → `objectMapper` 反序列化成 `Issue.CommitMessage` → 两个 `-m` 分别传入 subject/body。
- [x] 2.3 回写 `dev_issue`（commitSha/commitMessage/status=`FIXED`）。
- [x] 2.4 按 source 累加 `FixResult.fixedSonarIssueNum`/`fixedAiIssueNum`。

## 3. 失败处理

- [x] 3.1 `RestoreAction` 还原 issue 状态（FIXED/VERIFIED → SELECTED，清 commitSha/commitMessage），LambdaUpdateWrapper 直接写在 RestoreAction。
- [x] 3.2 catch 补 `| JsonProcessingException`（D3 解析失败兜底；DataAccessException 不补，靠全局 CronTask）。
- [x] 3.3 失败 message 带卡住的 issue 信息 + 已修双计数。

## 4. 验证

- [x] 4.1 两种 source 的 issue 都能修掉并产出独立 commit。
