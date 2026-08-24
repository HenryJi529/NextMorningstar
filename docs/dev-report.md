# 代码工坊 · AI 自动值守的代码质量优化流水线

> **概念:** 程序员下班，AI 上班 —— 夜间无感清洗技术债，早上看 PR 决定是否合并。

> **目标:** 梳理值得在汇报中重点展示的设计决策与架构取舍。每一条都有"为什么"和"弃了什么"。

---

## 一、元叙事：用「人主导 + AI 辅助」开发「AI 自主 + 人工门」产品

本项目有两层人-AI 合作，既是设计准绳，也是汇报主线。

**运行时：AI 全自动 + 人工合并门。** AI 夜间无人值守完成 扫描→修复→验证，但最终合并到主干的权力永远在人的手里。在自动化谱系中卡在"AI 辅助"与"全自主"的中间位置——既有 AI 的效率，又守住了生产安全的底线。

**开发时：人主导 + AI 辅助。** 本项目自身由"人 + Claude Code"协作构建。核心 Action 与状态机代码由人牢牢握住，AI 承担样板/调试/验收/review。这两层合作互为镜像，使产品自身即为方法论的证明。

**前后端把控程度不同，是刻意分工。** 后端（Java/Spring Boot/状态机）由开发者严格掌控：状态机流转、Action 边界、安全模型、权限点、数据一致性这些决定产品能否正确且安全地改代码，bug 成本极高（可能越权、写坏仓库、状态机死锁），必须由人把住。AI 在后端只承担样板代码、单测骨架、调试辅助、review 提示。前端（Vue3/TypeScript/Tailwind）则让 AI 承担更多实现工作：页面布局、组件组装、样式微调、轮询与分页逻辑；人负责 IA 定稿、关键交互拍板、视觉方向把关。原因有三：一是开发顺序上后端接口/状态机/数据契约先固定，前端从 UI 还原开始、按已定契约一步一步调通——后端是前端的前置依赖，先把住后端才能保证前端调试有稳定靶子；二是前端迭代快、体验导向、回滚调整成本低，适合把人的精力省下来投入后端核心；三是开发者的主战场在后端，前端交给 AI 快速落地是符合个人技术栈优势的理性选择。这并不意味着前端不重要，而是说在 MVP 时间和认知资源约束下，把“不可外包的判断”放在后端，把“可快速修正的表达”放在前端。

> **汇报金句：** 用「人主导 + AI 辅助」的方式，开发出一个「AI 自主 + 人工门」的产品——这本身即证明 AI 不替代人，而是在人划定的边界内，把人从重复劳动中解放出来。

### 1.1 使用场景

**痛点**

1. 白天时间紧迫，开发人员为赶进度只关注核心业务逻辑是否跑通，留下一堆编译警告、未处理的边界异常，直接下班回家。
2. 每个团队的系统里都堆积着大量"不影响运行、但看着难受"的技术债：废弃的 API 调用、不规范的日志格式、缺少判空的潜在隐患。开发人员没有时间（和意愿）去修。

**用法**

1. 夜间"无感清洗"（Nightly Clean-up）——程序员下班，AI 上班。
2. 早上上班，开发人员第一件事就是看 PR，决定是否合并。

### 1.2 为什么做「代码质量优化」流水线，而不是完整流水线

**愿景是完整流水线**：需求拆解 → UI 生成/还原 → 代码生成 → 自动测试（循环迭代、人工评审、代码落地、测试脚本真实运行）→ 代码质量优化。但在当前 AI 技术节点，无论 OpenHands、Devin、SWE-agent 还是其他 Agent，都无法真正全自动、闭环、妥善地完成需求开发。收敛到「代码质量优化」这一环，是因为它是整条链里**唯一满足「确定性」要求的环节**，其余环节在现阶段都卡在：

- **需求侧发散**——需求本身是发散的，没有「绝对确定」的输入。
- **确定性要求**——流水线需要输入和判定标准都绝对确定；AI 逻辑推理不稳定，但编译器和测试框架绝对诚实、绝对稳定。
- **质量基础设施不足**——单元测试覆盖率低，缺少诚实的自动化裁判。
- **Agent 能力暂不够**——上下文膨胀与迷失、「烟囱式」乱写代码。
- **环境复杂**——沙盒难以适用真实开发环境。
- **生产敬畏**——人不经手的代码难以真正发现隐患（与「人工门」一脉相承）。
- **资源约束**——行内模型资源有限，复杂任务更耗 token，且需要及时人机协同的只能在工时做，模型资源更撑不住。

所以不是「不想做完整流水线」，而是「代码质量优化是当前唯一能把 AI 自主跑起来的确定性环节」——sonar 既出题又阅卷，恰好补上 AI 推理不稳的短板。这也正是「AI 自主 + 人工门」能成立的前提。

### 1.3 可以扫描出哪些问题

