# NextMorningstar · AI 代码质量优化平台 — 架构精彩设计报告

> **目标:** 梳理值得在汇报中重点展示的设计决策与架构取舍。每一条都有"为什么"和"弃了什么"。

---

## 一、元叙事：用「人主导 + AI 辅助」开发「AI 自主 + 人工门」产品

本项目有两层人-AI 合作，既是设计准绳，也是汇报主线。

**运行时：AI 全自动 + 人工合并门。** AI 夜间无人值守完成 扫描→修复→验证，但最终合并到主干的权力永远在人的手里。在自动化谱系中卡在"AI 辅助"与"全自主"的中间位置——既有 AI 的效率，又守住了生产安全的底线。

**开发时：人主导 + AI 辅助。** 本项目自身由"人 + Claude Code"协作构建。核心 Action 与状态机代码由人牢牢握住，AI 承担样板/调试/验收/review。这两层合作互为镜像，使产品自身即为方法论的证明。

> **汇报金句：** 用「人主导 + AI 辅助」的方式，开发出一个「AI 自主 + 人工门」的产品——这本身即证明 AI 不替代人，而是在人划定的边界内，把人从重复劳动中解放出来。

---

## 二、安全模型：「能改仓库代码的凭证，绝不进 AI 容器」

这是整个系统最核心的安全设计。三条防线逐层收紧。

**第一层：凭证隔离。** Git 凭证（bot token + admin token）只在后端内存，每次使用时由 `ProcessBuilder` 以 `-c http.extraHeader` 注入临时 alpine/git 容器——当次生效、用完即毁。URL 中不拼 token（不进 volume 里的 `.git/config`），容器销毁后物理上不可恢复。AI 容器内零 git 凭证 → prompt injection 偷不到仓库写权限。

**第二层：最小权限。** 两个 token 各司其职：admin token 只在"加/删 collaborator"的瞬间使用；bot token 只有单仓库 write 权限。爆炸半径被压缩到单个仓库。

**第三层：网络出站白名单。** 容器只放行 Gitea/SonarQube/DeepSeek 三个目标，其余挡死。即使容器内凭证（sonar token、model key）被偷，也外传不出。

**实现细节：** 所有 git 操作由后端通过 `docker run --rm` 起临时 alpine/git 容器执行。clone URL 用无凭证形式（`<host>/<owner>/<repo>.git`），token 通过 `git -c http.extraHeader=Authorization: token <value>` 当次生效——**不拼进 remote URL**。如果拼进去，git 会把 token 原样写入 volume 里的 `.git/config`，持久化泄露。容器用完即毁（`--rm`），token 物理上不可恢复。所有 git 命令统一加 `-c safe.directory=/workspace/repo`——volume 属主是 bot 用户，但 git 容器以 root 运行，git 安全检查会报 "dubious ownership"。

**唯一不得不的让步：** DeepSeek API key 必须进容器（AI 要在容器内跑，调 API 就要 key）。但这是可独立轮换的非代码权限，非 git 凭证。

---

## 三、双通道发现 + 两道防线验证

### 3.1 ScanAction：双通道并行扫描

```
ScanAction:
  ├─ Maven 构建: find pom.xml → mvn -q compile（阿里云镜像，|| true best-effort）
  ├─ SonarQube 通道: sonar-scanner → RestClient 翻页拉 OPEN issue → /api/rules/show 拿规则描述
  │   └─ impacts 数组 → 三维 severity → InScopeSeverities.sonar 过滤 → 随机打乱截断
  └─ AI Discovery: heredoc 写 prompt + schema 文件 → claude --print --output-format json --json-schema
      └─ structured_output 提取 → AiIssue 反序列化 → InScopeSeverities.ai 过滤 → 随机打乱截断
  → 双通道统一入库(issueMapper.insert)
```

**SonarQube 做已知模式识别**（空指针、SQL 注入、资源泄漏等规则化问题），**Claude 做语义理解**（上帝类、竞态条件、N+1 查询等规则引擎抓不到的）。两者互补，不是替代。

