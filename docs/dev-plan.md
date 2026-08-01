# NextMorningstar · AI 漏洞修复流水线 — 开发计划

> **概念**:程序员下班,AI 上班 —— 夜间无感清洗技术债,早上看 PR 决定是否合并。
>
> **目标定位(8/14 上线)**:演示级端到端闭环 + 真实多仓库 + 夜间无人值守稳定。资源池复用 Spring Async,并发靠容器隔离。本版**不做优先级排序**。
>
> **时间盘**:8/2(周日)缓冲 → 8/14(周五上线)。日历可用 ~57h、阶段工时 52h → ~5h buffer。**全程仅 8/8(六)、8/9(日) 一个完整周末**,是 fix 主力 + 闭环收尾的**决战窗口**,务必保证不被占用。
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
2. 全程可观测——`ActionAttempt` + `ai_report` + commit 留痕,非黑箱
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
| 状态机内核 | `State`(17 态) / `Event` / `StateMachineService`(`synchronized` + Spring 事件) |
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
| 1 | **容器策略** | 预构建 `dev-fix-runtime` 镜像(jdk+maven+node+python+**claude CLI**+sonar-scanner+`.mcp.json`,**不装 git 凭证**),每 run 起一个独立容器,**只跑 claude 改文件 + maven/sonar 构建**。宿主机↔容器靠**共享卷**互通代码;git 由后端执行(决策 12)。容器隔离并发 |
| 2 | **verify 本质** | **不是"编译通过",而是"sonar 重扫后 issue 消失"**。Java build 只为产 `target/classes` 喂给 scanner。sonar 既出题又阅卷 |
| 3 | **scan 方式** | 用 `sonar-scanner` 自己扫(已验证参数:`sonar.java.binaries` / `skipUnresolvedTypeChecks` / 多模块逗号分隔) |
| 4 | **新增 `dev_issue` 表** | 一漏洞一记录、一漏洞一 commit 的载体。Fix→Verify→Submit 串联的关键,**必加** |
| 5 | **优先级** | 本版**不加**,定时任务直接遍历 `enabled` project |
| 6 | **commit 归后端命令行 git** | claude 只改文件;**后端命令行 git** `add -A && commit`(凭证不进容器),保证"一漏洞一 commit" + 规范 message,可控 |
| 7 | **claude 认证** | 容器内 `~/.claude/settings.json` 配 **deepseek(国产模型)** 连接,不用 claude 登录态 |
| 8 | **claude 修复模式** | **claude 通过 sonarqube MCP 自己查 issue+rule 再修复**,平台只喂 issueId + 捕获输出 + commit |
| 9 | **密钥规则排除** ⭐ | ScanAction 加规则黑名单,默认排除凭据类(`java:S2068` / `secrets:*`);Security Hotspots 走独立 API,`/api/issues/search` 天然不返回 → 大部分明文密钥问题自动挡掉(公司内网明文密钥合规) |
| 10 | **代码托管平台** | 演示(8/14)用 **Gitea**(已就绪);生产内网用 **GitLab**,留待内网部署阶段独立实现。**不做抽象层**(YAGNI)——届时目标单一,直接把 Gitea 调用替换为 GitLab;真到两套长期并存再抽接口 |
| 11 | **仓库授权(双 token)** ⭐ | **bot token(`repo write`)由后端命令行 git 使用**(日常 git 凭证,落 credential store);**admin token 仅"加 collaborator"瞬间用、用完即弃**。项目经理启用项目时自动给 bot 加 write、禁用移除。即使都在后端,日常频繁落盘的凭证仍只 write 级、admin 不常驻 → 爆炸半径最小 |
| 12 | **git 归后端命令行 git** ⭐ | git clone/commit/push/revert 由**后端命令行 git**(ProcessBuilder)在共享卷工作区执行,凭证(credential helper/环境变量)**只存后端、不进 AI 容器**。容器只跑 claude 改文件 + maven/sonar 构建,宿主机↔容器靠共享卷互通。submodule 用 `--recursive` 原生支持(规避 JGit 兼容坑)。**prompt injection 偷不到 git 凭证** |
| 13 | **失败 issue 跨 run 记忆** | scan 时排除"近期 FAILED"的 issue(查 `dev_issue` 历史 status=FAILED 且近期),避免每夜反复重试同一个修不好的 issue,提升无人值守效率 |
| 14 | **资源池 + 夜间窗口** ⭐ | 并发度可配(`schedule.max-concurrency`,**默认 2**——Fix 占大头是模型对话 I/O、CPU 空闲,多 run 错开可并行;Scan/Verify 才 CPU 密集且短)。夜间 21:00 自动创建 PENDING run,次日 **6:00 清晨清理**:`cancelOvernightRuns` 取消所有非终态活跃 run(PENDING 直接标 CANCELED,其余走 `requestCancel`)。另每 5min 超时检测(60min 无响应)+ 每 30s 分发调度 |
| 15 | **AI 诊断报告(commit message)** | 由 claude 基于 SonarQube 接口数据(rule/issue 详情)用**中文**生成单 issue 诊断,格式按 `resources/dev/ai-report-template.md`(用户提供);平台读模板 + 喂 sonar 数据给 claude → 输出存 `dev_issue.commit_message` → PR 评论汇总。客观数据 + AI 中文改写,避免直接用英文 rule 描述 |