扫描按三维质量维度给每个问题打标（一个 issue 可同时多标签）——**Security（安全漏洞）/ Reliability（逻辑缺陷）/ Maintainability（代码异味）**。

Sonar 规则引擎能稳定识别的，是**规则化问题**，落到真实修复场景典型三类：

1. **逻辑缺陷**：资源泄漏、空指针风险、除零。
2. **安全漏洞**：硬编码凭证、使用过时的加密套件 MD5、未使用 PreparedStatement 拼接 SQL。
3. **代码异味**：废弃 API、方法复杂度过高、重复代码块。

但 Sonar 只能抓"规则化"的模式，语义层面的问题——竞态条件、死锁风险、N+1 查询、上帝类、语义重复、敏感数据泄露——它抓不到，这正是需要 **AI Discovery（Claude）** 作第二通道补齐的原因（见 4.2）。

### 1.4 演示叙事：About 页的「机制 → 角色 → 信任 → 行动」

`/dev/about` 平台介绍页是元叙事的对外表达面，五个板块按一条递进线组织：

1. **Hero**：slogan「晚上 AI 修代码，早上你顺手审 PR」——口语版「程序员下班，AI 上班」，重活归 AI、轻活归人。
2. **机制**：流水线怎么跑（七阶段主链）。
3. **角色**：三种身份各司其职（项目管理员=所有者 / 流水线机器人=机器工人 / 平台管理员=熔断权）。
4. **信任**：AI 拿得到仓库的钥匙吗（凭证隔离 / 最小权限 / 凭证脱敏）。
5. **行动**：三步接入你的仓库 + CTA。

顺序是设计过的，不是随便排的：安全卡依赖身份卡的概念——读者先认识「机器人」这个角色，才看得懂"机器人凭证只有接入仓库的写权限"在说什么，故**角色先于信任**；身份卡刚告诉读者"你是项目的所有者"，紧接着就是"三步接入你的仓库"，故**角色紧挨行动**；最强的定心丸（三道防线）压在 CTA 正前方，读者点"去接入项目"前最后读到的是安全，故**信任收在行动之前**。

文案纪律两条：页面语域用大白话（「就算…也」「顺手」），报告语域用术语（爆炸半径、最小权限）——同一事实两种表达，不算不一致；所有安全表述必须与代码实现一一对应，未实现的机制（如网络出站白名单）只能进第十三节"后置"，绝不写进防线（8/22 纠错的教训）。


---

## 二、安全模型：「能改仓库代码的凭证，绝不进 AI 容器」

这是整个系统最核心的安全设计。三条防线逐层收紧。

**第一层：凭证隔离。** Git 凭证（bot token + admin token）只留在后端，每次使用时由 `ProcessBuilder` 以 `-c http.extraHeader` 注入临时 alpine/git 容器——当次生效、用完即毁。URL 中不拼 token（不进 volume 里的 `.git/config`），容器销毁后物理上不可恢复。AI 容器内零 git 凭证 → prompt injection 偷不到仓库写权限。

**第二层：最小权限。** 两个 token 各司其职：admin token 只在项目接入/改配置时使用（校验仓库与分支、加 collaborator），流水线运行全程零 admin 凭证；bot token 是 Gitea 账号级凭证，写权限随 collaborator 身份按仓库收放——接入项目时授权、删除项目时回收，只覆盖已接入的仓库，碰不到其他仓库。

**第三层：凭证脱敏。** 命令日志与失败异常中的凭证值统一打码（详见下文实现细节），凭证不进日志、不落库。

**实现细节：** 所有 git 操作由后端通过 `docker run --rm` 起临时 alpine/git 容器执行。clone URL 用无凭证形式（`<host>/<owner>/<repo>.git`），token 通过 `git -c http.extraHeader=Authorization: token <value>` 当次生效——**不拼进 remote URL**。如果拼进去，git 会把 token 原样写入 volume 里的 `.git/config`，持久化泄露。容器用完即毁（`--rm`），token 物理上不可恢复。除首次 clone 外，所有 git 命令统一加 `-c safe.directory=/workspace/repo`——volume 属主是 bot 用户，但 git 容器以 root 运行，git 安全检查会报 "dubious ownership"。配套地，`ProcessUtil` 对命令日志与异常 message 统一脱敏——`MODEL_API_KEY`/`SONARQUBE_TOKEN`/`sonar.token`/`Authorization: token` 的值一律 `***`，凭证不进日志、也不随失败异常落 `action_attempt.result`。

**不得不的让步：** 进沙盒容器的只有 AI/扫描凭证——DeepSeek API key（AI 要在容器内跑，调 API 就要 key）和 SonarQube token（容器内扫描与 MCP 自查要用）。两者都是可独立轮换的非代码权限，非 git 凭证。

---

## 三、后端实现演进：从状态机骨架到真实 Action