**关键技术点**：
- `sonar.java.binaries` 设为 `**/target/classes`（单模块项目使用通配即可）
- Maven 阿里云镜像通过 Dockerfile COPY `settings.xml` → `/workspace/maven-settings.xml`，构建时通过 `-s` 指定
- AI 输出使用 `claude --json-schema` + `--output-format json` structured output，后端从 `structured_output` 字段反序列化，不依赖模型格式自觉
- AI prompt 从 `AiMetadata.Type` 枚举自动生成类型列表，从 `Issue.Severity` 生成取值列表，永不过期
- 选择策略：随机打乱 → 截断，不做 severity 排序（避免每轮都是同一批老问题）、不做去重（双通道问题类型不同）、不做跨 run 排除（先验证修复能力）

AI Discovery 产出的问题自带诊断——`description`（为什么是问题）、`suggestion`（怎么修）、`type`（21 种 AiIssueType 分类，从 GOD_CLASS 到 RACE_CONDITION）。信息自包含，不需要外部知识库。

### 3.2 FixAction：统一 prompt + MCP 自查

ScanAction 阶段已把全部诊断信息存入 issue 字段。FixAction 对所有 issue 使用同一 prompt 模板——Claude 从 `title`/`codeSnippet`/`metadata` 获取上下文即修，修复完成后调用 sonarqube MCP 的 `analyze_code_snippet` 自查修改文件，确保不引入新 issue。

**commit_message 只让 AI 做它该做的。** Claude 修复后吐 `{subject, body}` 两字段 JSON——subject 一句话总结、body 修复思路，后端拼成 `subject\n\nbody` 交 git commit。曾考虑让 AI 同时输出 `verification`（自述怎么验证）和 `risk`（修复风险），都砍掉：验证是 VerifyAction 的职责，AI 自述验证不可靠又越权（活没干完就 preemptive 描述怎么验收）；risk 同理——同一模型刚改完代码就评自己的风险，没有信息增量。模板内嵌代码（text block），不引用外部模板文件，少一个运行时依赖。

**失败要能诊断卡在哪。** `FixResult` 不记单一 `fixedIssueNum`，而是按 source 双计数 `fixedSonarIssueNum`/`fixedAiIssueNum`——失败时一眼看出修了几个、卡在 SonarQube 通道还是 AI 通道。任一 issue 失败即整轮 `FIX_FAILED`，不做逐 commit 精准保留（理由见第六节）。

### 3.3 VerifyAction：两道防线

```
VerifyAction:
  ① SonarQube 重扫 → 客观判定（修的 issue 全 CLOSED + 无回归）
  ② Claude review  → 语义验证（思路对不对 + 实现到不到位）
  两道都过 → VERIFIED
```

第一道是硬数字（数量对比检测回归：重扫 issue 数 > 原扫描数 - 已修复数 → 存在回归），第二道是语义判定。**SonarQube 做客观门槛，Claude 做语义评审**——不是"自己出题自己判"，而是各司其职。

**第二道不喂 diff，让 Claude 自己读代码。** 容器内 claude code CLI（working dir `/workspace/repo`）本就能 `cat` 文件，喂 diff 是冗余。改用 FixAction 留下的 `commitMessage`（`{subject,body}`）承载"修复思路"，第二道两维度判定：① commitMessage 描述的思路逻辑上能否解决原问题 ② 当前代码是否按该思路正确修改且真正消除问题。逐条 review + fail-fast 短路，任一判 `{"verified":false}` 整轮回退。

**输出最小 JSON，砍 reason。** Claude 只回 `{"verified":true/false}`——失败即整轮回退（issue 回 SELECTED）+ 下轮自动排除，无人看 reason；调试看 `log.info(rawOutput)`。砍 reason 省 token、降复杂度，失败 message 用固定文案。

### 3.4 SonarQube 对用户透明

issue 入库后不再区分来源。PR body 统一格式：title + 三维 severity + 代码片段链接(跳转源码对应行) + 修改记录链接(跳转 commit)，AI 分支额外 type/description。用户看不到 SonarQube 原始 API 数据，只看到结构化的中文诊断报告。

---

## 四、数据模型：Source 区分器 + 三维 Severity

`dev_issue` 承载所有问题记录，无论来自 SonarQube 还是 AI Discovery：

```sql
source                   VARCHAR(16)   -- SONAR / AI
metadata                 JSON           -- SonarMetadata 或 AiMetadata（@JsonTypeInfo 多态）
title                    VARCHAR(1024)
reliability_severity     VARCHAR(16)    -- BUG(缺陷)
security_severity        VARCHAR(16)    -- VULNERABILITY(安全漏洞)
maintainability_severity VARCHAR(16)    -- CODE_SMELL(代码异味)
```

