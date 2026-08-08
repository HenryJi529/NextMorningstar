# NextMorningstar · AI 漏洞修复流水线 — 架构精彩设计报告

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

## 三、Sonar 裁判闭环：扫描与验证镜像对称

pipeline 的结构视觉感很强——这不是偶然。

```
 START → SYNC → [Scan] → [Fix] → [Verify] → SUBMIT → CLEAN
                  │        │        │
                  └─ 出题 ─┴─ 做题 ─┴─ 阅卷 ┘
                  发现 N 个    逐个修复    重扫判定
```

**Scan：** `mvn compile → sonar-scanner → API 拉取 OPEN issue`，Sonar 是"出题人"。

**Verify：** `mvn compile → sonar-scanner → API 查询 issue 状态`，Sonar 是"阅卷人"。

左右镜像，同一种工具，不同角色。不需要 AI 自评修复质量——全是客观数据。这个闭环是汇报中最容易被记住的结构。

---

## 四、18 态状态机：一条主链、一个回退环、一个终态

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
2. **`maxFixesPerRun=1` 等效替代。** 状态机不改，把每批修复数降到 1，行为上完全等价于 issue 级隔离，但架构上不引入额外状态层。

**结论：** issue 级状态机不是"后面对齐再做"，而是评估后明确拒绝的方向。同一套简单状态机靠调参覆盖全部行为范围。

---

## 五、不浪费 AI 算力的正确方式：三层防线而非扭曲状态机

### 5.1 一度想做的方案（评估后明确拒绝）

曾考虑 **逐 commit 精准保留**：Verify 失败时 `git reset --hard <最早失败 commit^>` 保留修好的 commit → 只重修失败的 issue → 重试耗尽后强制 SUBMIT 已有成果。这个方向会引入链式复杂度：

- VerifyAction 膨胀：从纯判定变为定位最早失败 + 执行 reset + chown
- RestoreAction 分裂：verify 失败只清理不切分支，cancel 完整还原——两条路径
- RestoredTrigger 四分支：需判断失败来源 × 重试次数
- FixAction 乱序：失败 issue 排到队尾以避免永远卡在同一个 issue

**结论：** 逐 commit 保留不是"后面对齐再做"，而是评估后明确拒绝。理由有三：状态机从编排引擎退化为业务决策引擎；节省的是夜间 AI 时间（不稀缺）；防线由决策 14（跨 run 记忆）和 `maxFixesPerRun` 窗口承担。

### 5.2 替代方案：三层防线，不改状态机

| 层 | 机制 | 作用 |
|---|---|---|
| 1 | AI 修复能力验证 | 8/2 预研三步全过（连通/MCP 查 issue/真修复），修复质量可靠 |
| 2 | `maxFixesPerRun` 窗口 | 能力下降时降低每批修复数——同一状态机，调参即可 |
| 3 | 决策 14（跨 run 记忆） | 本轮修不了的 issue 下轮自动排除，不重复占用重试次数 |

**核心洞察：** `maxFixesPerRun` 是复杂度调杆。设为 10 是批量模式（吞吐高），设为 1 等效 issue 级隔离——状态机零改动。调参不用动架构。

### 5.3 分支方案悖论

每个 issue 独立验证需要 `mvn compile + sonar-scanner`，整个 Scan 也是 `mvn compile + sonar-scanner`。一个发现 N 个问题，一个验证 1 个问题——两者同代价。发现比验证还便宜，这是反直觉的架构异味。整轮回退恰好避开了这个悖论。

---

## 六、命名确定性：不把 Docker 状态存入数据库

容器名 `morningstar_dev_sandbox_<runId>` 和 volume 名 `morningstar_dev_repo_<projectId>` 均由 ID 确定性推导。数据库中不存 `container_id`——DB 是冗余副本，Docker daemon 才是真实数据源，两者会漂移。能推导就不存。

**与之配套的失败语义：** Action 失败统一 catch `ProcessExecutionException` 返回 FAILED 结果，不裸抛。裸抛意味着 `AbstractAction` 无兜底，attempt 停在 RUNNING、run 卡在中间态占并发槽——只能等 60 分钟超时兜底。

---

## 七、Gitea 双视角地址

同一 Gitea 实例，不同消费者看不同的地址：

| 配置 | 消费者 | dev 值 | 为什么不同 |
|---|---|---|---|
| `public-origin` | 后端 API + PR 链接 + 浏览器 | `http://127.0.0.1:7001` | Mac 宿主机不解析 `host.docker.internal` |
| `container-origin` | 临时 git 容器 clone/fetch/push | `http://host.docker.internal:7001` | 容器内无法访问 `127.0.0.1` |

每环境两值显式配置，不隐式回退。生产两者同为公网域名——冗余但不隐藏差异。

---

## 八、故障隔离：CleanAction 的破环逻辑

`FailedTrigger` 中有一个反直觉的判断：FAILED 状态如果来自 CLEANING 阶段，不再发 CLEAN 事件。**否则 CLEAN 失败 → FAILED → 自动再发 CLEAN → 再失败 → ... 无限循环刷 `action_attempt` 表。**

同理，`CleanAction` 对 "No such container" 返回成功——目标既是"容器不存在"，已不存在 = 目标已达成。

---

## 九、演进路径：MVP 刻意放过的

| MVP 不做 | 理由 |
|---|---|
| 优先级排序 | 夜间窗口资源充足，先到先修即可 |
| GitLab 适配 | 演示用 Gitea 已就绪；生产替换时再实现 |

---

## 关键决策索引

| 决策 | 位置 | 摘要 |
|---|---|---|
| 安全模型（三层防御） | dev-plan 决策 12-13 | git 凭证不进 AI 容器，prompt injection 偷不到写权限 |
| Sonar 裁判闭环 | dev-plan 决策 2 | 既出题又阅卷，全客观判定 |
| 整轮回退 vs 精准保留 | fix-runtime-container 决策 18 | 四组论据论证整轮回退是正确设计 |
| `maxFixesPerRun` 复杂度调杆 | fix-runtime-container 决策 18 §四 | 调参不动架构 |
| 失败 issue 跨 run 记忆 | dev-plan 决策 14 | 防每夜重复尝试修不好的 issue |
| 命名确定性 | dev-plan 决策 18 | 不把 Docker 状态存 DB，能推导就不存 |
| 状态机编排隔离 | fix-runtime-container 决策 16 | RestoredTrigger 是唯一分支点 |
| Gitea 双视角地址 | dev-plan 决策 19 | 容器内外看不同 URL |
| CleanAction 破环 | dev-plan 决策 18 | 防止 CLEAN 失败→无限循环 |
| 双 token 最小权限 | dev-plan 决策 12 | admin 和 bot 分离，爆炸半径最小 |
