## 上下文

修复流程中,容器承担 claude 改文件 + maven/sonar 构建;git 操作(含凭证)由后端命令行 git 在共享卷完成(见决策 12),凭证不进容器。多仓库并发靠容器隔离。

## 目标 / 非目标

**目标:** 每次运行一个独立容器(跑 claude + 构建);宿主机↔容器共享卷互通代码;镜像通用、配置可切换;**git 凭证不进容器**。
**非目标:** 不做容器资源池/复用;不在容器内执行 git;不实现 scan 等具体动作(后续 change)。

## 决策

### 决策 1:每 run 一容器
独立容器天然隔离并发,简单可靠;容器即用即删,无需池化。

### 决策 2:配置打进镜像 + 运行时 env 注入
`settings.json`/`mcp.json` 以**占位符模板**(`<DEEPSEEK_API_KEY>`/`<SONARQUBE_TOKEN>`)COPY 进镜像(无真 key,可安全分发);`entrypoint` 启动时用环境变量(`DEEPSEEK_API_KEY`/`SONARQUBE_TOKEN`)替换占位符。真 key 不进镜像、由后端 `docker run -e` 注入。**路径层级**:`settings.json` 用户级(`~/.claude/`,claude 全局读、不依赖 cwd);`mcp.json` **项目级**(`/workspace/.mcp.json`,claude 跟 cwd 走)。

### 决策 3:容器网络
容器内 sonar `127.0.0.1` 不可达宿主机,统一用 `host.docker.internal`。Mac(Docker Desktop)自动解析;**Linux 生产**需在 `docker run` 加 `--add-host=host.docker.internal:host-gateway`(原生 Docker 默认不提供该 DNS)。

### 决策 4:git 归后端、凭证不进容器
git clone/commit/push 由后端命令行 git 在共享卷执行,凭证(credential helper/环境变量)只存后端,AI 容器内无 git 凭证 → prompt injection 偷不到;submodule 用 `--recursive` 原生支持(规避 JGit 兼容坑)。容器无需装 git。