---

## 三、端到端流程

```
PENDING
 → START      后端建工作区 ~/dev-workspaces/<runId>/repo,docker 起容器挂载之(共享卷)
 → SYNC       后端命令行 git clone --recursive + 切分支(凭证在后端,不进容器)
 → SCAN       容器内 mvn -q compile(产 target/classes)→ sonar-scanner
              → 后端调 /api/issues/search 拉 OPEN issue(按 severity 排序,过规则黑名单)
              → 落 dev_issue 表,截断到 maxFixesPerRun
 → FIX ⭐     逐 issue:容器内 claude+MCP 查 issue/rule 并改文件(共享卷)
              → 后端命令行 git add -A && commit -m "fix(<ruleKey>): <msg>"   # 一漏洞一 commit
 → VERIFY     容器内 mvn -q compile → sonar-scanner 重扫 → 后端查 issue 状态
              → 已关闭=成功;仍 OPEN → 后端 git revert 该 commit + 标记失败
 → SUBMIT     后端命令行 git push 修复分支 → 调 Gitea API 开 PR(auto-delete)+ 评论贴【AI诊断+sonar链接】
 → CLEAN      docker rm -f(工作区卷按需保留/清理)
```

> **取消/失败**:任何阶段失败达重试上限 → `FAILED`;用户点取消 → `CancelTracker` 在下个安全点终止 → 走 `RESTORING`(还原代码)→ `CLEANED`。

---

## 四、关键技术方案

### 4.1 修复容器与镜像

镜像 `dev-fix-runtime:latest`(放 `deploy/dev-fix-runtime.Dockerfile`):
- 基础:`eclipse-temurin:17-jdk`
- 装入:maven、node、python3、**claude code CLI**、sonar-scanner(**不装 git 凭证**——git 操作由后端完成,见决策 12)
- **共享卷**:宿主机 `~/dev-workspaces/<runId>/repo` ↔ 容器 `/workspace/<runId>/repo`,后端 git 与容器 claude/maven 操作同一份代码
- **模型连接可插拔**:`~/.claude/settings.json` + `.mcp.json` 运行时挂载(不写死镜像);开发期(外网)挂 deepseek,内网部署换行内模型——镜像通用

> 🔴 **Day-1 spike(阶段 1)**:容器内跑通 `claude -p "..."`(deepseek + sonarqube MCP),且后端命令行 git 能 clone 到共享卷、容器内可见。全流程前提。

### 4.2 claude + MCP 修复模式(FixAction 核心)

平台不再解析 issue 详情、不拼复杂 prompt。claude 在容器内(共享卷)改文件,后端控制 commit:
```bash
# 1. 容器内 claude 自己查 rule、定位、修复(只打印 rule key)
RULE=$(docker exec <c> claude --permission-mode bypassPermissions --print \
  "使用 sonarqube mcp 的 search_sonar_issues_in_projects 查看 issue <id>,再通过 show_rule 查看对应 rule,仔细阅读后修复。请勿重新格式化其他部分,修复完成后仅打印 rule key,不要加任何描述文字。")

# 2. 后端命令行 git 控制 commit(一漏洞一 commit,凭证不进容器)
git -C ~/dev-workspaces/<runId>/repo add -A
git -C ~/dev-workspaces/<runId>/repo commit -m "fix(<RULE>): <issue消息>"
```
- 单 issue 超时:`claude ... --max-turns N` + 整 run wall-clock(复用 CancelTracker)
- deepseek 最新模型修复效果好,预计不用调 prompt(用户已验证)