**source 区分器**：`@JsonTypeInfo(property = "@source")`，和 `ActionResult` 同模式——JSON 列内嵌多态子类（`SonarMetadata` 含 issueKey/ruleKey，`AiMetadata` 含 type 分类）。

**三维 severity 独立**：SonarQube 的三质量维度（Reliability/Security/Maintainability）各自独立评分。一个 issue 可以同时是 BLOCKER 级别的安全漏洞和 MEDIUM 级别的代码异味——这不是反规范化，是正确建模。

**AiIssueType 分类体系**：21 种 AI 可识别的代码问题类型，分六大类（架构/逻辑/安全/可维护/性能/并发）+ OTHER 兜底。每个类型带中文描述，既是 AI prompt 里的分类指引，也是 PR 报告里的用户可读标签。

**不做的事**：不加唯一约束——ScanAction 插入前删除本 run 旧数据，业务逻辑保证不重复。不设 `rule_key` 顶层列——对 AI Discovery 没用，对 SonarQube 存 metadata 里即可。

---

## 五、18 态状态机：一条主链、一个回退环、一个终态

每两个状态形成一个 Action 的执行区间（`...ING → ...ED`），Trigger 自动驱动：

```
PENDING → [STARTING → STARTED] → [SYNCING → SYNCED] → [SCANNING → SCANNED]
       → [FIXING → FIXED] → [VERIFYING → VERIFIED] → [SUBMITTING → SUBMITTED]
       → [CLEANING → CLEANED]
                                          ↑
       [FIXING/VERIFYING 失败] → [RESTORING → RESTORED] ─┘（重试）
                                          ↓（耗尽）
                                       FAILED → CLEAN
```

**RestoredTrigger** 是唯一的分支点：根据时间戳区分"fix 失败"还是"verify 失败"，各自检查重试上限，决定续修或放弃。总共 50 行代码，没有嵌套状态机，没有编译期不可见的隐式行为。

**曾考虑 issue 级独立状态机但否决。** 每个 issue 独立跑 `FIXING → VERIFYING → RESTORING → FIXING` 看似优雅，但引入两个根本问题：

1. **嵌套状态机。** run 级状态机下再挂 N 个 issue 级状态机——编排复杂度翻倍，Trigger 要同时监听两层事件。
2. **`maxSonarIssuesPerRun=1` + `maxAiIssuesPerRun=1` 等效替代。** 状态机不改，把每批修复数降到 1，行为上完全等价于 issue 级隔离，但架构上不引入额外状态层。

**结论：** issue 级状态机不是"后面对齐再做"，而是评估后明确拒绝的方向。同一套简单状态机靠调参覆盖全部行为范围。

**issue 状态机也因此精简到三态。** 既然整轮回退、不做 issue 级状态机，issue 的 `Status` 就只剩 `SELECTED → FIXED → VERIFIED` 的流转——任一阶段失败（fix 或 verify）都整轮回 `SELECTED`，不标 `FAILED`。fail-fast 下 `FAILED` 永无写入点（失败抛异常、不落状态），是死状态，直接从枚举删除。`ACCEPTED`/`REJECTED` 预留给 SUBMIT 后的人工终态。少一个永远不会出现的状态，枚举即文档——读代码的人不会困惑"这个状态什么时候出现"。

**PR 结果也走字段，不进状态机。** run 提交 PR 后到 `CLEANED` 终态，但 PR 还在 Gitea 等人评审——合并或拒绝是几小时甚至几天后的事。这个"迟到的观测"不塞进状态机（`CLEANED` 是终态，后面再加 `MERGED`/`REJECTED` 会污染终态语义），而是用 `Run.prStatus` 字段（OPEN/MERGED/CLOSED）记录，定时任务轮询 Gitea API 回写；issue 的 `ACCEPTED`/`REJECTED` 终态也在此落定。观测归观测、流程归流程——状态机只管"下一步干什么"，不管"外部世界后来怎么了"。（8/13 已实现：`syncPrStatus(runId)` 抽成 service 方法，`sync-pr-status-cron` 定时遍历 + `getRun` 详情实时同步，回写 `ACCEPTED`/`REJECTED`。）

---

## 六、不浪费 AI 算力的正确方式：三层防线而非扭曲状态机

