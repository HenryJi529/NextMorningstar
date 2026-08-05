## 为什么

真实修复需在隔离环境进行(隔离 AI + 提供构建环境),但 **git 凭证绝不能进 AI 容器**(防 prompt injection 偷取)。因此容器只承担"claude 改文件 + maven/sonar 构建",git 操作(含凭证)全部由后端通过临时 alpine/git 容器在 named volume 上完成(决策 9/12)。

## 变更内容

- 编写 `deploy/dev-sandbox/Dockerfile`:基于 JDK17 镜像,装入 maven/node/python/claude code CLI/sonar-scanner(**不装 git**——git 操作与凭证均归后端命令行,见决策 4)。
- 工作区用 **named volume**(`docker volume create ws-<projectId>`):容器以非 root bot 运行,named volume 保留镜像目录属主无 UID 对齐问题;`StartAction` 创建 volume 并挂载到 `/workspace`,`CleanAction` 只删容器不删 volume(volume 持久化为项目缓存,决策 10)。后端 git 通过临时 alpine 容器操作 volume(非 bind mount,无需宿主路径)。
- 模型连接(`/home/bot/.claude/settings.json`)与 `mcp.json` 以**占位符模板打进镜像**(`<DEEPSEEK_API_KEY>`/`<SONARQUBE_TOKEN>`),`entrypoint` 启动时用环境变量替换真 key(不挂载文件、真 key 不进镜像);外网 deepseek ↔ 内网模型仅注入 key 不同,共用一镜像。
- `StartAction`:创建 named volume + 启动容器(以 bot 用户运行),注入 env + `--add-host`;`CleanAction`:删容器,**不删 volume**(决策 10)。
- 容器内 spike 验证 `claude -p` + sonarqube MCP 可用;sonar 地址用 `host.docker.internal`。
- `dev_run.container_id` 记录运行时容器映射。

## 能力

### 新增能力
- `fix-runtime`:为每次运行提供隔离、可驱动、可清理的标准化修复容器。

## 影响范围

- `deploy/dev-sandbox/Dockerfile`(新增)。
- `deploy/dev-sandbox/entrypoint.sh`(新增,运行时用 env 替换占位符)。
- `deploy/dev-sandbox/config/claude/{settings.json,mcp.json}`(新增,占位符模板)。
- `StartAction`/`CleanAction`:替换 Mock,docker 操作走 `ProcessRunner`(决策 6)。
- `util/ProcessRunner`(新增,`ProcessBuilder` 封装,docker/git 共用)。
- `properties/SandboxProperties`、`properties/DeepseekProperties`(新增,决策 7);`application-app.yml` 补 `dev.sandbox`/`dev.deepseek` 段。
- `dev_run`:启用 `container_id`。