汇报时如果只讲最终架构，会掩盖这个项目最值得一说的工程过程。**后端不是一次性写成的，而是按“先编排、再重试、再填真实动作”的节奏逐步长大的**，每一步都有明确目的。

### 3.1 阶段一：用 MockAction 跑通基础状态机

先搭一条最简主链：`PENDING → STARTING → STARTED → SYNCING → SYNCED → SCANNING → SCANNED → FIXING → FIXED → VERIFYING → VERIFIED → SUBMITTING → SUBMITTED → CLEANING → CLEANED`。每个 Action 先继承 `MockAction`，随机返回成功或失败。目标只有一个：**验证状态机编排本身正确**——事件怎么发、Trigger 怎么接、状态怎么落表、DB 与内存是否一致。此时不 care 真实修复能力，只 care 流程能走通。

### 3.2 阶段二：加入重试与回退环

基础链跑通后，立刻暴露一个问题：Fix 或 Verify 失败怎么办？直接 FAILED 太浪费，应该回滚到修复前现场再试。于是引入 `RESTORING → RESTORED` 回退环和 `RestoredTrigger`：
- Fix 失败 → `RESTORING` 回滚代码 → `RESTORED` → 重新 `FIXING`
- Verify 失败 → 同样回滚 → 重新 `FIXING`
- 每轮重试计数，耗尽后才会进入 `FAILED`

`RestoredTrigger` 是整个状态机唯一的分支点，通过时间戳区分失败来源、检查重试上限。这条环让状态机从“单程票”变成“可回滚的流水线”。

### 3.3 阶段三：逐个替换 MockAction

状态机和重试环稳定后，才开始把 MockAction 替换成真实实现。替换顺序由依赖关系决定：**前面的 Action 是后面 Action 的前置条件**。

| 顺序 | Action | 真实化内容 |
|---|---|---|
| 1 | `StartAction` | 起 Docker sandbox 容器，准备 `/workspace/repo` 和 bot 用户 |
| 2 | `SyncAction` | 临时 alpine/git 容器 clone 代码，用 bot token 注入 |
| 3 | `ScanAction` | 双通道扫描：SonarQube 规则引擎 + Claude AI Discovery |
| 4 | `FixAction` | 统一 prompt 调用 Claude 修复，MCP `analyze_code_snippet` 自查 |
| 5 | `VerifyAction` | SonarQube 重扫客观判定 + Claude 语义评审 |
| 6 | `SubmitAction` | 推修复分支、调 Gitea API 开 PR、写统一格式诊断报告 |
| 7 | `CleanAction` | 清理容器（不删 volume——项目级代码缓存，下轮增量复用） |

每替换一个就跑端到端验证，确保状态机仍然能正确驱动新动作。这种“骨架先行、血肉后填”的方式，让真实 Action 的开发不会被状态机 bug 阻塞。

### 3.4 阶段四：PR 状态反馈（提交后的独立生命周期）

Run 到 `CLEANED` 后，PR 还在 Gitea 等人评审。于是新增 `Run.prStatus`（OPEN/MERGED/CLOSED）和独立定时任务 `sync-pr-status-cron`，轮询 Gitea API：
- `merged=true` → 本 run 全部 `VERIFIED` issue 置 `ACCEPTED`
- `closed & !merged` → 全部 `REJECTED`
- `open` → 继续轮询

这一步不污染状态机（`CLEANED` 仍是终态），把“人工裁决”作为提交后的独立观测生命周期处理。

### 为什么这样演进

1. **先验证编排正确，再验证动作真实**：如果状态机本身有 bug，真实 Action 失败后无法判断是动作错了还是编排错了；Mock 阶段把编排风险先清掉。
2. **重试环必须在真实 Action 之前定稿**：真实修复动作一定会失败，没有回退环就要么一次废要么逻辑到处补。
3. **每个 Action 有稳定前置**：Scan 依赖 Sync 的代码、Fix 依赖 Scan 的 issue、Verify 依赖 Fix 的 commit，顺序替换避免多变量同时爆炸。
4. **人把住 Action 边界和状态机，AI 填动作内部**：状态机流转、Action 出入口、错误处理由人设计；prompt 模板、DTO 解析、日志打印等内部实现大量交给 AI。

---

## 四、双通道发现 + 两道防线验证

### 4.1 扫描工具选型：SonarQube 而非 PMD/SpotBugs

扫描通道的选型，核心不是"谁扫得更准"，而是"谁能让后续的 Fix/Verify 环节有料可依"。整条链路需要扫描工具提供三样东西：**规则描述**（喂给 FixAction 的修复提示）、**server 端状态**（喂给 VerifyAction 的重扫比对）、**MCP**（喂给 FixAction 的修复自查）。

