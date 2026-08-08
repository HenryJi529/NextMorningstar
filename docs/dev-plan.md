# NextMorningstar · AI 漏洞修复流水线 — 开发计划

> **概念**:程序员下班,AI 上班 —— 夜间无感清洗技术债,早上看 PR 决定是否合并。
>
> **目标定位(8/14 上线)**:演示级端到端闭环 + 真实多仓库 + 夜间无人值守稳定。资源池复用 Spring Async,并发靠容器隔离。本版**不做优先级排序**。
>
> **时间盘**:8/2(周日)缓冲 → 8/14(周五上线)。日历可用 ~57h、阶段工时 56h(周末加码 +4h 抵 8/5 休息)→ ~1h buffer,极紧。**全程仅 8/8(六)、8/9(日) 一个完整周末**,是 fix 主力 + 闭环收尾的**决战窗口**,各拉到 10h,务必保证不被占用。
> **关键里程碑**:**8/9 后端端到端闭环跑通**,留 5 天给前端/联调/演示素材。

---

## 合作模式(项目灵魂 · 汇报核心)

本项目有两层"人-AI 合作",既是设计准绳,也是汇报叙事。

### 运行时:AI 与开发者的合作 ——「AI 自主修复 + 人工合并门」

在自动化谱系中,本项目卡在"AI 辅助"与"全自主"之间:**AI 全自动完成 扫描→修复→验证,但合并到主干的权力永远在人**。

| 环节 | AI | 人 |
|---|---|---|
| 配置"管哪些仓库" | — | ✅ 项目经理启用 |
| 扫描→修复→验证 | ✅ 全自主 | — |
| **合并到主干** | ❌ 绝不 | ✅ 唯一不可剥夺的权力 |
| 修不了的 issue | 标记不强提 | ✅ 决定怎么办 |
| 随时取消 | 响应 | ✅ |

**底层逻辑**:漏洞修复有 sonar 做客观裁判(确定性),AI **能**自主;但合并影响主干,"人不经手的代码难以发现隐患"(生产敬畏),故**必须**留人工门。

**让协作可信的 4 原则**:
1. 单一人工门——夜间无感、早上看 PR
2. 全程可观测——`ActionAttempt` + `commit_message` + commit 留痕,非黑箱
3. AI 不强推——`verify` 失败的修复不进 PR,人不替 AI 背锅
4. 合并权不可剥夺——AI 只有"提案权"

**演进路径**:MVP 人全审 → 近期 AI 自评置信度(低置信只报告) → 远期分级信任 + 人反馈反哺。

### 开发时:人与 AI(Claude)的合作 ——「人主导 + AI 辅助」

本项目自身由"人 + Claude Code"协作开发。模式为**人主导、AI 辅助**:核心代码人手写(牢牢把握每一行,如状态机),AI 承担样板/调试/验收/review。

| 人写(核心,要掌控) | AI 辅助 |
|---|---|
| 状态机/编排、Action 核心算法、安全(凭证/授权)相关 | 样板代码(SQL/PO/Mapper/CRUD/配置类) |
| 关键设计决策的代码 | 跑命令 + 调试(编译/git/docker/curl/sonar) |
| 任何想亲手写的部分 | review 人写的代码、卡住时讨论、前端(人定) |

**铁律**:核心逻辑 AI 不越界代写;每阶段由人划定"这块我写 / 那块你写"。

> **元叙事(汇报金句)**:用「人主导 + AI 辅助」的方式,开发出一个「AI 自主 + 人工门」的产品——这本身即是本项目的价值示范:AI 不替代人,而是在人划定的边界内,把人从重复劳动里解放出来。

---

## 一、现状诊断

### ✅ 已就绪的"骨架"(不用重做)

| 模块 | 说明 |
|---|---|
| 状态机内核 | `State`(18 态) / `Event` / `StateMachineService`(`synchronized` + Spring 事件) |
| **自动编排** | `Trigger` 监听 `StateChangedEvent` 自动驱动下一个 Action,18 个全到位 |
| 执行模板 | `AbstractAction`:`@Async` + `ActionAttempt` 记录 + 重试 + 成败事件分发 |
| 重试 / 取消 | `MaxAttemptsProperties` + `CancelTracker` |
| 数据模型 | `dev_project` / `dev_run` / `dev_action_attempt` 三表 + PO + Mapper |
| 配置 | `SonarqubeProperties`(token + origin) |

### ❌ 核心缺口(本计划要填的)

**8 个 Action 的 `doExecute` 目前全部继承 `MockAction`(sleep 1s + 随机成败),真实业务逻辑零实现。** 此外缺:Controller 入口、`dev_issue` 表、定时任务、前端、演示案例。

---

## 二、核心设计决策