### 4.3 sonar 裁判机制(VerifyAction)

修复后 `mvn -q compile` → `sonar-scanner` 重扫 → 查 issue 状态:
```bash
# 修复成功的判定:issue 不再是 OPEN(变 FIXED/已关闭 或 查不到)
curl -s -u "<token>:" "http://<sonar>/api/issues/search?issues=<issueKey>" | jq '.issues[]?.status'
```
- `OPEN` → 修复失败 → `git revert <commitSha>` + `dev_issue.status=FAILED`
- 否则 → `dev_issue.status=VERIFIED`

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
| Gitea **bot token**(git) | 后端 credential store | git clone/push(后端命令行 git) | 高 → 锁后端 |
| Sonar token | **容器内** | sonar-scanner + claude MCP | 中(非代码权限) |
| deepseek key | **容器内** | claude 调模型 | 中(可独立轮换) |

**三层防御:**
1. **凭证隔离**:git 凭证只在后端,AI 容器内没有 → prompt injection 偷不到代码仓库写权限
2. **网络出站白名单**:容器只放行 gitea/sonar/deepseek,其余挡死 → 即使容器内 sonar/deepseek 凭证被偷也外传不出
3. **bot token 最小权限**:单仓库 write,后端凭证文件若泄漏也只限已授权仓库

**为什么 deepseek key 必须进容器(唯一不得不的让步):** claude 必须容器内跑(隔离 AI,不能放后端直接操作),调模型就要 key。让步的是 AI 服务 key(可独立轮换、非代码权限),不是 git 凭证;靠网络白名单兜底。

**为什么弃用 JGit、选命令行 git:** JGit 对 submodule(递归/多认证/LFS)支持差、易踩坑;命令行 git `--recursive` 原生稳定,且命令与原方案几乎一致。

---

## 五、数据模型

### 5.1 新增 `dev_issue` 表

```sql
create table if not exists dev_issue
(
    id                int auto_increment,
    run_id            binary(16)    not null,
    sonar_project_key varchar(128)  not null comment 'sonar 项目键(冗余,避免跨 run 汇总联表)',
    sonar_issue_key   varchar(128)  not null comment 'sonar issue UUID(喂给 claude)',
    sonar_rule_key    varchar(128)  not null comment 'java:S2259',
    sonar_severity    varchar(16)   not null comment 'BLOCKER/CRITICAL/MAJOR/MINOR/INFO',
    sonar_type        varchar(16)   not null comment 'BUG/VULNERABILITY/CODE_SMELL',
    sonar_message     varchar(1024) not null comment 'issue 描述',
    sonar_effort      varchar(16)            comment '修复耗时估算,如 30min',
    status            varchar(16)   not null comment 'SELECTED/FIXED/FAILED/ACCEPTED/REJECTED',
    commit_sha        varchar(64)            comment '修复 commit',
    commit_message    text                   comment 'commit message(替代 ai_report)',
    create_time       datetime,
    update_time       datetime,
    primary key (id),
    unique key uk_run_issue (run_id, sonar_issue_key)
) comment '研发问题表';
```

> 字段统一加 `sonar_` 前缀；`sonar_project_key` 冗余到 issue 减少跨 run 汇总的联表查询；`commit_message` 替代原 `ai_report`(commit message 即诊断载体)；`attempt_no` 移除(不需记录重试次数)。

### 5.2 现有表现状(阶段 0 完工)

- `dev_project`:11 字段 — `id`/`admin_id`/`name`/`link`/`branch_name`/`sonar_project_key`/`description`/`enabled`/`max_fixes_per_run`/`create_time`/`update_time`。
- `dev_run`:8 字段 — `id`/`project_id`/`state`(State 枚举)/`status`(Run.Status 枚举)/`container_id`/`pr_id`/`create_time`/`update_time`。不加 `finished_at`(`update_time` 即可,决策 6)。
- `dev_action_attempt`:7 字段 — `id`/`run_id`/`action_type`/`attempt_no`/`status`/`result`(JSON)/`create_time`/`update_time`。不加 `start_time`/`end_time`(`create_time`/`update_time` 即可,决策 6)。