- **PMD、SpotBugs ❌**
  ```bash
  mvn compile com.github.spotbugs:spotbugs-maven-plugin:4.10.3.0:check
  mvn org.apache.maven.plugins:maven-pmd-plugin:3.28.0:check
  ```
  - 只有告警、缺修复提示——规则名 + 触发点，没有"为什么是问题、怎么修"的指导，FixAction 的统一 prompt 拿不到可用的诊断上下文。
  - 纯离线工具、无 server 端状态——扫完即散，无法翻页拉取 issue、无法"重扫后数量对比"判定回归（VerifyAction 第一道防线依赖这个）。
  - 无 MCP 通道——FixAction 修复后要靠 sonarqube MCP 的 `analyze_code_snippet` 自查是否引入新 issue，这条链 PMD/SpotBugs 接不上。

- **SonarScanner + SonarServer ☑️**
  - 规则描述完整：`/api/rules/show` 拿到规则文档（问题是什么 + 怎么修），直接喂给 FixAction 统一 prompt。
  - 三维 severity 独立：Reliability / Security / Maintainability 三质量维度，天然映射 `dev_issue` 的三维 severity 数据模型（见第五节）。
  - 既出题又阅卷：verify 用 sonar-scanner 重扫、按 issue 数量对比判定是否消除（决策 2）。
  - 有 MCP：`analyze_code_snippet` 供 FixAction 自查修改文件。

**结论：** SonarQube 被选中，不是因为它比 PMD/SpotBugs 扫得更多，而是因为只有它把"扫描 → 修复 → 验证"串成了一个可喂给 AI 的闭环——规则描述喂 Fix、重扫喂 Verify、MCP 喂自查。PMD/SpotBugs 三样都缺，即便告警更精确也用不上。

### 4.2 ScanAction：双通道扫描

```
ScanAction:
  ├─ Maven 构建: find pom.xml → mvn -q compile（阿里云镜像;无 pom 才跳过,有 pom 编译失败即抛异常响亮失败——决策 50,否则 sonar 拿旧字节码分析新源码,门禁不可信）
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

AI Discovery 产出的问题自带诊断——`description`（为什么是问题）、`suggestion`（怎么修）、`type`（22 种 `AiMetadata.Type` 分类，从 GOD_CLASS 到 RACE_CONDITION）。信息自包含，不需要外部知识库。

### 4.3 FixAction：统一 prompt + MCP 自查

ScanAction 阶段已把全部诊断信息存入 issue 字段。FixAction 对所有 issue 使用同一 prompt 模板——Claude 从 `title`/`codeSnippet`/`metadata` 获取上下文即修，修复完成后调用 sonarqube MCP 的 `analyze_code_snippet` 自查修改文件，确保不引入新 issue。

**commit_message 只让 AI 做它该做的。** Claude 修复后吐 `{subject, body}` 两字段 JSON——subject 一句话总结、body 修复思路，后端拼成 `subject\n\nbody` 交 git commit。曾考虑让 AI 同时输出 `verification`（自述怎么验证）和 `risk`（修复风险），都砍掉：验证是 VerifyAction 的职责，AI 自述验证不可靠又越权（活没干完就 preemptive 描述怎么验收）；risk 同理——同一模型刚改完代码就评自己的风险，没有信息增量。模板内嵌代码（text block），不引用外部模板文件，少一个运行时依赖。

**失败要能诊断卡在哪。** `FixResult` 不记单一 `fixedIssueNum`，而是按 source 双计数 `fixedSonarIssueNum`/`fixedAiIssueNum`——失败时一眼看出修了几个、卡在 SonarQube 通道还是 AI 通道。任一 issue 失败即整轮 `FIX_FAILED`，不做逐 commit 精准保留（理由见第七节）。

### 4.4 VerifyAction：两道防线

```
VerifyAction:
  ① SonarQube 重扫 → 客观判定（修的 issue 全 CLOSED + 无回归）
  ② Claude review  → 语义验证（思路对不对 + 实现到不到位）
  两道都过 → VERIFIED