| # | 决策 | 方案 |
|---|---|---|
| 1 | **容器策略** | 预构建 `morningstar-dev-sandbox` 镜像(jdk+maven+node+python+**claude CLI**+sonar-scanner + 配置模板 settings.json/mcp.json 占位符打进镜像,**不装 git**),每 run 起一个独立容器,**只跑 claude 改文件 + maven/sonar 构建**。代码用 **named volume**(fix-runtime-container 决策 9)互通,git 由后端执行(决策 13)。容器以非 root bot 用户运行(fix-runtime-container 决策 8),隔离并发 |
| 2 | **verify 本质** | **不是"编译通过",而是"sonar 重扫后 issue 消失"**。Java build 只为产 `target/classes` 喂给 scanner。sonar 既出题又阅卷 |
| 3 | **scan 方式** | 用 `sonar-scanner` 自己扫(已验证参数:`sonar.java.binaries` / `skipUnresolvedTypeChecks` / 多模块逗号分隔) |
| 4 | **新增 `dev_issue` 表** | 一漏洞一记录、一漏洞一 commit 的载体。Fix→Verify→Submit 串联的关键,**必加** |
| 5 | **优先级** | 本版**不加**,定时任务直接遍历 `enabled` project |
| 6 | **commit 归临时容器 git** | claude 只改文件;后端通过 `docker run --rm -v morningstar_dev_repo_<projectId>:/workspace/repo` 起临时 alpine/git 容器 `add -A && commit`(纯本地操作,不需凭证),保证"一漏洞一 commit" + 规范 message,可控 |
| 7 | **claude 认证** | 容器内 `/home/bot/.claude/settings.json` 配 **deepseek(国产模型)** 连接,不用 claude 登录态 |
| 8 | **claude 修复模式** | **统一 prompt，读 issue 字段即修**（title/codeSnippet/metadata），不依赖 MCP；ScanAction 已存储全部诊断信息 |
| 9 | **密钥规则排除** ⭐ | ScanAction 加规则黑名单,默认排除凭据类(`java:S2068` / `secrets:*`);Security Hotspots 走独立 API,`/api/issues/search` 天然不返回 → 大部分明文密钥问题自动挡掉(公司内网明文密钥合规) |
| 10 | **volume 持久化** | volume 命名 `morningstar_dev_repo_<projectId>` 绑定项目(非 run),SyncAction 首次 clone 后续 `fetch + switch -C + clean -fdx` 增量更新;CleanAction 只删容器不删 volume,仅项目删除时才清理(决策一起更新) |
| 11 | **代码托管平台** | 演示(8/14)用 **Gitea**(已就绪);生产内网用 **GitLab**,留待内网部署阶段独立实现。**不做抽象层**(YAGNI)——届时目标单一,直接把 Gitea 调用替换为 GitLab;真到两套长期并存再抽接口 |
| 12 | **仓库授权(双 token)** ⭐ | **bot token(`repo write`)通过 git `http.extraHeader` 传入临时 alpine/git 容器**(日常 git 凭证,不落盘);**admin token 仅"加 collaborator"瞬间用、用完即弃**。项目经理启用项目时自动给 bot 加 write、禁用移除。凭证仅存在临时容器内、用完即毁 → 爆炸半径最小 |
| 13 | **git 归临时容器** ⭐ | git clone/commit/push/reset 由后端通过 `docker run --rm -v morningstar_dev_repo_<projectId>:/workspace/repo` 起临时 alpine/git 容器执行(**镜像名写死**,不进配置),用完即毁。clone/fetch URL 一律**无凭证形式**,token 走 `-c http.extraHeader` 当次生效,**不落 volume 里 `.git/config`**。代码互通靠 named volume(fix-runtime-container 决策 9)。**prompt injection 偷不到 git 凭证**。MVP 不处理子模块(clone 无 `--recursive`,决策 22) |
| 14 | **失败 issue 跨 run 记忆** | scan 时排除"近期 FAILED"的 issue(查 `dev_issue` 历史 status=FAILED 且近期),避免每夜反复重试同一个修不好的 issue,提升无人值守效率 |
| 15 | **资源池 + 夜间窗口** ⭐ | 并发度可配(`schedule.max-concurrency`,**默认 2**——Fix 占大头是模型对话 I/O、CPU 空闲,多 run 错开可并行;Scan/Verify 才 CPU 密集且短)。夜间 21:00 自动创建 PENDING run,次日 **6:00 清晨清理**:`cancelOvernightRuns` 取消所有非终态活跃 run(PENDING 直接标 CANCELED,其余走 `requestCancel`)。另每 5min 超时检测(60min 无响应)+ 每 30s 分发调度 |
| 16 | **AI 诊断报告(commit message)** | 由 claude 基于 SonarQube 接口数据(rule/issue 详情)用**中文**生成单 issue 诊断,格式按 `resources/dev/ai-report-template.md`(用户提供);平台读模板 + 喂 sonar 数据给 claude → 输出存 `dev_issue.commit_message` → PR 评论汇总。客观数据 + AI 中文改写,避免直接用英文 rule 描述 |
| 17 | **容器操作走命令行 docker** ⭐ | `StartAction`/`CleanAction`/git 操作均用 `ProcessBuilder` 调 `docker` CLI(`docker run -d`/`docker rm -f`/`docker run --rm alpine/git`),不引 docker-java 库,共用 `util/ProcessUtil`(8/6 已实现:stderr 独立线程防死锁、stdout 仅剥末尾换行、静态嵌套异常带完整 stderr、6 单测)。后端纯编排,不碰文件系统 |
| 18 | **命名确定性 + 失败语义**(8/6 定) | 容器名 `morningstar_dev_sandbox_<runId>`、volume 名 `morningstar_dev_repo_<projectId>` 均由 ID 确定性推导(前缀在 `sandbox.container-name-prefix`/`volume-name-prefix`)→ **不记 `dev_run.container_id`**(DB 冗余会漂移,docker 才是真源)。Action 失败统一 catch `ProcessExecutionException` → FAILED 结果(stderr 落 `action_attempt.result`);**不裸抛**(`AbstractAction` 无兜底,裸抛 = attempt 停 RUNNING + run 卡中间态占并发槽等 60min 超时)。CleanAction 幂等:"No such container" 视为成功。`FailedTrigger` 破环:FAILED 来自 CLEANING 不再发 CLEAN(否则 CLEAN 失败 → FAILED → CLEAN 无限循环刷 attempt 表);START 无重试,START_FAILED → FAILED → CLEAN(幂等)→ CLEANED 干净终态 |
| 19 | **Gitea 双视角地址**(8/7 定) | Gitea 地址按消费方拆两份,**每环境显式配全、无任何回退**:`public-origin`(对外地址,对齐 Gitea ROOT_URL 语义——后端调 API、PR 链接、浏览器访问)与 `container-origin`(容器网络内地址——临时 git 容器 clone/fetch/push)。dev: `http://127.0.0.1:7001` / `http://host.docker.internal:7001`(宿主机不解析 host.docker.internal,8/7 ping 实测否决单配置方案);生产两者同为公网域名,冗余但显式。`GiteaProperties.origin` → `publicOrigin` + 新增 `containerOrigin`,`GiteaUtil` 三处引用随迁 |
| 20 | **ProcessUtil.test() 探测模式**(8/7 定) | `ProcessUtil` 新增 `test(String... args)`:命令成功返 `true`、失败返 `false`,不抛异常。与 `run` 互补——`run` 表"必须成功,失败即异常",`test` 表"成败都是答案"。SyncAction 用 `!test("rev-parse")` 做首次/增量分支,消掉内层 try-catch,只剩外层兜底 git 操作失败。放在 `ProcessUtil` 非私有——探测是通用策略,RestoreAction/FixAction 后续可复用 |
| 21 | **属主漂移处理**(8/7 实测定) | alpine/git 容器以 root 运行,在 volume 上创建的文件 root:root、sandbox(bot)无法修改。两管齐下:①git 命令统一 `-c safe.directory=/workspace/repo`(clone 除外)②末尾 `docker exec --user root chown -R bot:bot`(覆盖容器的 USER bot)。MVP 不处理子模块(clone 无 `--recursive`,增量无 submodule update),后续独立任务补 |
| 22 | **MVP 不处理子模块**(8/7 定) | clone 不加 `--recursive`,增量无 `submodule update`,SyncResult 无 `hasSubModule`。子模块需额外处理(子仓库权限链、跨子模块 commit、`.gitmodules` URL 替换),排到 MVP 后独立任务 |
| 23 | **RestoreAction 还原流程**(8/7 实测定) | 7 步还原 + 属主修正:`reset --hard HEAD`(FixAction 有 commit 不能用 `checkout .`)→ `clean -fdx`(删除 untracked)→ `switch <originalBranch>`(切回原始分支)→ `rev-parse --verify` 探测 → `branch -D fix/<runId>`(删除本地修复分支)→ `reset --hard origin/<originalBranch>`(兜底重置到 fetch 状态)→ `rev-parse HEAD` 取证 commitSha → `docker exec --user root chown -R bot:bot`。**纯本地操作**:不依赖远端,不需要 `--add-host`/`http.extraHeader`/`GiteaProperties`。`RestoreResult` 记 commitSha |
| 24 | **FIX/VERIFY 重试收敛到 RestoredTrigger**(8/7 定) | `FixingStateTransition`、`VerifyingStateTransition` 无条件 `FIX_FAILED`/`VERIFY_FAILED → RESTORING`,不再注入 `ActionAttemptMapper`/`MaxAttemptsProperties`/`CancelTracker`。所有重试决策收敛到 `RestoredTrigger`:通过 `latestFix`/`latestVerify` 时间戳判断最新失败来源,按对应重试上限(`fix`/`verify`)决定续修(`Event.FIX`)或放弃(`FIX_FAILED`/`VERIFY_FAILED → FAILED`)。取消检查(`cancelTracker.contains()`)嵌入重试条件,取消时自动走对应失败事件。`RestoredStateTransition` 新增 `FIX_FAILED`/`VERIFY_FAILED → FAILED` |
| 25 | **git commit 身份配置**(8/7 定) | 后续 FixAction commit 需要 `user.name`/`user.email`,否则 git 报 "Committer identity unknown"。SyncAction 在 clone/fetch 之后设 repo 级 `git config`(落 volume `.git/config`,后续临时容器可读):`user.name` 取 `GiteaProperties.botUsername`、`user.email` 取 `GiteaProperties.botEmail`(8/7 `GiteaProperties` 新增字段)。每次 sync 都重写,不依赖上次值——bot 身份变更下次跑即生效。不设 `--global`(容器临时),不拼进 clone URL(不走 volume) |
| 26 | **后端容器化 + docker.sock**(8/7 定) | 生产后端运行在容器内,需操控宿主机 Docker daemon(起/停 sandbox 和临时 alpine/git 容器)。**Dockerfile**:多阶段构建,从 `docker:cli` 镜像 COPY `docker` 二进制(`~25MB`,Go 纯静态、零系统依赖)到 `eclipse-temurin:17-jre`,比 apt repo 干净。**docker-compose**:springboot 服务挂载 `/var/run/docker.sock:/var/run/docker.sock`(默认 root 运行,无权限问题)。网络保持域名直连(sandbox 容器用真实域名访问 Gitea/SonarQube,不与 docker-compose 网络绑定——demo 部署在一起,但后续可能拆分) |