> `priority` 本版不加(决策 5)。`state` vs `status` 双字段分离:前者追踪流水线阶段(状态机驱动),后者追踪整体结果(观测用),查询仅用 `state`(决策 10)。

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
    severity-whitelist: [BLOCKER, CRITICAL, HIGH]
    rule-blacklist:                 # ⭐ 密钥/凭据类默认排除
      - java:S2068
      - common-java:S2068
      - secrets:*
  runtime:
    image: dev-fix-runtime:latest
    workspace: /workspace
    workspace-host-bind: ~/dev-workspaces  # 宿主机挂载根
    concurrency: 2                          # 资源池:同时跑的 run 数(Fix 是 I/O,2 核可并发 2–3)
  gitea:
    origin: http://<gitea>
    bot-username: morningstar-bot
    bot-token: xxx              # 仅 repo write,后端命令行 git 使用(凭证不进容器)
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

### 阶段 1 · 修复容器 + Docker 接入(~5h)— *8/4–8/5*
- [ ] 写 `deploy/dev-fix-runtime.Dockerfile`(含 claude CLI + sonar-scanner + `.mcp.json` COPY)
- [ ] 🔴 **spike:容器内 `claude -p "..."` 跑通(deepseek + sonarqube MCP)**
- [ ] `StartAction`:docker create/run + 挂载**共享卷**(宿主机 `~/dev-workspaces/<runId>` ↔ 容器 `/workspace/<runId>`)
- [ ] `CleanAction`:docker rm -f
- [ ] 维护 `runId → containerId`(存 `dev_run.container_id`)
- **验收**:Start/Clean 真实起删容器

### 阶段 2 · 代码同步 / 还原(~4h)— *8/4*
- [ ] `SyncAction`:**后端命令行 git** `clone --recursive` + 切分支(到共享卷,凭证在后端)
- [ ] `RestoreAction`:**后端命令行 git** `checkout . && clean -fd`(失败/取消还原用)
- **验收**:共享卷看到克隆代码、容器内可见,且能还原

### 阶段 3 · 漏洞扫描(~3h)— *8/5*
- [ ] `ScanAction`:`mvn -q compile` → `sonar-scanner` → 调 `/api/issues/search`
- [ ] severity 排序 + 规则黑名单过滤(密钥排除)+ 截断 `maxFixesPerRun`
- [ ] 落 `dev_issue` 表
- **验收**:真实仓库拉出 N 条 issue 进表

### 阶段 4 · 漏洞修复 ⭐(~8h)— *8/6–8/7*
- [ ] `FixAction`:遍历 `dev_issue`,逐个 claude+MCP 修复(见 4.2)
- [ ] 平台 `git commit`(一漏洞一 commit),回写 `commit_sha` + `ai_report`
- [ ] 单 issue / 整 run 超时
- **验收**:demo 仓库某空指针,容器内 claude 真能修掉并产出一个 commit

### 阶段 5 · 验证(~3h)— *8/8*
- [ ] `VerifyAction`:`mvn -q compile` → `sonar-scanner` 重扫 → 查 issue 状态(见 4.3)
- [ ] 失败 → `git revert` + `dev_issue.status=FAILED`
- **验收**:故意改坏,Verify 抓到并回滚

### 阶段 6 · PR 提交(~4h)— *8/9*
- [ ] 调研 Gitea API:建分支 / 推 commit / 开 PR / 发评论
- [ ] `SubmitAction`:**后端命令行 git** 推修复分支 → 调 Gitea API 开 PR → 用 4.4 模板写评论
- **验收**:Gitea 出现带 AI 诊断 + sonar 链接的 PR
- 🎉 **端到端闭环跑通**

### 阶段 7 · 前端(配置页 + 大屏)(~8h)— *8/10–8/11*
- [ ] 复用 `frontend/` + system 模块认证
- [ ] 配置页:仓库/分支/启停 CRUD + 手动触发 + 手动停止
- [ ] 大屏:run 实时状态机流转(SSE/WS)、修复成功率、节省人月(20min/bug)、高频缺陷 Top
- **MVP**:大屏先保 3 核心指标,高频缺陷图表有时间再做

### 阶段 8 · 联调 + 演示案例 + 无人值守(~6h)— *8/11–8/13*
- [ ] 造 demo 仓库:埋 5–8 个 sonar 真问题(空指针/资源泄漏/废弃 API/SQL 拼接)
- [ ] 用 NextMorningstar backend 自己跑一轮(真实价值演示)
- [ ] 跑通夜间定时真实触发
- [ ] 修边界:并发、超时、取消、单 run 挂不影响其他