### 6.1 一度想做的方案（评估后明确拒绝）

曾考虑 **逐 commit 精准保留**：Verify 失败时 `git reset --hard <最早失败 commit^>` 保留修好的 commit → 只重修失败的 issue → 重试耗尽后强制 SUBMIT 已有成果。这个方向会引入链式复杂度：

- VerifyAction 膨胀：从纯判定变为定位最早失败 + 执行 reset + chown
- RestoreAction 分裂：verify 失败只清理不切分支，cancel 完整还原——两条路径
- RestoredTrigger 四分支：需判断失败来源 × 重试次数
- FixAction 乱序：失败 issue 排到队尾以避免永远卡在同一个 issue

**但复杂度还是次要的——根本原因是回归。** 精准保留会跨轮拼接修复集：已保留的成功修复（上一轮）和这轮重修失败 issue 的修复，在不同基线、不同上下文产生，可能互相踩踏——重修 issue B 的改动覆盖或破坏上一轮 issue A 已修好的代码（同文件/同模块的 issue 尤甚），凭空引入新回归。全量回退则保证每个 PR 的修复集是**原子的**：每一轮从干净 `origin` 重新修所有 issue，所有修复在同一轮、同一基线、同一上下文产生，彼此协调一致，不存在"跨轮拼凑"的缝隙。宁可重修已修好的 issue 浪费算力，也不拼凑跨轮修复冒回归风险——夜间 AI 时间不稀缺，一个带回归的 PR 合进主干，代价远大于重修。

**结论：** 逐 commit 保留不是"后面对齐再做"，而是评估后明确拒绝。**核心理由是质量**：全量回退保证修复集原子性、回归比例更低（跨轮拼凑的修复会互相踩踏）；其次是复杂度（状态机从编排引擎退化为业务决策引擎）和效率认知（节省的是夜间 AI 时间，不稀缺）；防线由决策 14（跨 run 记忆）和 `maxIssuesPerRun` 窗口承担。

### 6.2 替代方案：三层防线，不改状态机

| 层 | 机制 | 作用 |
|---|---|---|
| 1 | AI 修复能力验证 | 8/2 预研三步全过，修复质量可靠 |
| 2 | `maxIssuesPerRun` 窗口 | 能力下降时降低每批修复数——同一状态机，调参即可 |
| 3 | 决策 14（跨 run 记忆） | 本轮修不了的 issue 下轮自动排除，不重复占用重试次数 |

**核心洞察：** `maxSonarIssuesPerRun` + `maxAiIssuesPerRun` 是复杂度调杆。设为 10+5 是典型模式，都设为 1 等效 issue 级隔离——状态机零改动。调参不用动架构。

### 6.3 分支方案悖论

每个 issue 独立验证需要 `mvn compile + sonar-scanner`，整个 Scan 也是 `mvn compile + sonar-scanner`。一个发现 N 个问题，一个验证 1 个问题——两者同代价。发现比验证还便宜，这是反直觉的架构异味。整轮回退恰好避开了这个悖论。

---

## 七、命名确定性：不把 Docker 状态存入数据库

容器名 `morningstar_dev_sandbox_<runId>` 和 volume 名 `morningstar_dev_repo_<projectId>` 均由 ID 确定性推导。数据库中不存 `container_id`——DB 是冗余副本，Docker daemon 才是真实数据源，两者会漂移。能推导就不存。

**与之配套的失败语义：** Action 失败统一 catch `ProcessExecutionException` 返回 FAILED 结果（带语义 message）。即便不主动 catch，`AbstractAction` 全局兜底（dev-plan 决策 37，`execute()` 包 `catch(Exception)`）也会把异常转 FAILED——但那时 message 只剩 `e.toString()`、可读性差，故各 Action 仍主动 catch 已知异常带语义。

---

## 八、Gitea 双视角地址

同一 Gitea 实例，不同消费者看不同的地址：

| 配置 | 消费者 | dev 值 | 为什么不同 |
|---|---|---|---|
| `backend-origin` | 后端 API + PR 链接 + 浏览器 | `http://127.0.0.1:7001` | Mac 宿主机不解析 `host.docker.internal` |
| `container-origin` | 临时 git 容器 clone/fetch/push | `http://host.docker.internal:7001` | 容器内无法访问 `127.0.0.1` |