```

第一道是硬数字（数量对比检测回归：重扫 issue 数 > 原扫描数 - 已修复数 → 存在回归；失败时按 key 求差集记录明细——未修复 = 当前扫描 ∩ 本轮 FIXED，新引入 = 当前扫描 − 扫描基线，判定口径不变、明细只为排障），第二道是语义判定。**SonarQube 做客观门槛，Claude 做语义评审**——不是"自己出题自己判"，而是各司其职。

**第二道不喂 diff，让 Claude 自己读代码。** 容器内 claude code CLI（working dir `/workspace/repo`）本就能 `cat` 文件，喂 diff 是冗余。改用 FixAction 留下的 `commitMessage`（`{subject,body}`）承载"修复思路"，第二道两维度判定：① commitMessage 描述的思路逻辑上能否解决原问题 ② 当前代码是否按该思路正确修改且真正消除问题。逐条 review + fail-fast 短路，任一判 `{"verified":false}` 整轮回退。

**输出最小 JSON，砍 reason。** Claude 只回 `{"verified":true/false}`——失败即整轮回退（issue 回 SELECTED），无人看 reason；调试看 `log.info(rawOutput)`。砍 reason 省 token、降复杂度，失败 message 用固定文案。

### 4.5 SonarQube 对用户透明

issue 统一走同一张表、同一修复/验证路径。PR body 统一骨架：标题 + 三维 severity + 代码片段链接(跳转源码对应行) + 修改记录链接(跳转 commit)，AI 分支额外 type/description；每条 issue 标注来源(【来源：SONAR】/【来源：AI】)——来源透明，处理路径一致。用户不需要接触 SonarQube 原始 API 数据，拿到的就是结构化的诊断报告。

---

## 五、数据模型：Source 区分器 + 三维 Severity

`dev_issue` 承载所有问题记录，无论来自 SonarQube 还是 AI Discovery：

```sql
source                   VARCHAR(16)   -- SONAR / AI
metadata                 JSON           -- SonarMetadata 或 AiMetadata（@JsonTypeInfo 多态）
title                    VARCHAR(1024)
reliability_severity     VARCHAR(16)    -- BUG(逻辑缺陷)
security_severity        VARCHAR(16)    -- VULNERABILITY(安全漏洞)
maintainability_severity VARCHAR(16)    -- CODE_SMELL(代码异味)
```

**source 区分器**：`@JsonTypeInfo(property = "@source")`，和 `ActionResult` 同模式——JSON 列内嵌多态子类（`SonarMetadata` 含 issueKey/ruleKey，`AiMetadata` 含 type 分类）。

**三维 severity 独立**：SonarQube 的三质量维度（Reliability/Security/Maintainability）各自独立评分。一个 issue 可以同时是 BLOCKER 级别的安全漏洞和 MEDIUM 级别的代码异味——这不是反规范化，是正确建模。

**`AiMetadata.Type` 分类体系**：22 种 AI 可识别的代码问题类型，分六大类（架构/逻辑/安全/可维护/性能/并发）+ OTHER 兜底。每个类型带中文描述，既是 AI prompt 里的分类指引，也是 PR 报告里的用户可读标签。

**不做的事**：不加唯一约束——ScanAction 插入前删除本 run 旧数据，业务逻辑保证不重复。不设 `rule_key` 顶层列——对 AI Discovery 没用，对 SonarQube 存 metadata 里即可。

---

## 六、18 态状态机：一条主链、一个回退环、一个终态

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

**RestoredTrigger** 是唯一的分支点：根据时间戳区分"fix 失败"还是"verify 失败"，各自检查重试上限，决定续修或放弃。不到 50 行代码，没有嵌套状态机，没有编译期不可见的隐式行为。

**曾考虑 issue 级独立状态机但否决。** 每个 issue 独立跑 `FIXING → VERIFYING → RESTORING → FIXING` 看似优雅，但引入两个根本问题：

1. **嵌套状态机。** run 级状态机下再挂 N 个 issue 级状态机——编排复杂度翻倍，Trigger 要同时监听两层事件。
2. **`maxSonarIssuesPerRun=1` + `maxAiIssuesPerRun=1` 等效替代。** 状态机不改，把每批修复数降到 1，行为上完全等价于 issue 级隔离，但架构上不引入额外状态层。

**结论：** issue 级状态机不是"后面对齐再做"，而是评估后明确拒绝的方向。同一套简单状态机靠调参覆盖全部行为范围。

**issue 状态机也因此精简到三态。** 既然整轮回退、不做 issue 级状态机，issue 的 `Status` 就只剩 `SELECTED → FIXED → VERIFIED` 的流转——任一阶段失败（fix 或 verify）都整轮回 `SELECTED`，不标 `FAILED`。fail-fast 下 `FAILED` 永无写入点（失败抛异常、不落状态），是死状态，直接从枚举删除。`ACCEPTED`/`REJECTED` 预留给 SUBMIT 后的人工终态。少一个永远不会出现的状态，枚举即文档——读代码的人不会困惑"这个状态什么时候出现"。

**PR 结果也走字段，不进状态机。** run 提交 PR 后到 `CLEANED` 终态，但 PR 还在 Gitea 等人评审——合并或拒绝是几小时甚至几天后的事。这个"迟到的观测"不塞进状态机（`CLEANED` 是终态，后面再加 `MERGED`/`REJECTED` 会污染终态语义），而是用 `Run.prStatus` 字段（OPEN/MERGED/CLOSED）记录，定时任务轮询 Gitea API 回写；issue 的 `ACCEPTED`/`REJECTED` 终态也在此落定。观测归观测、流程归流程——状态机只管"下一步干什么"，不管"外部世界后来怎么了"。（8/13 已实现：`syncPrStatus(runId)` 抽成 service 方法，`sync-pr-status-cron` 定时遍历回写 `ACCEPTED`/`REJECTED`——仅 `VERIFIED` 的 issue 参与回写，不动其他状态；8/14 `getRun` 纯读化，移除详情实时同步副作用。）

---

## 七、不浪费 AI 算力的正确方式：三层防线而非扭曲状态机

### 7.1 一度想做的方案（评估后明确拒绝）

曾考虑 **逐 commit 精准保留**：Verify 失败时 `git reset --hard <最早失败 commit^>` 保留修好的 commit → 只重修失败的 issue → 重试耗尽后强制 SUBMIT 已有成果。这个方向会引入链式复杂度：

- VerifyAction 膨胀：从纯判定变为定位最早失败 + 执行 reset + chown
- RestoreAction 分裂：verify 失败只清理不切分支，cancel 完整还原——两条路径
- RestoredTrigger 四分支：需判断失败来源 × 重试次数
- FixAction 乱序：失败 issue 排到队尾以避免永远卡在同一个 issue

**但复杂度还是次要的——根本原因是回归。** 精准保留会跨轮拼接修复集：已保留的成功修复（上一轮）和这轮重修失败 issue 的修复，在不同基线、不同上下文产生，可能互相踩踏——重修 issue B 的改动覆盖或破坏上一轮 issue A 已修好的代码（同文件/同模块的 issue 尤甚），凭空引入新回归。全量回退则保证每个 PR 的修复集是**原子的**：每一轮从干净 `origin` 重新修所有 issue，所有修复在同一轮、同一基线、同一上下文产生，彼此协调一致，不存在"跨轮拼凑"的缝隙。宁可重修已修好的 issue 浪费算力，也不拼凑跨轮修复冒回归风险——夜间 AI 时间不稀缺，一个带回归的 PR 合进主干，代价远大于重修。

**结论：** 逐 commit 保留不是"后面对齐再做"，而是评估后明确拒绝。**核心理由是质量**：全量回退保证修复集原子性、回归比例更低（跨轮拼凑的修复会互相踩踏）；其次是复杂度（状态机从编排引擎退化为业务决策引擎）和效率认知（节省的是夜间 AI 时间，不稀缺）；防线由 `RestoredTrigger` 重试上限和 `maxIssuesPerRun` 窗口承担。

### 7.2 替代方案：三层防线，不改状态机

| 层 | 机制 | 作用 |
|---|---|---|
| 1 | AI 修复能力验证 | 8/2 预研三步全过，修复质量可靠 |
| 2 | `maxIssuesPerRun` 窗口 | 能力下降时降低每批修复数——同一状态机，调参即可 |
| 3 | `RestoredTrigger` 重试上限 | fix/verify 各有重试上限,耗尽整轮放弃转 FAILED——重试决策收敛于唯一分支点,不散落各 Action |

**核心洞察：** `maxSonarIssuesPerRun` + `maxAiIssuesPerRun` 是复杂度调杆。设为 10+5 是典型模式，都设为 1 等效 issue 级隔离——状态机零改动。调参不用动架构。

### 7.3 分支方案悖论

每个 issue 独立验证需要 `mvn compile + sonar-scanner`，整个 Scan 也是 `mvn compile + sonar-scanner`。一个发现 N 个问题，一个验证 1 个问题——两者同代价。发现比验证还便宜，这是反直觉的架构异味。整轮回退恰好避开了这个悖论。

---

## 八、命名确定性：不把 Docker 状态存入数据库

容器名 `morningstar_dev_sandbox_<runId>` 和 volume 名 `morningstar_dev_repo_<projectId>` 均由 ID 确定性推导。数据库中不存 `container_id`——DB 是冗余副本，Docker daemon 才是真实数据源，两者会漂移。能推导就不存。

**与之配套的失败语义：** Action 失败统一 catch `ProcessExecutionException` 返回 FAILED 结果（带语义 message）。即便不主动 catch，`AbstractAction` 全局兜底（dev-plan 决策 37，`execute()` 包 `catch(Exception)`）也会把异常转 FAILED——但那时 message 只剩 `e.toString()`、可读性差，故各 Action 仍主动 catch 已知异常带语义。

---

## 九、Gitea 双视角地址

同一 Gitea 实例，不同消费者看不同的地址：

| 配置 | 消费者 | dev 值 | 为什么不同 |
|---|---|---|---|
| `backend-origin` | 后端 API + PR 链接 + 浏览器 | `http://127.0.0.1:7001` | Mac 宿主机不解析 `host.docker.internal` |
| `container-origin` | 临时 git 容器 clone/fetch/push | `http://host.docker.internal:7001` | 容器内无法访问 `127.0.0.1` |

