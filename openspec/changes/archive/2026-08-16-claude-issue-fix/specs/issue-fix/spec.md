# issue-fix 规格

## 目的

由 AI 逐漏洞修复代码,产出一漏洞一 commit。

## 需求

### 需求:逐漏洞修复并提交

#### 场景:修复单个漏洞

- **WHEN** 修复阶段处理一个 `SELECTED` issue
- **THEN** AI 基于 issue 字段（title/codeSnippet/metadata）修改代码，修复后调用 sonarqube MCP `analyze_code_snippet` 自查修改文件确保不引入新 issue，平台为该修复产生一个独立 commit
- **AND** 修复完成后执行 `mvn compile` 自查项目整体编译（编译错误是最常见的跨文件回归，Maven 项目须在输出前消除，无 pom 容忍跳过）
- **AND** 改动涉及方法签名/类名/字段等被引用声明时，用检索工具圈定引用方文件并同样 `analyze_code_snippet` 自查（只查直接引用，不扩大检索范围）
- **AND** 回写该 issue 的 commitSha、commitMessage（JSON {subject, body}）与 status=FIXED

#### 场景:修复失败

- **WHEN** 任一 issue 的 AI 输出 JSON 解析失败（JsonProcessingException）或 git 操作失败（ProcessExecutionException）
- **THEN** 整轮 FIX_FAILED → RestoreAction 整轮回退（git 7 步 + issue 回 SELECTED），不做逐 issue 部分保留