每环境两值显式配置，不隐式回退。生产两者同为公网域名——冗余但不隐藏差异。

---

## 九、故障隔离：CleanAction 的破环逻辑

`FailedTrigger` 中有一个反直觉的判断：FAILED 状态如果来自 CLEANING 阶段，不再发 CLEAN 事件。**否则 CLEAN 失败 → FAILED → 自动再发 CLEAN → 再失败 → ... 无限循环刷 `action_attempt` 表。**

同理，`CleanAction` 对 "No such container" 返回成功——目标既是"容器不存在"，已不存在 = 目标已达成。

---

## 十、平台管理员：熔断权与所有权分离

平台跑起来后会出现归属人处理不了的运维场景：run 卡死占着并发槽（全局仅 2 个）、项目配置错误每晚反复失败刷表。这要求存在一个"超级用户"——但超级用户该有多大权力，是一道边界设计题。

**设计定稿：管理员只有熔断权，没有所有权。** 角色 `dev_admin` 能做的事恰好两件：取消**任何**正在运行的 run、停用**任何**项目（只写 `enabled=false`)。不能做的事同样明确：无 enable 端点（恢复权归 owner，避免"管理员停用 ↔ owner 启用"的拉锯）、无配置编辑权、不级联取消进行中 run（停用只挡未来调度，不打断现场）。

**实现上零新体系。** 直接复用平台既有权限框架：`application-perm.yml` 声明角色与权限点（`dev:run:cancel`/`dev:project:disable`)，启动时 insert-if-absent 同步，登录时装入 authorities，接口上一个 `@PreAuthorize` 注解收尾。管理员接口独立 `/dev/admin/**` 命名空间，不改造 owner 接口——owner 的取消/停用逻辑一行没动，两套入口各管各的校验。

**三层身份分离。** owner（项目归属人，平台自跑项目归专门服务账号 `morningstar-nightly`)/ bot(Gitea 侧提交 PR 的 HaibaraAi369)/ 平台管理员（熔断者）各司其职，任一身份被攻破或滥用，爆炸半径都被限制在自己的职责内——与第二节"最小权限"一脉相承。

**弃了什么：** 独立的权限体系（重复造轮子）、`disabled_by` 字段（MVP 不需要区分"谁停的")、级联取消（越权且危险）、审计表（降级为 `log.info` 留痕，表结构在 admin-operations design 留档，出现争议再升级）。管理员角色也不被禁止创建项目——那是无用守卫，真实约束来自接口语义而非身份封锁。

---

## 十一、演进路径：MVP 刻意放过的

| MVP 不做 | 理由 |
|---|---|
| 优先级排序 | 夜间窗口资源充足，先到先修即可 |
| GitLab 适配 | 演示用 Gitea 已就绪；生产替换时再实现 |
| 管理员操作审计表 | `dev_admin_operation` 表结构已在 admin-operations design 决策 6 留档；MVP 降级为 `log.info` 留痕，出现争议再升级 |
| 管理员 enable 端点 | 熔断权 vs 所有权分离：管理员只写 `enabled=false`，恢复权归 owner；拉锯风险 MVP 接受 |

---

## 关键决策索引

