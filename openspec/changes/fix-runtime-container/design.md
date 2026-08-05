## 上下文

修复流程中,容器承担 claude 改文件 + maven/sonar 构建;git 操作(含凭证)由后端通过临时 alpine/git 容器在 named volume 完成(见决策 12),凭证不进容器。多仓库并发靠容器隔离。

## 目标 / 非目标

**目标:** 每次运行一个独立容器(跑 claude + 构建);named volume 互通代码;镜像通用、配置可切换;**git 凭证不进容器**。
**非目标:** 不做容器资源池/复用;不在容器内执行 git;不实现 scan 等具体动作(后续 change)。

## 决策

### 决策 1:每 run 一容器
独立容器天然隔离并发,简单可靠;容器即用即删,无需池化。

### 决策 2:配置打进镜像 + 运行时 env 注入
`settings.json`/`mcp.json` 以**占位符模板**(`<DEEPSEEK_API_KEY>`/`<SONARQUBE_TOKEN>`)COPY 进镜像(无真 key,可安全分发);`entrypoint` 启动时用环境变量(`DEEPSEEK_API_KEY`/`SONARQUBE_TOKEN`)替换占位符。真 key 不进镜像、由后端 `docker run -e` 注入。**路径层级**:`settings.json` 用户级(`~/.claude/`,claude 全局读、不依赖 cwd);`mcp.json` **项目级**(`/workspace/.mcp.json`,claude 跟 cwd 走)。

### 决策 3:容器网络
容器内 sonar `127.0.0.1` 不可达宿主机,统一用 `host.docker.internal`。Mac(Docker Desktop)自动解析;**Linux 生产**需在 `docker run` 加 `--add-host=host.docker.internal:host-gateway`(原生 Docker 默认不提供该 DNS)。

### 决策 4:git 归临时容器、凭证不进 AI 容器

git clone/commit/push 由后端通过 `docker run --rm -v ws-<projectId>:/workspace` 起临时 alpine/git 容器执行,凭证以 `-e GIT_TOKEN=...` 注入,用完即毁。AI 容器(dev-sandbox)内无 git 凭证 → prompt injection 偷不到。submodule 用 `--recursive` 原生支持(规避 JGit 兼容坑)。与决策 9(named volume)统一:后端不存代码、不碰文件系统,纯编排。

### 决策 5:镜像跨架构构建(sonar-scanner 按 `TARGETARCH` 选包)
开发者本地 Apple Silicon(arm64)、部署服务器 amd64。sonar-scanner 官方按架构分包(`linux-x64`/`linux-aarch64`),无统一包、官方 Docker 镜像仅 amd64。Dockerfile 用 BuildKit 自动注入的 `TARGETARCH` 选包(`amd64→x64`/`arm64→aarch64`),两边**原生构建**,不依赖 Rosetta——arm64 容器跑 x64 二进制会触发 `rosetta error: failed to open elf at /lib64/ld-linux-x86-64.so.2` → SIGTRAP(exit 133)。

### 决策 6:容器操作走命令行(ProcessBuilder + docker CLI)
`StartAction`/`CleanAction` 的 docker 操作走 `ProcessBuilder` 调 `docker` CLI(`docker run -d`/`docker rm -f`),不引 docker-java 库——与决策 12 的命令行 git 同套,共用 `util/ProcessRunner`(执行 + 捕获 stdout + 异常)。命令即文档、调试直观、零新依赖。前提:后端进程能访问 docker(开发期 Mac 宿主机有 docker CLI ✅;生产后端容器化部署需挂 docker.sock,8/14 前不处理)。

### 决策 7:模型配置独立分域(`DeepseekProperties`)
dev 流水线的模型 key 单独 `DeepseekProperties`(prefix `morningstar.app.dev.deepseek`),与 blog 模块的 `spring.ai.deepseek`(spring-ai ChatClient 在用,见 `AiConfig`/`ArticleServiceImpl`)**分域**——两者同源(`${DEEPSEEK_API_KEY}`)但配置路径分开,避免混用 `spring.ai.deepseek` 致语义不清。`morningstar.app.dev.*` 下三个配置类平级:`SandboxProperties`(镜像/工作区)、`DeepseekProperties`(模型 key)、`SonarqubeProperties`(sonar token)。

### 决策 8:容器内以非 root 用户运行 bot

Claude Code CLI 在 root/sudo 下禁止 `--dangerously-skip-permissions`。Dockerfile 创建 `bot` 用户并 `USER bot`,容器所有进程(entrypoint/sleep/`docker exec`)均以 bot 身份运行;`~/.claude` 与 `/workspace` 均 `chown` 给 bot。

### 决策 9:workspace 使用 named volume(`docker volume`)

容器以非 root 运行时,bind mount 宿主目录会导致 UID 不对齐(容器内 bot ≠ 宿主机用户),导致无写权限。改用 named volume(`docker volume create ws-<projectId>`,启动时 `-v ws-<projectId>:/workspace`):Docker 自动从镜像复制目录结构并保留属主,UID 天然正确。

### 决策 10:volume 持久化作项目级本地缓存

volume 命名从 `ws-<runId>` 改为 `ws-<projectId>`,绑定项目生命周期(非 run)。SyncAction 首次 clone,后续 run 只做 `git fetch + reset --hard` 增量更新(几秒,避免每次全量 clone)。CleanAction 只删容器,**不删 volume**(`docker volume rm` 仅在项目删除时触发)。效果:volume 成为项目级代码缓存,大幅减少网络 I/O 和时间。
