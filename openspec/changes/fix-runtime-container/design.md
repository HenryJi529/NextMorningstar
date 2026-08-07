## 上下文

修复流程中,容器承担 claude 改文件 + maven/sonar 构建;git 操作(含凭证)由后端通过临时 alpine/git 容器在 named volume 完成(见决策 12),凭证不进容器。多仓库并发靠容器隔离。

## 目标 / 非目标

**目标:** 每次运行一个独立容器(跑 claude + 构建);named volume 互通代码;镜像通用、配置可切换;**git 凭证不进容器**。
**非目标:** 不做容器资源池/复用;不在容器内执行 git;不实现 scan 等具体动作(后续 change)。

## 决策

### 决策 1:每 run 一容器
独立容器天然隔离并发,简单可靠;容器即用即删,无需池化。

### 决策 2:配置打进镜像 + 运行时 env 注入
`settings.json`/`mcp.json` 以**占位符模板**(`<MODEL_API_KEY>`/`<SONARQUBE_TOKEN>`)COPY 进镜像(无真 key,可安全分发);`entrypoint` 启动时用环境变量(`MODEL_API_KEY`/`SONARQUBE_TOKEN`)替换占位符。真 key 不进镜像、由后端 `docker run -e` 注入。**路径层级**:`settings.json` 用户级(`~/.claude/`,claude 全局读、不依赖 cwd);`mcp.json` **项目级**(`/workspace/.mcp.json`,claude 跟 cwd 走)。volume 挂载点是 `/workspace/repo`(决策 9),`.mcp.json` 留在镜像层——每次起容器都是新鲜占位符副本,entrypoint 每次用当前 env 替换(token 轮换即换即生效,真 key 不滞留 volume)。

### 决策 3:容器网络
容器内 sonar `127.0.0.1` 不可达宿主机,统一用 `host.docker.internal`。Mac(Docker Desktop)自动解析;**Linux 生产**需在 `docker run` 加 `--add-host=host.docker.internal:host-gateway`(原生 Docker 默认不提供该 DNS)。

### 决策 4:git 归临时容器、凭证不进 AI 容器

git clone/commit/push 由后端通过 `docker run --rm -v morningstar_dev_repo_<projectId>:/workspace/repo` 起临时 alpine/git 容器执行(**alpine/git 镜像名写死、不进配置**——工具镜像,不与 sandbox 镜像同一生命周期),用完即毁。clone/fetch URL 一律**无凭证形式**(host/owner/repo.git),token 通过 `git -c http.extraHeader=Authorization: token <value>` 直接拼入命令参数(Java 字符串拼接,当次生效,不需要 `-e` 环境变量),**不拼进 remote URL**(token 进 URL 会被 git 原样写入 volume 里 `.git/config`,持久化泄露)。AI 容器(dev-sandbox)内无 git 凭证 → prompt injection 偷不到。**MVP 不处理子模块**(clone 无 `--recursive`,增量无 `submodule update`),后续独立任务补。与决策 9(named volume)统一:后端不存代码、不碰文件系统,纯编排。

### 决策 5:镜像跨架构构建(sonar-scanner 按 `TARGETARCH` 选包)
开发者本地 Apple Silicon(arm64)、部署服务器 amd64。sonar-scanner 官方按架构分包(`linux-x64`/`linux-aarch64`),无统一包、官方 Docker 镜像仅 amd64。Dockerfile 用 BuildKit 自动注入的 `TARGETARCH` 选包(`amd64→x64`/`arm64→aarch64`),两边**原生构建**,不依赖 Rosetta——arm64 容器跑 x64 二进制会触发 `rosetta error: failed to open elf at /lib64/ld-linux-x86-64.so.2` → SIGTRAP(exit 133)。