---

## 三、端到端流程

```
PENDING
 → START      后端 `docker volume create morningstar_dev_repo_<projectId>`,起容器挂载 `morningstar_dev_repo_<projectId>:/workspace/repo`(volume 持久化,决策 10)
 → SYNC      后端起临时 alpine/git 容器(`--add-host` 统一带):`processUtil.test("rev-parse")` 探测(决策 20)
             → 首次:清空 `/workspace/repo`(`alpine find -mindepth 1 -delete`,保重试幂等)→ `clone --branch <branchName>`
             → 增量:`fetch`(带 `http.extraHeader`)+ `switch -C` + `clean -fdx`
             → git 命令统一带 `-c safe.directory=/workspace/repo`;末尾 `docker exec --user root chown -R bot:bot`
             → 取证 `rev-parse HEAD` 进 `SyncResult(gitUrl/branchName/commitSha)`;失败外层 catch 转 FAILED,不裸抛
             (MVP 不处理子模块:clone 无 `--recursive`,增量无 `submodule update`,SyncResult 无 `hasSubModule`)
 → SCAN       容器内 mvn -q compile(产 target/classes)→ sonar-scanner
              → 后端调 /api/issues/search 拉 OPEN issue(按 severity 排序,过规则黑名单)
              → 落 dev_issue 表,按 maxIssuesPerRun 截断
 → FIX ⭐     逐 issue:容器内 claude 读 issue 字段修复(named volume)
              → 后端 `docker run --rm -v morningstar_dev_repo_<projectId>:/workspace/repo alpine/git add -A && commit`   # 临时容器,一漏洞一 commit
 → VERIFY     ①SonarQube 重扫（关闭+回归检测）→ ②Claude review（语义验证）
              → 全 CLOSED=全 VERIFIED;存在 OPEN → 全 FAILED → RestoreAction 整轮回退(完整 7 步回到 origin)
 → SUBMIT     后端 `docker run --rm -v morningstar_dev_repo_<projectId>:/workspace/repo alpine/git push` 修复分支 → 调 Gitea API 开 PR(auto-delete)+ 统一格式诊断评论
 → CLEAN      docker rm -f(不删 volume,保留项目缓存,决策 10)
```