每环境两值显式配置，不隐式回退。生产两者同为公网域名——冗余但不隐藏差异。

---

## 十、故障隔离：CleanAction 的破环逻辑

`FailedTrigger` 中有一个反直觉的判断：FAILED 状态如果来自 CLEANING 阶段，不再发 CLEAN 事件。**否则 CLEAN 失败 → FAILED → 自动再发 CLEAN → 再失败 → ... 无限循环刷 `action_attempt` 表。**

同理，`CleanAction` 对 "No such container" 返回成功——目标既是"容器不存在"，已不存在 = 目标已达成。

---

## 十一、平台管理员：熔断权与所有权分离

平台跑起来后会出现项目管理员处理不了的运维场景：run 卡死占着并发槽（全局仅 4 个,8/16 调）、项目配置错误每晚反复失败刷表。这要求存在一个"超级用户"——但超级用户该有多大权力，是一道边界设计题。

**设计定稿：管理员只有熔断权与调度权，没有所有权。** 角色 `dev_admin` 能做的事恰好两件：取消**任何**正在运行的 run、切换**任何**项目的调度启停（`toggleSchedule` 双向切换 `enabled`,8/16 从单向"仅停用"调整为双向——管理员工具箱里启停本是一体,恢复不再绕回 owner)。不能做的事同样明确：无配置编辑权、不级联取消进行中 run（停用只挡未来调度，不打断现场）。