| 决策 | 位置 | 摘要 |
|---|---|---|
| 安全模型（三层防御） | dev-plan 决策 12-13 | git 凭证不进 AI 容器，prompt injection 偷不到写权限 |
| 双通道发现 | sonar-issue-scan proposal | SonarQube 规则引擎 + Claude 语义审查，互补非替代 |
| 统一修复 prompt + MCP 自查 | claude-issue-fix design 决策 1 | ScanAction 已存储全部诊断信息，修复后 MCP `analyze_code_snippet` 兜底 |
| commit_message {subject,body} 克制 | claude-issue-fix design 决策 4 | 只让 AI 描述修复，不自述验证/风险（越权且无信息增量）|
| 两道防线验证 | sonar-rescan-verify design 决策 1 | SonarQube 客观门槛 + Claude 语义判定 |
| VerifyAction 不喂 diff + 砍 reason | sonar-rescan-verify design 决策 3/4 | Claude 自己读代码 + commitMessage 判思路，输出最小 `{verified}` |
| Source 区分器 + 三维 severity | pipeline-foundation design 决策 1 | JSON 多态 metadata，B/S/M 三维独立 |
| SonarQube 对用户透明 | gitea-pr-submit design 决策 2 | PR body 诊断报告，不区分来源 |
| 整轮回退 vs 精准保留 | fix-runtime-container 决策 18 | 四组论据论证整轮回退是正确设计 |
| `maxIssuesPerRun` 复杂度调杆 | dev-plan 决策 28 | Sonar/AI 分别配置，随机打乱截断，调参不动架构 |
| ~~失败 issue 跨 run 记忆~~ | dev-plan 决策 14 → 28 | 永不做——先验证修复能力，不应回避困难 |
| ScanAction 选择策略 | dev-plan 决策 28 | 随机打乱→截断，不做去重/黑名单/跨run/severity排序 |
| AI JSON 输出解析 | dev-plan 决策 29 | `--json-schema` + `--output-format json` structured output，后端解析 `structured_output` |
| Maven 阿里云镜像 | dev-plan 决策 30 | Dockerfile COPY settings.xml，构建时写入 |
| 命名确定性 | dev-plan 决策 18 | 不把 Docker 状态存 DB，能推导就不存 |
| 状态机编排隔离 | fix-runtime-container 决策 16 | RestoredTrigger 是唯一分支点 |
| issue 状态三态精简 | dev-plan 决策 31 | 删 FAILED 死状态，fail-fast 整轮回退，枚举即文档 |
| PR 结果用 prStatus 不进状态机 | dev-plan 决策 34 | 观测归观测、流程归流程，CLEANED 终态不污染 |
| Gitea 双视角地址 | dev-plan 决策 19 | 容器内外看不同 URL |
| CleanAction 破环 | dev-plan 决策 18 | 防止 CLEAN 失败→无限循环 |
| 双 token 最小权限 | dev-plan 决策 12 | admin 和 bot 分离，爆炸半径最小 |
| 读公开、写私有权限模型 | dev-plan 决策 38 | 读接口去 adminId 公开（配置参考 + 面板），写接口保留；deleteProject 活跃 run 守卫 |
| 失败分支清理决定不做 | dev-plan 决策 39 | 删分支危险且越界，平台只观测不删除，残留人工清理 |
| triggerRun 单飞 | dev-plan 决策 40/46 | 一项目一 run；手动触发改混合并发槽（有槽直启、满槽排队等 dispatch），并发上限故事闭环 |
| 前端契约对齐 | dev-plan 决策 43 | 以后端 Jackson 序列化规则为准：`non_null` 下可空字段标 `?:`，UUID→string，枚举对齐 `name()` |
| 平台管理员权限模型 | dev-plan 决策 44 | 复用既有权限框架不新建体系；熔断权 vs 所有权分离；三层身份（owner/bot/管理员）|
| Stats 用 Integer 不用 Long | dev-plan 决策 45 | 全局 `Long→ToStringSerializer` 会把 Long 序列化成 string，与前端 `number` 冲突 |
| deliveredIssueCount 口径 | dev-plan 决策 45 | 只算 SUCCEEDED run 的 VERIFIED/ACCEPTED/REJECTED；REJECTED 计入——修复数衡量 AI 能力，接受度归合并率 KPI |
| Stats 只出原子计数 | dev-plan 决策 45 | 合并率/占槽比前端算；`maxConcurrency` 是面板唯一需要的配置项（占槽分母），runTimeout/cron 不进 Stats |
| FAILED 一律算活跃 | dev-plan 决策 46 | 躺平 FAILED 容器现场未知，必须占槽+挡触发，防同项目双 run 抢 volume |
| Detail bo 展示扩充 | dev-plan 决策 47 | PO 不沾染展示字段；`@SuperBuilder` 继承 + 统一 `toDetail`，前端类型同构继承；RunDetail 含 actionAttemptBriefs 阶段流水（可视化数据源），CopyUtil 健壮化支持跨类同名拷贝 |
| Run 触发方式入 PO | dev-plan 决策 48 | `triggerType`(MANUAL/SCHEDULED）是真实属性不进 Detail；写入点收敛 `createRun`,调用方用 enum 声明 |
| 前端三页 IA 与可视化定稿 | dev-plan 决策 49 | 我的项目/系统管理/平台说明；历史任务单表不拆 PR；回退环弧线表达 RESTORING 回路；失败徽章/耗时由 actionAttemptBriefs 前端聚合 |