> **取消/失败**:任何阶段失败达重试上限 → `FAILED` → `FailedTrigger` 发 `CLEAN`(来自 CLEANING 的 FAILED 除外,破环);FIX/VERIFY 失败 → `RESTORING`(RestoreAction 7 步还原,决策 23)→ `RESTORED` → `RestoredTrigger` 重试决策(决策 24):有重试且未取消 → `FIXING` 续修,否则 `FIX_FAILED`/`VERIFY_FAILED` → `FAILED` → `CLEAN`;用户点取消 → `CancelTracker` 标记 → `*ed` Trigger 发 `CLEAN`(跳过后续阶段)→ `CLEANED`。

---

## 四、关键技术方案

### 4.1 修复容器与镜像

镜像 `morningstar-dev-sandbox:latest`(放 `deploy/dev-sandbox/Dockerfile`):
- 基础:`eclipse-temurin:17-jdk`
- 装入:maven、node、python3、**claude code CLI**、sonar-scanner(**不装 git**——git 操作由后端完成,见决策 13)
- **named volume**:`docker volume create morningstar_dev_repo_<projectId>`,容器挂载 `morningstar_dev_repo_<projectId>:/workspace/repo`,后端 git 与容器 claude/maven 操作同一份代码。非 root bot 用户下 UID 天然正确(fix-runtime-container 决策 9)
- **模型连接可插拔**:`settings.json`(用户级 `~/.claude/`)+ `mcp.json`(**项目级 `/workspace/.mcp.json`**,claude 跟 cwd 走)以**占位符模板打进镜像**,entrypoint 启动时用 env(`MODEL_API_KEY`/`SONARQUBE_TOKEN`)替换真 key(真 key 不进镜像);开发期(外网)注入 deepseek key,内网部署换行内模型 key——镜像通用

> 🔴 **Day-1 spike(阶段 1)**:容器内跑通 `claude -p "..."`(deepseek + sonarqube MCP),且后端能通过临时容器 git clone 到 named volume、AI 容器内可见。全流程前提。

### 4.2 统一 prompt 修复模式（FixAction 核心）

平台不依赖 MCP——ScanAction 已把 SonarQube rule 描述和 AI 诊断全存进 issue 字段。FixAction 统一 prompt，Claude 读 issue 即修，后端控制 commit：
```bash
# 1. 容器内 claude 读 issue 字段修复
docker exec <c> claude -p --dangerously-skip-permissions \
  "修复以下问题——标题: <title>，诊断: <metadata.description>，建议: <metadata.suggestion>，文件: <metadata.filePath>，代码片段: <codeSnippet>。请修复并输出 commit message。"

# 2. 后端通过临时容器 git 控制 commit(一漏洞一 commit,纯本地不需凭证)
docker run --rm -v morningstar_dev_repo_<projectId>:/workspace/repo alpine/git \
  -c http.extraHeader="Authorization: token ..." \
  -C /workspace/repo add -A
docker run --rm -v morningstar_dev_repo_<projectId>:/workspace/repo alpine/git \
  -c http.extraHeader="Authorization: token ..." \
  -C /workspace/repo commit -m "<commit_message>"
```
- 单 issue 超时:`claude ... --max-turns N` + 整 run wall-clock(复用 CancelTracker)
- deepseek 最新模型修复效果好,预计不用调 prompt(用户已验证)

### 4.3 sonar 裁判机制(VerifyAction)

修复后 `mvn -q compile` → `sonar-scanner` 重扫 → 查 issue 状态:
```bash
# 修复成功的判定:issue 不再是 OPEN(变 FIXED/已关闭 或 查不到)
curl -s -u "<token>:" "http://<sonar>/api/issues/search?issues=<issueKey>" | jq '.issues[]?.status'
```
- 全部已关闭 → 所有 issue `dev_issue.status=VERIFIED`
- 存在 `OPEN` → 所有 issue `status=FAILED` → 状态机驱动 RestoreAction 整轮回退到 `origin/<branch>`
- MVP 不做逐 commit 保留（理由见 fix-runtime-container 决策 18）

### 4.4 PR 评论模板(SubmitAction)

对应素材"链接对接 + AI 诊断报告 + 修复思路 + 合规底线":
```markdown
## 🤖 AI 夜间清洗报告

本 PR 由 **NextMorningstar** 自动生成,共修复 **N** 个问题。
⚠️ AI 无主干合并权限,请人工 review 后决定是否合并。

| # | 规则 | 级别 | AI 诊断 | Sonar |
|---|------|------|---------|-------|
| 1 | `java:S2259` | 🔴 BLOCKER | 补判空 | [issue](http://<sonar>/issues?open=<id>) · [rule](http://<sonar>/coding_rules?open=java:S2259) |
| 2 | `java:S2095` | 🟠 CRITICAL | try-with-resources | [issue](...) · [rule](...) |

### 修复思路
- **java:S2259**: ...(来自 claude 输出)
- **java:S2095**: ...
```

### 4.5 凭证与安全模型 ⭐

**核心原则:能改代码仓库的凭证,绝不进 AI 容器。**

| 凭证 | 位置 | 用途 | 风险 |
|---|---|---|---|
| Gitea **admin token** | 后端,用完即弃 | 加/删 collaborator | 高,但瞬间 |
| Gitea **bot token**(git) | 后端内存(yml) | git clone/push(临时 alpine/git 容器 `-e` 注入) | 高 → 用完即毁 |
| Sonar token | **容器内(env 注入)** | sonar-scanner | 中(非代码权限) |
| deepseek key | **容器内(env 注入)** | claude 调模型 | 中(可独立轮换) |