### 阶段 9 · 交付素材(~5h)— *8/14*
- [ ] 作品录屏(端到端全过程 + 大屏)
- [ ] 演示 PPT + 详细介绍材料

---

## 八、排期日历

| 日期 | 星期 | 时段 | 阶段 | 当日必须达成 |
|---|---|---|---|---|
| 8/2 | 日 | 缓冲 | — | ✅ 本地预研三步全通过(claude 连通/MCP 查 issue/真修复) |
| 8/3 | 一 | 4h ✅ | 0 地基 | ✅ 数据模型/Controller/定时骨架/mock 验收,超出预期 |
| 8/4 | 二 | 4h | 0 收尾 + 1 镜像 | Gitea 授权(任务 3) + 写 Dockerfile |
| 8/5 | 三 | 4h | 1 容器 + claude spike | 🔴 **容器内 claude+deepseek+MCP 跑通** |
| 8/6 | 四 | 4h | 2 git + 3 scan 起步 | clone/restore 真实 |
| 8/7 | 五 | 4h | 3 收尾 + 4 fix 起步 | 拉 issue 落表 |
| 8/8 | 六 | 8h ⭐ | 4 fix 主力 | demo 单 issue 真修复 + commit |
| 8/9 | 日 | 8h ⭐ | 5 verify + 6 submit + 联调 | **Gitea PR → 闭环跑通** 🎉 |
| 8/10 | 一 | 4h | 7 前端 | 配置页 + 大屏起步 |
| 8/11 | 二 | 4h | 7 收尾 | 大屏完成 |
| 8/12 | 三 | 4h | 8 联调 | demo 仓库 + 本项目真跑 |
| 8/13 | 四 | 4h | 8 + buffer | 无人值守 + 修 bug |
| 8/14 | 五 | 5h | 9 | 录屏 + PPT + 材料 |

### 8/2 缓冲日建议(可选,不写平台代码,只做风险前置) — ✅ 8/2 已完成,三步全通过

核心命门是 **8/4 的容器内 claude spike**。若 8/2 有零碎精力,在**本地**(先不进容器)验证这条链能跑通,可把头号风险提前暴露:
```bash
# 1. 确认 claude code CLI 装好、deepseek 连接配好
claude --permission-mode bypassPermissions --print "ping"
# 2. 确认 sonarqube MCP 配好,能查 issue
claude --permission-mode bypassPermissions --print "使用 sonarqube mcp 的 search_sonar_issues_in_projects 查看 demo-project-backend 的 issue,返回前 3 个 id 的 json 数组"
# 3. 拿一个真 issue 让它修(在 demo 仓库工作目录里跑)
claude --permission-mode bypassPermissions --print "使用 sonarqube mcp 查看 issue <id> 与 rule,修复它,勿格式化其他部分,修完只打印 rule key"
```
跑通这三步 → 8/4 只需"把这套搬进容器",风险大降。跑不通 → 8/3 就有时间切方案(如换模型/API 直连),不至于 8/4 才炸。

---

## 九、MVP 切割线(落后时按序砍)

1. vue/python 的 verify → 跳过(MVP 只 Java `mvn compile`)
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
| **prompt injection / 凭证泄漏** | git 凭证**不进 AI 容器**(后端命令行 git,决策 12);容器网络出站白名单(只放行 gitea/sonar/deepseek)挡外传;bot-token 最小权限(单仓库 write)限爆炸半径 |
| AI 修复不确定/编译不过 | Verify 兜底 + git revert;demo 仓库保下限 |
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
- [ ] 提供 `.mcp.json`(sonarqube MCP 配置),阶段 1 打进镜像
- [ ] 提供 deepseek 的 `~/.claude/settings.json` 连接方式
- [ ] 提供 **Gitea bot 账号 + 双 token**(admin 授权 + bot write)
- [ ] 提供 demo 仓库(或由我生成脚手架 + 埋漏洞)
- [x] AI 诊断报告通过 `commit_message` 生成(决策 15:基于 SonarQube 数据 + resources 模板,中文)
- [x] 资源池 + 夜间窗口(决策 14,并发默认 2、6:00 清晨清理)
