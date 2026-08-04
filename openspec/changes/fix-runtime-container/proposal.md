## 为什么

真实修复需在隔离环境进行(隔离 AI + 提供构建环境),但 **git 凭证绝不能进 AI 容器**(防 prompt injection 偷取)。因此容器只承担"claude 改文件 + maven/sonar 构建",git 操作(含凭证)全部由后端命令行 git 在共享卷上完成。

## 变更内容

- 编写 `deploy/dev-fix-runtime.Dockerfile`:基于 JDK17 镜像,装入 maven/node/python/claude code CLI/sonar-scanner(**不装 git**——git 操作与凭证均归后端命令行,见决策 4)。
- 工作区以**共享卷**互通:宿主机 `~/dev-workspaces/<runId>/repo` ↔ 容器 `/workspace/<runId>/repo`;后端 git 与容器 claude/maven 操作同一份代码。
- 模型连接(`~/.claude/settings.json`)与 `.mcp.json` 运行时挂载,不写死镜像(外网 deepseek ↔ 内网模型可切换)。
- `StartAction` 创建宿主机工作区目录并启动容器挂载之;`CleanAction` 删除容器。
- 容器内 spike 验证 `claude -p` + sonarqube MCP 可用;sonar 地址用 `host.docker.internal`。
- `dev_run.container_id` 记录运行时容器映射。

## 能力

### 新增能力
- `fix-runtime`:为每次运行提供隔离、可驱动、可清理的标准化修复容器。

## 影响范围

- `deploy/dev-fix-runtime.Dockerfile`(新增)。
- `StartAction`/`CleanAction`:替换 Mock 实现。
- `dev_run`:启用 `container_id`。