### 决策 6:容器操作走命令行(ProcessBuilder + docker CLI)
`StartAction`/`CleanAction` 的 docker 操作走 `ProcessBuilder` 调 `docker` CLI(`docker run -d`/`docker rm -f`),不引 docker-java 库——与决策 12 的命令行 git 同套,共用 `util/ProcessUtil`(执行 + 捕获 stdout + 异常,8/6 已实现,6 单测)。命令即文档、调试直观、零新依赖。前提:后端进程能访问 docker(开发期 Mac 宿主机有 docker CLI ✅;生产后端容器化部署需挂 docker.sock,8/14 前不处理)。

**ProcessUtil 实现要点(8/6 对话定稿):**
- `run(String...)`:执行命令,完整命令打 INFO 日志;返回 stdout——**仅剥末尾换行(`\R+$`),其余空白原样保留**(容器 ID 直接可用,调用方无需 trim;带格式输出不被误伤)。
- 非零退出抛**静态嵌套异常** `ProcessUtil.ProcessExecutionException`(含命令 + 退出码 + 完整 stderr;嵌套理由:异常语义完全依附 `run()`,与 statemachine 领域异常的独立文件风格区分)。启动失败/中断同样包装。
- **stderr 独立线程读**,防 >64KB 管道缓冲区死锁(守护测试以 200KB stderr 验证:能跑完 = 不死锁、消息长度 >200KB = 无截断)。
- 测试为纯 JUnit 单测(无 Spring 上下文,`dev/util/ProcessUtilTest`)。
- **日志策略**:打完整命令(含 `-e` 注入的 env)可接受——GIT_TOKEN 等是**系统自有凭证**而非用户敏感信息,且注入的是不跑 AI 的临时容器(决策 4 防的 prompt injection 路径不存在);后端日志在安全边界内。

### 决策 7:模型配置独立分域(`ClaudeCodeProperties`)
dev 流水线的模型 key 单独 `ClaudeCodeProperties`(prefix `morningstar.app.dev.claude-code`,字段 `modelApiKey`),与 blog 模块的 `spring.ai.deepseek`(spring-ai ChatClient 在用,见 `AiConfig`/`ArticleServiceImpl`)**分域**——两者同源(`${MODEL_API_KEY}`)但配置路径分开,避免混用 `spring.ai.deepseek` 致语义不清。命名为 claude-code 而非 deepseek:配置的本质是"claude code CLI 的模型凭证",供应商可换(外网 deepseek ↔ 内网模型),不与供应商绑死;镜像侧 env/占位符同步中性化为 `MODEL_API_KEY`。`morningstar.app.dev.*` 下三个配置类平级:`SandboxProperties`(仅镜像;`/workspace` 挂载点是镜像契约——entrypoint/mcp.json 路径写死,做成配置项会谎称灵活性,故不收)、`ClaudeCodeProperties`(模型 key)、`SonarqubeProperties`(sonar token)。

### 决策 8:容器内以非 root 用户运行 bot

Claude Code CLI 在 root/sudo 下禁止 `--dangerously-skip-permissions`。Dockerfile 创建 `bot` 用户并 `USER bot`,容器所有进程(entrypoint/sleep/`docker exec`)均以 bot 身份运行;`~/.claude` 与 `/workspace` 均 `chown` 给 bot。

### 决策 9:workspace 使用 named volume(`docker volume`)

容器以非 root 运行时,bind mount 宿主目录会导致 UID 不对齐(容器内 bot ≠ 宿主机用户),导致无写权限。改用 named volume(`docker volume create morningstar_dev_repo_<projectId>`,启动时 `-v morningstar_dev_repo_<projectId>:/workspace/repo`):Docker 自动从镜像复制目录结构并保留属主,UID 天然正确。**挂载点取 `/workspace/repo` 而非 `/workspace`**:volume 只装仓库代码,`.mcp.json` 留在镜像层(决策 2)。前提:镜像里 `/workspace/repo` 必须**存在且属主为 bot**(Dockerfile `mkdir -p /workspace/repo` + `chown`)——否则 Docker 以 root 属主创建挂载点,bot 无写权限(8/6 实测踩坑)。

### 决策 10:volume 持久化作项目级本地缓存