**三层防御:**
1. **凭证隔离**:git 凭证只在后端,AI 容器内没有 → prompt injection 偷不到代码仓库写权限
2. **网络出站白名单**:容器只放行 gitea/sonar/deepseek,其余挡死 → 即使容器内 sonar/deepseek 凭证被偷也外传不出
3. **bot token 最小权限**:单仓库 write,后端凭证文件若泄漏也只限已授权仓库

**为什么 deepseek key 必须进容器(唯一不得不的让步):** claude 必须容器内跑(隔离 AI,不能放后端直接操作),调模型就要 key——以**运行时 env 注入**(`MODEL_API_KEY`,entrypoint 替换占位符),**不进镜像层**。让步的是 AI 服务 key(可独立轮换、非代码权限),不是 git 凭证;靠网络白名单兜底。

**为什么弃用 JGit、选命令行 git:** JGit 对 submodule(递归/多认证/LFS)支持差、易踩坑;命令行 git `--recursive` 原生稳定,且命令与原方案几乎一致。

---

## 五、数据模型

### 5.1 新增 `dev_issue` 表

```sql
create table if not exists dev_issue
(
    id                       int auto_increment,
    run_id                   binary(16)    not null,
    source                   varchar(16)   not null comment 'SONAR/AI',
    metadata                 json comment 'SonarMetadata 或 AiMetadata(@source 区分)',
    title                    varchar(1024) not null,
    reliability_severity     varchar(16) comment 'BUG(缺陷)',
    security_severity        varchar(16) comment 'VULNERABILITY(安全漏洞)',
    maintainability_severity varchar(16) comment 'CODE_SMELL(代码异味)',
    effort                   varchar(16) comment '修复耗时估算',
    status                   varchar(16)   not null comment 'SELECTED/FIXED/VERIFIED/FAILED/ACCEPTED/REJECTED',
    commit_sha               varchar(64) comment '修复 commit',
    commit_message           text comment 'commit message',
    create_time              datetime,
    update_time              datetime,
    primary key (id)
) comment '研发问题表';
```

> source 区分器 + metadata JSON（内嵌 SonarMetadata/AiMetadata 多态）；三维 severity 独立表达 BUG/VULNERABILITY/CODE_SMELL；无唯一约束——ScanAction 插入前删旧重写。

### 5.2 现有表现状(阶段 0 完工)

- `dev_project`:11 字段 — `id`/`admin_id`/`name`/`link`/`branch_name`/`description`/`enabled`/`max_sonar_issues_per_run`/`max_ai_issues_per_run`/`create_time`/`update_time`。
- `sonarProjectKey`：**不存表**——`owner:repo` 随时从 `link` 解析，不冗余存储。ScanAction 需要时从 link 推导。
- `max_sonar_issues_per_run`/`max_ai_issues_per_run`：**创建时写入 DB**——前端未指定则取全局 `max-issues-per-run` 默认值写入，之后全局变更不影响已有项目。ScanAction 直接读 project 字段，不 fallback。
- `dev_run`:8 字段 — `id`/`project_id`/`state`(State 枚举)/`status`(Run.Status 枚举)/`container_id`(已弃用,容器名由 runId 推导,决策 18)/`pr_id`/`create_time`/`update_time`。不加 `finished_at`(`update_time` 即可,pipeline-foundation 决策 6)。
- `dev_action_attempt`:7 字段 — `id`/`run_id`/`action_type`/`attempt_no`/`status`/`result`(JSON)/`create_time`/`update_time`。不加 `start_time`/`end_time`(`create_time`/`update_time` 即可,pipeline-foundation 决策 6)。

> `priority` 本版不加(决策 5)。`state` vs `status` 双字段分离:前者追踪流水线阶段(状态机驱动),后者追踪整体结果(观测用),查询仅用 `state`(pipeline-foundation 决策 10)。

---

## 六、配置项清单(`application-*.yml` → `morningstar.app.dev`)

```yaml
morningstar.app.dev:
  sonarqube:
    origin: http://127.0.0.1:7002
    token: squ_xxx
  max-attempts:        # 各阶段重试上限(对齐现有 MaxAttemptsProperties)
    scan: 2
    fix: 2
    verify: 1
    # start/sync/submit/clean/restore ...
  fix:
    max-fixes-per-run: 10           # 也可放 project 级覆盖
    issue-time-limit-seconds: 300   # 单 issue claude 超时
    run-time-limit-seconds: 3600    # 整 run wall-clock
    severity-whitelist: [BLOCKER, HIGH, MEDIUM]
    rule-blacklist:                 # ⭐ 密钥/凭据类默认排除
      - java:S2068
      - common-java:S2068
      - secrets:*
  sandbox:
    image: morningstar-dev-sandbox:latest
    container-name-prefix: morningstar_dev_sandbox_
    volume-name-prefix: morningstar_dev_repo_
  claude-code:
    model-api-key: ${MODEL_API_KEY}
  gitea:
    public-origin: http://<gitea>     # 对外地址:后端 API、PR 链接、浏览器访问
    container-origin: http://<gitea>  # 容器网络内地址:临时 git 容器 clone/fetch/push(dev: host.docker.internal,生产:公网同 public-origin,决策 19)
    bot-username: morningstar-bot
    bot-token: xxx              # 仅 repo write,临时 alpine/git 容器使用(通过 http.extraHeader 注入,用完即毁)
    admin-token: yyy            # admin,平台后端持有,仅用于加/删 collaborator
  schedule:
    create-cron: "0 0 21 * * ?"          # 每晚 21:00 创建 PENDING run
    dispatch-cron: "*/30 * * * * ?"      # 每 30s 从 PENDING 队列分发
    timeout-cron: "0 */5 * * * ?"        # 每 5min 超时检测
    cleanup-cron: "0 0 6 * * ?"          # 次日 6:00 清晨清理
    run-timeout-minutes: 60              # 超过 60min 无响应视为超时
    max-concurrency: 2                   # 并发槽位数(Fix 是 I/O，2 核可并发 2-3)
```

---

## 七、分阶段计划

