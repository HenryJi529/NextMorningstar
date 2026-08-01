## 上下文

状态机内核(`State`/`Event`/`StateMachineService`)、自动编排(`Trigger`)、执行模板(`AbstractAction`)、重试/取消与三张表已实现,8 个 Action 仍为 Mock。本变更是把流水线"接出来":HTTP 入口、issue 级数据载体、夜间调度,使其可被驱动与观测,并在 mock 下端到端验证编排正确性。

## 目标 / 非目标

**目标:**
- 流水线可通过 HTTP 手动触发、可查询、可取消。
- 流水线可由夜间定时任务自动驱动。
- 以 `dev_issue` 记录单漏洞修复过程,支撑后续 Fix/Verify/Submit。
- mock 模式下端到端跑通,验证状态机与编排。

**非目标:**
- 不实现任何真实 Action(容器/扫描/修复/PR 等)。
- 不引入优先级排序。
- 不实现前端。

## 决策

### 决策 1:`dev_issue` 数据模型（14 字段）
新增 `dev_issue` 承载"一漏洞一记录、一漏洞一 commit"。SonarQube 来源字段统一加 `sonar_` 前缀：`sonar_project_key`、`sonar_issue_key`、`sonar_rule_key`、`sonar_severity`、`sonar_type`、`sonar_message`、`sonar_effort`。`sonar_project_key` 归于 issue 减少跨 run 汇总的联表查询。`status` 枚举 SELECTED→FIXED/FAILED→ACCEPTED/REJECTED，代码手动设置。`commit_message` 替代原 `ai_report`。`attempt_no` 移除（不需记录重试次数）。唯一约束 `uk_run_issue (run_id, sonar_issue_key)`。

### 决策 2:并发度 = 2
Fix 阶段是模型对话 I/O,CPU 空闲,多 run 可并行;Scan/Verify 才 CPU 密集且短。并发度 2 在 2 核机器上一夜 ~10h 窗口可跑 ~16-20 仓库。

### 决策 3:定时创建与分发分离
定时任务拆为 4 个独立 cron，各司其职：
- `nightlyCreateRuns`(21:00):扫描 enabled 项目，跳过已有活跃 run 的项目，其余创建 PENDING run。
- `dispatchPendingRuns`(每 30s):查 PENDING run，按 maxConcurrency(默认 2)槽位捞取并发送 START 事件。
- `cancelTimeoutRuns`(每 5min):查超过 60min 无响应的 run(24h 时间窗口)，触发取消。
- `cancelOvernightRuns`(6:00):取消所有非终态活跃 run(PENDING 直接标 CANCELED，其余走 requestCancel)。

**动机**:创建与启动分离，创建轻量(只 insert)，分发控制并发。超时与清晨清理确保资源不泄漏。

### 决策 4:项目归属(`admin_id`)
`dev_project` 加 `admin_id`(FK→`sys_user.id`)。所有 Project/Run 操作需传 `adminId` 做权限校验。`CreateProjectRequestVo.adminId` 由 Controller 从 SecurityContext 取当前用户填入（`hidden=true`）。`listByAdminId` 替代 `listAll`。

### 决策 5:不可变字段
`link`(仓库链接)和 `sonarProjectKey` 创建后不可改——前者改了需要重新 clone,后者是 sonar 项目标识不应变更。`UpdateProjectRequestVo` 仅含 name/branchName/description/maxFixesPerRun。

### 决策 6:不冗余时间字段
`dev_run` 不加 `finished_at`(`updateTime` 即可)。`dev_action_attempt` 不加 `start_time`/`end_time`(`createTime`/`updateTime` 即可)。FillDataHandler 自动处理,与业务逻辑解耦。

### 决策 7:VO 扁平包结构
Request VO 直接放在 `pojo.vo`(非 `pojo.vo.req`),与其他模块不同。dev 模块 VO 类型单一,扁平够用。

### 决策 8:createRun vs triggerRun 分离
`RunService.createRun(projectId)` 无权限校验，纯 insert PENDING run，供调度器使用。`RunService.triggerRun(projectId, adminId)` 含 adminId 权限校验 + `createRun` + `sendEvent(START)`，供 Controller 手动触发使用。

### 决策 9:仓库授权与 link 唯一性

collaborator 是**仓库级**(owner/repo)的，不挂在 project 记录上。若同一仓库对应多个 project，任一 project 禁用/删除都会移除共享的 collaborator，连累其他仍 `enabled` 的 project（「合作者混乱」）。故采取「归一化 + 唯一约束」配套：

- **link 归一化**：`createProject` 用 `formatRepoLink` 把任意写法（`https://`/`git@`/带 `.git`/带尾 `/`）归一为 `<origin>/<owner>/<repo>`，使「同一仓库」在 DB 里是同一字符串（link 不可变，仅 create 归一）。
- **link UNIQUE**：`uk_dev_project_link`，粒度为**仓库级（不含 branch）**——因 collaborator 是仓库级；代价是同仓库多分支需另设计（多分支 run 或 collaborator 引用计数），当前不支持。
- **必须配套**：只归一不唯一仍可重复；只唯一不归一则别名（`…/y` vs `…/y.git`）绕过约束。

撞键由 `DuplicateKeyException` 翻译为 `DEV_PROJECT_LINK_DUPLICATE`（与 blog/system 既有惯例一致）。

### 决策 10:Run 的 state 与 status 双字段分离

`dev_run` 同时有 `state`(State 枚举)和 `status`(Run.Status 枚举):

- **`state`**:流水线阶段,状态机驱动,用于所有业务查询和状态判断(如 `notIn(PENDING, CLEANED, FAILED)` 判断活跃 run)。
- **`status`**:整体结果,观测用(RUNNING/SUCCEEDED/FAILED/CANCELING/CANCELED)。Cancel 流程通过 `status=CANCELING` 标记取消意图,6 个 Trigger 检查 `cancelTracker` 后走 CLEAN。

**动机**:流水线阶段(17 态)与用户关心的结果(5 态)粒度不同,分离后查询用 `state`(精确),用户展示用 `status`(简洁)。
PENDING 状态 run 尚未启动，不在任何 Trigger 的取消检查路径上。因此在 `StateMachineService.requestCancel` 中拒绝 PENDING 的取消请求(避免残留 CANCELING 状态标记)，改为 `cancelOvernightRuns` 中直接 `updateById` 设置 `status=CANCELED`。