volume 命名从 `ws-<runId>` 改为 `morningstar_dev_repo_<projectId>`,绑定项目生命周期(非 run)。SyncAction 首次 clone,后续 run 只做 `fetch + switch -C + clean -fdx` 增量更新(几秒,避免每次全量 clone)。CleanAction 只删容器,**不删 volume**(`docker volume rm` 仅在项目删除时触发)。效果:volume 成为项目级代码缓存,大幅减少网络 I/O 和时间。

### 决策 11:命名确定性 + 失败语义(8/6 落地 StartAction/CleanAction 时定)

- 容器名 `morningstar_dev_sandbox_<runId>`、volume 名 `morningstar_dev_repo_<projectId>` 均由 ID **确定性推导**(前缀配置 `sandbox.container-name-prefix`/`volume-name-prefix`)→ **不记 `dev_run.container_id`**:DB 副本会和 docker 实际状态漂移,能推导就不存;`CleanAction`/`FixAction`(`docker exec`)按 runId 算名直用。
- Action 失败统一 catch `ProcessExecutionException` → 返回 FAILED `ActionResult`(message = 命令+退出码+stderr,落 `action_attempt.result`)。**不裸抛**:`AbstractAction.execute` 无兜底,裸抛 → attempt 停 RUNNING、run 卡中间态占并发槽,只能等 60min 超时兜底。
- `CleanAction` 幂等:`docker rm -f` 报 "No such container" 视为成功(清理目标即"容器不存在",已不存在 = 目标达成)。
- `FailedTrigger` 破环:FAILED **来自 CLEANING** 时不再发 CLEAN——否则 CLEAN 失败 → FAILED → 自动 CLEAN → 再失败,无限循环刷 `action_attempt` 表(START 失败必踩:容器没起来,rm 必报 no such container)。破环后链路:START_FAILED → FAILED → CLEAN(幂等成功)→ CLEANED 终态;docker daemon 级故障则停在 FAILED 躺平,错误现场在 attempt 记录里。
- START 无重试(max-attempts 只覆盖 sync/scan/fix/verify/submit),故 StartAction 无需 `rm -f` 预清理——下次是新 runId、新容器名,不冲突。

### 决策 12: Gitea 双视角地址(8/7 定)

Gitea 地址按消费方拆两份,**每环境显式配全、无任何回退**:

| 配置 | 用途 | dev | prod |
|------|------|-----|------|
| `public-origin` | 后端 API、PR 链接、浏览器访问 | `http://127.0.0.1:7001` | `https://gitea.morningstar369.com` |
| `container-origin` | 临时 git 容器内访问 Gitea | `http://host.docker.internal:7001` | `https://gitea.morningstar369.com` |

命名选择:`public-origin` 对齐 Gitea ROOT_URL 语义(官方文档称"对外访问地址"),`container-origin` 表"容器网络内视角"。dev 两个值不同(Mac 宿主机不解析 host.docker.internal,8/7 ping 实测否决单配置方案),生产两者同为公网域名但显式写出,不依赖隐式回退。

代码影响:`GiteaProperties.origin` → `publicOrigin` + 新增 `containerOrigin`;`GiteaUtil.formatRepoLink`/`collaboratorUrl`/`authHeaders` 三处随迁;SyncAction 拼 clone URL 无条件用 `containerOrigin`。

### 决策 13: ProcessUtil.test() 探测模式(8/7 定)

`ProcessUtil` 新增 `test(String... args)` 方法:命令成功返回 `true`,失败返回 `false`,**不抛异常**。与 `run()` 互补——`run` 表达"必须成功,失败即异常",`test` 表达"成败都只是答案,返回布尔值"。

实际使用:
- SyncAction:`!test("rev-parse --is-inside-work-tree")` → 首次/增量分支(失败=首次,命令语义本身就回答"是不是 git 仓库")
- RestoreAction:`test("rev-parse --verify fix/<runId>")` → fix 分支是否存在(存在才删)

`test` 放在 `ProcessUtil` 而非 Action 私有方法:和 `run` 一样都是对进程执行结果的通用处理策略,不属于某个 Action 独有逻辑。后续 FixAction 的探测需求可复用。

