## 上下文

修复需 AI 阅读 rule 详情与 issue flow 后改代码;claude code CLI 经 sonarqube MCP 可自助获取这些上下文。

## 目标 / 非目标

**目标:** claude 自查自修,平台控制 commit;一漏洞一 commit;限时。
**非目标:** 不让 AI 自行 commit(平台控制,保证规范与可回滚);不调平台侧 SonarQube API 解析 issue 详情(交给 claude+MCP)。

## 决策

### 决策 1:claude+MCP 自查自修
prompt 极简(喂 issueId),claude 经 MCP 取 rule/flow 后修复,平台无需解析 issue 详情。

### 决策 2:commit 归临时 alpine/git 容器
claude 只改 named volume 文件,**临时 alpine/git 容器** `add`/`commit`(纯本地操作,不需凭证),保证一漏洞一 commit + 规范 message。

### 决策 3:双层超时
单 issue 限深度(`--max-turns`),整 run 限时(wall-clock + CancelTracker)。

### 决策 4:commit_message 由 AI 基于 sonar 数据生成
claude 经 MCP 已拿到 rule/issue 客观数据,顺带用中文按 resources 模板写诊断 → 既客观(基于 sonar 数据)又可读(中文 + 模板),避免直接套用英文 rule 描述。
