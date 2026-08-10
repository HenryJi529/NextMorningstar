# issue-fix 规格

## 目的

由 AI 逐漏洞修复代码,产出一漏洞一 commit。

## 需求

### 需求:逐漏洞修复并提交

#### 场景:修复单个漏洞

- **WHEN** 修复阶段处理一个 `SELECTED` issue
- **THEN** AI 基于 issue 字段（title/codeSnippet/metadata）修改代码，平台为该修复产生一个独立 commit
- **AND** 回写该 issue 的 commitSha、commitMessage（JSON {subject, body}）与 status=FIXED

#### 场景:修复失败

- **WHEN** 任一 issue 的 AI 输出 JSON 解析失败（JsonProcessingException）或 git 操作失败（ProcessExecutionException）
- **THEN** 整轮 FIX_FAILED → RestoreAction 整轮回退（git 7 步 + issue 回 SELECTED），不做逐 issue 部分保留