### 决策 14: 属主漂移处理(8/7 实测定)

alpine/git 容器以 root 运行,在 volume 上创建的文件属主 root:root;sandbox 容器以 bot 运行,无法修改 root 文件。两管齐下:

- **git 侧**:所有 `-C /workspace/repo` 的 git 命令统一加 `-c safe.directory=/workspace/repo`(volume 属主 bot 但容器以 root 跑,git 报 "dubious ownership";clone 除外——目录尚不存在)
- **文件侧**:if-else 之后末尾统一 `docker exec --user root <containerName> chown -R bot:bot /workspace/repo`(覆盖 clone/switch 产生的 root 文件;`--user root` 覆盖容器的 `USER bot` 限制)

两条路径一把收,无分支。

### 决策 15: RestoreAction 还原流程(8/7 实测通过)

失败/取消后还原工作区到 SyncAction 拉取时的状态,7 步 + 属主修正:

1. `reset --hard HEAD` — FixAction 有 commit(`checkout .` 不够用,reset 才能保证工作区干净)
2. `clean -fdx` — 删除 untracked 文件和目录
3. `switch <originalBranch>` — 切回配置的原始分支
4. `rev-parse --verify fix/<runId>` 探测 → `branch -D fix/<runId>` — 删除本地修复分支
5. `reset --hard origin/<originalBranch>` — 重置到 fetch 状态,兜底保证分支指针和工作区一致
6. `rev-parse HEAD` — 取证 commitSha 存入 `RestoreResult`
7. `docker exec --user root chown -R bot:bot` — 属主修正

**纯本地操作**:不依赖远端,不需要 `--add-host`/`http.extraHeader`/`GiteaProperties`。所有 git 命令带 `-c safe.directory=/workspace/repo`。

### 决策 16: FIX/VERIFY 重试收敛到 RestoredTrigger(8/7 定)

`FixingStateTransition` 和 `VerifyingStateTransition` 不再注入 `ActionAttemptMapper`/`MaxAttemptsProperties`/`CancelTracker`,失败无条件 `→ RESTORING`。所有重试与取消决策集中到 `RestoredTrigger`:

- 通过 `latestFix`/`latestVerify` 时间戳判断最新失败来源(不可能 `fixAttempts > verifyAttempts` 推出来源,因 FIX#1 FAIL → FIX#2 SUCCESS → VERIFY#1 FAIL 时 `fixAttempts=2 > verifyAttempts=1` 但最新失败是 VERIFY)
- 按对应重试上限(`maxAttempts.getFix()`/`maxAttempts.getVerify()`)决定续修或放弃
- 取消检查(`cancelTracker.contains()`)嵌入重试条件,取消时走对应失败事件(`FIX_FAILED`/`VERIFY_FAILED → FAILED → CLEAN`)
- `RestoredStateTransition` 新增 `FIX_FAILED`/`VERIFY_FAILED → FAILED`

优势:重试策略单一控制点,`FixingStateTransition`/`VerifyingStateTransition` 零依赖。

### 决策 17: 后端容器化(Docker-in-Docker via docker.sock)(8/7 定)

生产后端自身也运行在容器内,需操控宿主机 Docker daemon(起/停 sandbox 容器、临时 alpine/git 容器)。

- **Dockerfile**:多阶段构建,从 `docker:cli` 镜像 COPY `docker` 二进制到 `eclipse-temurin:17-jre`。`docker` CLI 是 Go 纯静态链接(~25MB),无系统依赖,比 apt repo 方式干净。
- **docker-compose**:springboot 服务挂载 `/var/run/docker.sock:/var/run/docker.sock`。当前 Dockerfile 无 `USER` 指令,容器以 root 运行,访问 docker.sock 无权限问题。
- **网络**:sandbox 容器用真实域名访问 Gitea/SonarQube,不与 docker-compose 网络绑定(当前 demo 部署在一起,但后续可能拆分为独立部署——域名保持通用性)。
