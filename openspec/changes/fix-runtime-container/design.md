## 上下文

修复流程中,容器承担 claude 改文件 + maven/sonar 构建;git 操作(含凭证)由后端命令行 git 在共享卷完成(见决策 12),凭证不进容器。多仓库并发靠容器隔离。

## 目标 / 非目标

**目标:** 每次运行一个独立容器(跑 claude + 构建);宿主机↔容器共享卷互通代码;镜像通用、配置可切换;**git 凭证不进容器**。
**非目标:** 不做容器资源池/复用;不在容器内执行 git;不实现 scan 等具体动作(后续 change)。

## 决策

### 决策 1:每 run 一容器
独立容器天然隔离并发,简单可靠;容器即用即删,无需池化。

### 决策 2:配置运行时挂载
`settings.json`/`.mcp.json` 挂载而非写入镜像,使外网(deepseek)与内网(行内模型)共用一个镜像。

### 决策 3:容器网络
容器内 sonar `127.0.0.1` 不可达宿主机,统一用 `host.docker.internal`。

### 决策 4:git 归后端、凭证不进容器
git clone/commit/push 由后端命令行 git 在共享卷执行,凭证(credential helper/环境变量)只存后端,AI 容器内无 git 凭证 → prompt injection 偷不到;submodule 用 `--recursive` 原生支持(规避 JGit 兼容坑)。容器无需装 git。
