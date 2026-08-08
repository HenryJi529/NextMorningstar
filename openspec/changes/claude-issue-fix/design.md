## 上下文

修复时 Claude 不需要再通过 MCP 查外部知识库——ScanAction 阶段已把 SonarQube rule 描述和 AI 诊断全存进 `dev_issue` 的 `metadata` JSON 和顶层字段（`title`/`codeSnippet`/三列 severity）。FixAction 只读 issue 字段，不分 source。

## 目标 / 非目标

**目标:** Claude 读 issue 字段即修，统一 prompt；平台控制 commit；一漏洞一 commit；限时。
**非目标:** 不让 AI 自行 commit(平台控制,保证规范与可回滚)；不依赖 MCP 或外部 API。

## 决策

### 决策 1:统一 prompt，读 issue 字段即修

FixAction 对所有 issue 使用同一 prompt 模板，Claude 从 `title`/`codeSnippet`/`metadata` 中获取完整诊断上下文（description/suggestion/filePath），不需要 MCP 或外部 API。ScanAction 已在写入阶段存好了所有必要信息。

### 决策 2:commit 归临时 alpine/git 容器
claude 只改 named volume 文件,**临时 alpine/git 容器** `add`/`commit`(纯本地操作,不需凭证),保证一漏洞一 commit + 规范 message。

### 决策 3:双层超时
单 issue 限深度(`--max-turns`),整 run 限时(wall-clock + CancelTracker)。

### 决策 4:commit_message 由 AI 基于 issue 字段生成

Claude 已有 issue 的完整诊断信息（title/description），顺带用中文按 resources 模板写诊断 → 既客观又可以读。