> 阶段 0–6 为后端端到端闭环(最先打通);7–9 为前端/演示/素材。

### 阶段 0 · 地基补全(~6h)— *8/2–8/3* ✅ 主体完成

- [x] 补 `dev_project`/`dev_run` 字段、新建 `dev_issue` 表与 PO/Mapper
- [x] `AbstractAction` 不额外加时间字段 — `createTime`/`updateTime` 已满足(约定统一)
- [x] `ProjectController`(CRUD + adminId 权限)/`RunController`(手动触发/查询/取消)
- [x] `RunService.createRun`(无权限，调度器用)与 `triggerRun`(含权限，Controller 用)分离
- [x] 定时调度:4 个独立 cron — `nightlyCreateRuns`(21:00 创建 PENDING)/`dispatchPendingRuns`(每 30s，按并发槽位分发)/`cancelTimeoutRuns`(每 5min，超时取消)/`cancelOvernightRuns`(6:00 清晨清理)
- [x] 取消安全:6 个"完成态" Trigger(Started/Synced/Scanned/Fixed/Verified/Cleaned)均检查 `isCancelingRun`;`requestCancel` 拒绝 PENDING/SUBMITTED/CLEANING/CLEANED
- [x] 配置 `application-app.yml`:`schedule.create-cron`/`dispatch-cron`/`timeout-cron`/`cleanup-cron`/`run-timeout-minutes`/`max-concurrency`
- [x] 验收:mock 模式端到端跑通(PENDING→CLEANED)，接口查询/取消正常(8/3 通过)

### 阶段 1 · 修复容器 + Docker 接入(~5h)— *8/4–8/6*
- [x] 写 `deploy/dev-sandbox/Dockerfile`(claude CLI + sonar-scanner + 配置模板 settings.json/mcp.json 占位符 COPY + entrypoint env 替换)
- [x] 🔴 **spike:容器内 `claude -p "..."` 跑通(deepseek + sonarqube MCP)** — 3.1 deepseek + 3.2 MCP 查 issue 全通
- [x] `util/ProcessUtil`:docker CLI 统一入口(ProcessBuilder;stderr 独立线程防 64KB 缓冲区死锁;stdout 仅剥末尾换行 `\R+$`;嵌套 `ProcessExecutionException` 含命令+退出码+完整 stderr;纯 JUnit 6 单测)— 8/6
- [x] `StartAction`:确保 volume 存在(`docker volume create morningstar_dev_repo_<projectId>`,决策 10),启动容器(命名 `morningstar_dev_sandbox_<runId>`)挂载 `morningstar_dev_repo_<projectId>:/workspace/repo`,注入 env(`MODEL_API_KEY`/`SONARQUBE_TOKEN`) + `--add-host=host.docker.internal:host-gateway`;异常转 FAILED 结果 — 8/6
- [x] `CleanAction`:docker rm -f(**不删 volume**,决策 10);"No such container" 幂等成功;`FailedTrigger` 破环(FAILED 来自 CLEANING 不再发 CLEAN)— 8/6
- [x] ~~维护 `runId → containerId`~~ 容器名由 runId 确定性推导,**不记 `dev_run.container_id`**(决策 18)
- **验收**:Start/Clean 真实起删容器

### 阶段 2 · 代码同步 / 还原(~4h)— *8/6–8/7*
- [x] `SyncAction`:✅ 落地(8/7,实测通过:首次 clone、增量更新、换分支全链路冒烟)。通过临时 alpine/git 容器(镜像写死)操作 volume。①取数:runId→projectId→link+branchName,`GiteaUtil.parseRepoIdentity` 解析,`containerOrigin` 拼 clone URL ②探测:`processUtil.test("rev-parse")` 返 false=首次(决策 20),`test` 不抛异常消掉内层 catch ③首次:清空(`alpine find -mindepth 1 -delete`,保重试幂等)→ `clone --branch <branchName>`(无子模块,MVP 不处理)④增量:`fetch`(带 http.extraHeader)+ `switch -C`(不用 reset——HEAD 可能停修复分支)+ `clean -fdx` ⑤每条 git 命令带 `-c safe.directory=/workspace/repo`(volume 属主 bot vs root 容器)⑥if-else 之后统一设 repo 级 git 身份:`git config user.name`(`GiteaProperties.botUsername`)+ `git config user.email`(`GiteaProperties.botEmail`,8/7 新增字段),每次 sync 重写保证可配置变更生效(决策 25)⑦所有路径末尾 `docker exec --user root chown -R bot:bot`(防属主漂移)⑧取证:`rev-parse HEAD` 进 `SyncResult(gitUrl/branchName/commitSha)` ⑨外层 catch → FAILED 结果(不裸抛)。`@JsonSubTypes` 已注册 SYNC
- [x] `GiteaProperties`:`origin` → `publicOrigin` + 新增 `containerOrigin`;`GiteaUtil` 三处引用随迁 `publicOrigin`;yml 两环境配置同步(决策 19)
- [x] `ProcessUtil.test()`:新增探测方法(决策 20)
- [x] `RestoreAction`:✅ 落地(8/7,实测通过:untracked/修改/分支提交全部可还原)。通过临时 alpine/git 容器(镜像写死)操作 volume。①`reset --hard HEAD`:丢弃 fix 分支上已跟踪文件的修改(FixAction 有 commit,不能用 `checkout .`)②`clean -fdx`:删除 untracked 文件和目录③`switch <originalBranch>`:切回配置的原始分支④`rev-parse --verify fix/<runId>` 探测 → `branch -D fix/<runId>`:删除本地修复分支⑤`reset --hard origin/<originalBranch>`:重置到 fetch 状态,兜底保证工作区干净⑥`rev-parse HEAD`:取证 commitSha 进 `RestoreResult`⑦`docker exec --user root chown -R bot:bot`:属主修正。**纯本地操作,不依赖远端**(无 `--add-host`/`http.extraHeader`/`GiteaProperties`)。外层 catch → FAILED 结果(不裸抛)。`RestoredTrigger` 重试/取消决策(决策 24):`FixingStateTransition`/`VerifyingStateTransition` 简化为无条件进 RESTORING,决策集中在 `RestoredTrigger`;取消检查嵌入重试条件,`StartedStateTransition` 补充 CLEAN 事件
- **验收**:✅ 首次 clone、增量更新、换分支 全链路通过