**实现上零新体系。** 直接复用平台既有权限框架：`application-perm.yml` 声明角色与权限点（`dev:run:cancel`/`dev:project:schedule`)，启动时 insert-if-absent 同步，登录时装入 authorities，接口上一个 `@PreAuthorize` 注解收尾。管理员接口独立 `/dev/admin/**` 命名空间，不改造 owner 接口——owner 的取消/停用逻辑一行没动，两套入口各管各的校验。

**三层身份分离。** owner（项目管理员，平台自跑项目归专门服务账号 `morningstar-nightly`)/ bot(Gitea 侧提交 PR 的 HaibaraAi369)/ 平台管理员（熔断者）各司其职，任一身份被攻破或滥用，爆炸半径都被限制在自己的职责内——与第二节"最小权限"一脉相承。

**弃了什么：** 独立的权限体系（重复造轮子）、`disabled_by` 字段（MVP 不需要区分"谁停的")、级联取消（越权且危险）、审计表（降级为 `log.info` 留痕，表结构在 admin-operations design 留档，出现争议再升级）。管理员角色也不被禁止创建项目——那是无用守卫，真实约束来自接口语义而非身份封锁。

---

## 十二、平台运维 KPI：五格一条叙事链

平台运维页的 KPI 行不是五个孤立计数，而是一条递进的叙事链，五格刚好构成完整闭环、一环不缺：

**规模 → 实时 → 产出 → 质量 → 价值**

| 格 | 维度 | 讲的是什么 |
|---|---|---|
| 接入仓库 | 规模 | 平台服务的项目面有多大 |
| 并发任务 0/4 | 实时 | 此刻并发槽占用，运维最关心的"现在在跑什么" |
| 累计交付修复 | 产出 | AI 实际修掉并通过验证的 issue 数 |
| PR 合并率 | 质量 | **人工裁决**他评——人审了认不认 |
| 累计节约人天 | 价值 | Σ ACCEPTED 估算工时 ÷ 480，压轴 |

刻意**不加"修复成功率"**：分母受扫描噪音（大量 LOW/INFO）与单轮截断影响天然失真，比率低只说明"这轮只挑了重要的修"；且该比率随配置漂移——调整 sonar 规则或 in-scope severity 级别都会大幅改变分子分母，既不稳定、也无跨期可比性；质量叙事由 PR 合并率承载更硬——那是人工裁决而非机器自评。五格定稿不扩，第六格会挤版式并稀释压轴格的视觉权重。

---

## 十三、演进路径：MVP 刻意放过的

MVP 没做的功能分两类：**后置**（时机未到，条件成熟再做，未来会回来）与**永久不做**（有替代方案或确定无价值）。

### 后置（未来规划，条件成熟再做）

| 事项 | 触发条件 |
|---|---|
| 高频缺陷分析反哺研发培训 | 长期积累大量 issue 数据后做聚合分析——MVP 5 仓库量级"Top"仅三五条，无统计意义，属数据规模未到时提前建设 |
| 优先级排序 | 夜间窗口资源充足先到先修；生产多仓库 / 资源紧张时再上 |
| GitLab 适配 | 演示用 Gitea 已就绪；生产内网部署时替换再实现 |
| 管理员操作审计表 | `dev_admin_operation` 表结构已在 admin-operations design 决策 6 留档；出现争议时从 `log.info` 升级 |
| 容器网络出站白名单 | 沙盒容器当前走默认 bridge 未限出站；出现真实外发风险诉求或合规要求时，用 internal network + 宿主机 iptables 只放行 Gitea/SonarQube/DeepSeek |

### 永久不做（有替代方案 / 确定无价值）