### 阶段 3 · DB 迁移 + Issue PO 重构(~2h)— *8/8*
- [ ] `dev_issue` 加 source/source_issue_key/rule_key/severity/type/title/effort/metadata 列
- [ ] 迁移脚本 `V2__ai_discovery.sql`，唯一约束 `(run_id, source, source_issue_key)`
- [ ] `Issue.java` 加 source 区分器 + 统一字段，Type 枚举不变（BUG/VULNERABILITY/CODE_SMELL）
- **验收**:旧 sonar_* 列保留兼容，新列可读写

### 阶段 4 · ScanAction 双通道 ⭐(~10h)— *8/9*
- [ ] HTTP 客户端（RestTemplate）调 SonarQube API
- [ ] SonarQube 通道:`docker exec` 跑 scanner → `/api/issues/search`(翻页取全量) → 取 issue+rule 的 `impacts` 数组映射三维 severity → batch insert
- [ ] SonarQube 通道:每个 issue 调 `/api/rules/show?key=<ruleKey>` 拿规则描述，存进 metadata
- [ ] `ScanResult` 加 `issueKeys`（基线快照，供 VerifyAction 回归检测）+ `sonarIssueNum`/`aiIssueNum`
- [ ] `AiDiscoveryProperties` 配置类 + yml（enable 开关）
- [ ] AI Discovery 通道:Claude 自由探索项目 → 输出结构化 JSON → map to Issue
- [ ] 合并去重（文件+行号重叠优先留 SonarQube）→ 规则黑名单 → severity 排序 → 跨 run 排除 FAILED → 按 `maxSonarIssuesPerRun`/`maxAiIssuesPerRun` 分别截断
- **验收**:双通道各有产出，issue 真实入库，ScanResult 存有基线快照

### 阶段 5 · FixAction 统一路径 ⭐(~10h)— *8/10*
- [ ] 统一 prompt:读 issue 字段（title/codeSnippet/metadata）→ Claude 修复，不区分 source
- [ ] 不依赖 MCP——规则描述和诊断信息已在 ScanAction 阶段存入 metadata
- [ ] 平台 git commit（一漏洞一 commit），回写 `commit_sha` + `commit_message`
- [ ] 单 issue / 整 run 超时
- **验收**:两种 source 的 issue 都能修掉并产出 commit

### 阶段 6 · VerifyAction 两道防线 ⭐(~10h)— *8/11*
- [ ] **第一道**:SonarQube 重扫 → 查 SELECTED issue 是否全 CLOSED + 对比 `ScanResult.issueKeys` 基线判断回归
- [ ] **第二道**:Claude review（读 fix diff + 原始 issue 信息）→ 语义判定修改是否正确
- [ ] 任一 issue 未关闭 / 有回归 / AI 判定 FAILED → 整轮 RESTORING
- **验收**:故意改坏，两道防线各自能抓到

### 阶段 7 · SubmitAction 统一格式 PR ⭐(~10h)— *8/12*
- [ ] 后端通过临时 alpine/git 容器推修复分支 → 调 Gitea API 开 PR
- [ ] 统一格式 PR 评论：每条 issue 展示 title + 三维 severity + description + suggestion + codeSnippet，SonarQube 对用户透明
- **验收**:Gitea 出现含统一格式诊断报告的 PR
- 🎉 **端到端闭环跑通**

### 阶段 8 · 前端(~10h)— *8/13*
- [ ] 复用 `frontend/` + system 模块认证
- [ ] 配置页:仓库/分支/启停 CRUD + 手动触发 + 手动停止
- [ ] 仪表盘:run 实时状态机流转（SSE/WS）、修复成功率、节省人月
- [ ] 大屏:高频缺陷 Top（有时间再做）

### 阶段 9 · 联调 + demo 真跑(~10h)— *8/14*
- [ ] 造 demo 仓库:埋 sonar 真问题 + 设计缺陷（供 AI Discovery 发挥）
- [ ] 用 NextMorningstar backend 自己跑一轮（真实价值演示）
- [ ] 跑通夜间定时真实触发
- [ ] 修边界:并发、超时、取消、单 run 挂不影响其他

### 阶段 10 · 交付素材(~10h)— *8/15*
- [ ] 作品录屏（端到端全过程 + 大屏）
- [ ] 演示 PPT + 详细介绍材料

---

## 八、排期日历

| 日期 | 星期 | 时段 | 阶段 | 当日必须达成 |
|---|---|---|---|---|
| 8/2 | 日 | 缓冲 | — | ✅ 本地预研三步全通过(claude 连通/MCP 查 issue/真修复) |
| 8/3 | 一 | 4h ✅ | 0 地基 | ✅ 数据模型/Controller/定时骨架/mock 验收,超出预期 |
| 8/4 | 二 | 4h ✅ | 0 收尾 + 1 镜像 | ✅ Gitea 授权(任务 3)+ Dockerfile(多阶段 node + entrypoint env 注入 + mcp 项目级 /workspace);超前打通 spike 3.1(claude+deepseek) |
| 8/5 | 三 | 4h ✅ | — | 设计决策超额完成:非 root bot + named volume + volume 持久化 + git 归临时容器 |
| 8/6 | 四 | 4h ✅ | 1 收尾 + 2 git | ✅ ProcessUtil + StartAction/CleanAction 落地(命名确定性、失败转 FAILED、clean 幂等、FailedTrigger 破环);SyncAction 设计中 |
| 8/7 | 五 | 4h ✅ | 2 收尾 + 7 前端设计 | ✅ SyncAction 全链路冒烟通过(首次/增量/换分支)+ git config 身份配置;✅ RestoreAction 实测通过 + RestoredTrigger 重试收敛;✅ 前端 UI 设计(WorkBuddy+OpenDesign 生成仪表盘+详情页,Vue3+AntDesign 对齐现有技术栈)|
| 8/8 | 六 | 8h | 决策收尾 + DB 迁移 | ✅ 决策 18/编号核查/凭证统一/架构报告； DB 迁移 + Issue PO 重构（source 区分器） |
| 8/9 | 日 | 10h ⭐ | ScanAction 双通道 | HTTP 客户端 + scanner + API + AI Discovery + 合并去重 → Issue 真实入库 |
| 8/10 | 一 | 10h ⭐ | FixAction 统一 prompt | 读 issue 字段 → Claude 修复 → git commit |
| 8/11 | 二 | 10h ⭐ | VerifyAction 两道防线 | ①SonarQube 重扫 ②Claude review → 全量判定 |
| 8/12 | 三 | 10h ⭐ | SubmitAction 统一格式 PR | 推分支 + 开 PR + 统一格式诊断评论 |
| 8/13 | 四 | 10h | 前端 | 配置页 + 仪表盘 + 大屏（穿插复用 API 等待时间） |
| 8/14 | 五 | 10h | 联调 + demo 真跑 | demo 仓库端到端 → **Gitea PR 闭环跑通** 🎉 |
| 8/15 | 六 | 10h | 交付素材 | 录屏 + PPT + 材料 |

### 8/2 缓冲日建议(可选,不写平台代码,只做风险前置) — ✅ 8/2 已完成,三步全通过

核心命门是 **8/5 的容器内 claude spike**。若 8/2 有零碎精力,在**本地**(先不进容器)验证这条链能跑通,可把头号风险提前暴露:
```bash
# 1. 确认 claude code CLI 装好、deepseek 连接配好
claude --dangerously-skip-permissions --print "ping"
# 2. 确认 sonarqube MCP 配好,能查 issue
claude --dangerously-skip-permissions --print "使用 sonarqube mcp 的 search_sonar_issues_in_projects 查看 demo-project-backend 的 issue,返回前 3 个 id 的 json 数组"
# 3. 拿一个真 issue 让它修(在 demo 仓库工作目录里跑)
claude --dangerously-skip-permissions --print "使用 sonarqube mcp 查看 issue <id> 与 rule,修复它,勿格式化其他部分,修完只打印 rule key"
```
跑通这三步 → 8/5 只需"把这套搬进容器",风险大降。跑不通 → 8/3 就有时间切方案(如换模型/API 直连),不至于 8/5 才炸。

---

## 九、MVP 切割线(落后时按序砍)

1. vue 的 verify → 跳过(MVP 只 Java `mvn compile`)
2. "自己跑 sonar-scanner 推分析" → 用 SonarServer 已有分析结果
3. 大屏高频缺陷图表 → 只保 成功率/节省人月/状态流转
4. demo 仓库 → 只埋 3–5 个 Java 问题
5. 最最后:本项目自跑 → 砍掉,只用 demo 仓库演

---

## 十、风险清单

| 风险 | 应对 |
|---|---|
| 容器内 claude+deepseek+MCP 可用 | ✅ **8/2 本地预研三步全通过**(连通 / MCP 查 issue / 真修复)。8/5 容器化降为低风险;**剩余坑**:容器内 `claude` 命令路径、`settings.json`/`mcp.json` 注入生效、**容器网络** |
| 修复质量 / 切方案备案 | deepseek 已验证有效;若 8/5 容器内异常,切回 Anthropic API 直连(FixAction 换实现,状态机不变) |
| **prompt injection / 凭证泄漏** | git 凭证**不进 AI 容器**(临时容器 `http.extraHeader` 注入,用完即毁,决策 12);容器网络出站白名单(只放行 gitea/sonar/deepseek)挡外传;bot-token 最小权限(单仓库 write)限爆炸半径 |
| AI 修复不确定/编译不过 | Verify 兜底 + RestoreAction 整轮回退;demo 仓库保下限 |
| SonarQube 扫描慢 | demo 仓库保持小;演示用已分析结果 |
| Gitea API 不熟 | 文档齐全,阶段 6 专项时间够 |
| **机器 2 核** | 并发度默认 **2**(Fix 占大头是模型对话 I/O、CPU 空闲,多 run 错开可并行;仅 Scan/Verify 的 mvn+sonar CPU 密集且短);一夜 10h 窗口并发 2 可跑 ~16–20 个仓库。16G 内存够 6–8 容器 |
| 单人 52h 极紧 | 守 8/9 里程碑,不通立刻切割;每阶段有验收点防卡死 |

---

## 十一、演示案例规划

- **demo 仓库**(保下限,确定能演):小 Java 项目,埋 5–8 个 sonar 必报问题
  - 空指针风险(`java:S2259`)
  - 资源未关闭(`java:S2095`)
  - 废弃 API 调用(`java:S1123` / `java:S1133`)
  - SQL 字符串拼接(`java:S2077`)
  - 日志拼装(`java:S2629`)
- **本项目自跑**(秀真实价值):NextMorningstar backend 真扫真修,有话题性
- **大屏数据**:修复成功率、节省人月(每 bug 按 20min 计)、高频缺陷 Top 反哺研发培训

---

## 待办与下一步

- [x] 用 **OpenSpec** 把阶段 0–9 拆成可追踪 change(已建 10 个)
- [x] 提供 mcp.json(sonarqube MCP 配置,占位符版)已打进镜像;真 token 运行时 env(`SONARQUBE_TOKEN`)注入
- [x] 提供 settings.json(deepseek 连接,占位符版)已打进镜像;真 key 运行时 env(`MODEL_API_KEY`)注入
- [ ] 提供 **Gitea bot 账号 + 双 token**(admin 授权 + bot write)
- [ ] 提供 demo 仓库(或由我生成脚手架 + 埋漏洞)
- [x] AI 诊断报告通过 `commit_message` 生成(决策 16:基于 SonarQube 数据 + resources 模板,中文)
- [x] 资源池 + 夜间窗口(决策 15,并发默认 2、6:00 清晨清理)