| 事项 | 理由 |
|---|---|
| SSE/WS 实时推送 | 3s 轮询演示与实际使用均够用，场景是夜间跑白天看，无高频实时观看需求；SSE 属"技术更优雅但用户无感"，不值得为此加连接管理复杂度 |
| 管理员 enable 端点 | 已失效——8/16 调整为双向 `toggleSchedule`，见第十一节 |

---

## 十四、项目价值

- **自动化闭环，解放人力**：将「扫描 → 修复 → 验证」的繁琐流程转化为后台无感进程，大幅节省排查空指针、资源泄漏等琐碎问题的时间。
- **专注业务，消灭技术债**：由 AI 在后台自主修复低级技术债，使开发人员全身心聚焦核心业务逻辑的研发。
- **极低成本，极致利用**：充分利用行内既有的闲时算力，几乎不新增额外硬件成本。
- **渐进式演进，远景可期**：架构上具备良好的扩展性，未来条件成熟时可无缝接入完整的研发流水线。
- **严控风险，守住合规底线**：恪守合规要求，绝不赋予 AI 生产环境发布或主干代码合并的权限，确保代码安全可控。

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
| SonarQube 对用户透明 | gitea-pr-submit design 决策 2 | PR body 诊断报告，统一骨架、标注来源 |
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
| About 页演示叙事线 | 本报告 1.4 | 机制 → 角色 → 信任 → 行动；页面大白话 / 报告术语双语域；安全表述必须与代码实现一一对应 |
| Stats 用 Integer 不用 Long | dev-plan 决策 45 | 全局 `Long→ToStringSerializer` 会把 Long 序列化成 string，与前端 `number` 冲突 |
| deliveredIssueCount 口径 | dev-plan 决策 45 | 只算 SUCCEEDED run 的 VERIFIED/ACCEPTED/REJECTED；REJECTED 计入——修复数衡量 AI 能力，接受度归合并率 KPI |
| Stats 只出原子计数 | dev-plan 决策 45 | 合并率/占槽比前端算；`maxConcurrency` 是面板唯一需要的配置项（占槽分母），stuckThreshold/cron 不进 Stats |
| FAILED 一律算活跃 | dev-plan 决策 46 | 躺平 FAILED 容器现场未知，必须占槽+挡触发，防同项目双 run 抢 volume |
| Detail bo 展示扩充 | dev-plan 决策 47 | PO 不沾染展示字段；`@SuperBuilder` 继承 + 统一 `toDetail`，前端类型同构继承；RunDetail 含 actionAttemptBriefs 阶段流水（可视化数据源），CopyUtil 健壮化支持跨类同名拷贝 |
| Run 触发方式入 PO | dev-plan 决策 48 | `triggerType`(MANUAL/SCHEDULED）是真实属性不进 Detail；写入点收敛 `createRun`,调用方用 enum 声明 |
| 前端三页 IA 与可视化定稿 | dev-plan 决策 49 | 我的项目/平台运维/平台介绍；历史任务单表不拆 PR；回退环弧线表达 RESTORING 回路；失败徽章/耗时由 actionAttemptBriefs 前端聚合 |
| Verify 门禁 key 明细与防跨文件回归 | dev-plan 决策 50 | key 差集明细只为排障，判定口径仍是数量对比；mavenBuild 不吞编译失败（否则旧字节码分析新源码，门禁不可信）；重试反馈提示词方案放弃（受众错位 + 回滚后位置失效 + 自检已够） |
| 节约人天量化口径 | dev-plan 决策 51 | `savedPersonDays` = Σ ACCEPTED issue 估算工时 ÷ 480,SQL 一步换算(`ROUND(COALESCE(SUM,0)/480.0,1)`);写死 'ACCEPTED' 不设参数——"节约人天"概念只绑已采纳,参数是无意义自由度 |
| RunDetail 漏斗四值 | dev-plan 决策 51 | 扫描发现(最新 SUCCEEDED SCAN attempt 的 result 反序列化)/本轮入选/已修复/验证通过;已修复与验证通过用累计口径(含其后状态),保漏斗单调不减 |
| Stats 调度时段两字段 | dev-plan 决策 52 | 后端解析 cron 下发 `scheduledStartTime/EndTime`,调度时段唯一展示点是顶栏胶囊——调 cron 不产生文案漂移;破"cron 不进 Stats"例的是用户可见语义而非运维调参 |
| list 接口分页 + statuses 过滤 | dev-plan 决策 53 | run/project 列表必填 pageNum/pageSize 返回 PageResult;statuses 是分页正确性配套(终态视图必须 SQL 层过滤,否则进行中 run 混入首页致缺行/空页);adminId 过滤因无调用方删除;排序键按方向取各自语义:ASC createTime 主序贴合分发顺序,DESC updateTime 主序按完成时间排——否则"先创建后完成"的 run 沉底 |
