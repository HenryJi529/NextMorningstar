# dev-vuln-pipeline 规格

## 目的

提供可手动/定时触发、可查询与取消的代码质量优化流水线运行，并以 issue 级粒度跟踪每个问题的修复过程。

## 需求

### 需求：流水线可被手动触发与跟踪

操作者可通过 HTTP 接口为已配置项目触发一次修复运行，并查询运行状态。

#### 场景：手动触发运行

- **WHEN** 项目 owner 调用 `POST /dev/run?projectId=` 为某 `enabled` 项目触发运行（写接口校验 `adminId` 归属）
- **THEN** 创建一条 `dev_run`（初始 `PENDING`，`triggerType=MANUAL`）
- **AND** 已有非终态 run（`state != CLEANED`）的项目拒绝再触发（`DEV_PROJECT_HAS_ACTIVE_RUN`，单飞守卫）
- **AND** 混合并发槽：`countExecutingRun() < maxConcurrency` 立即发 START 直启，满槽留 PENDING 等 dispatch（≤30s）并打排队日志
- **AND** 可通过 `GET /dev/run/{id}` / `GET /dev/run`（`projectId`/`adminId` 可选过滤、create_time 倒序）查询 run 状态，读接口登录即可（读公开写私有）

#### 场景：取消运行

- **WHEN** 项目 owner 对运行中的 run 发起取消（`DELETE /dev/run/{id}`，校验归属）
- **THEN** 在安全点终止流程，run 进入取消处理（`requestCancel` 忽略 PENDING/SUBMITTED/CLEANING/CLEANED/FAILED）

### 需求：夜间定时驱动

#### 场景：定时创建

- **WHEN** 到达配置的定时时刻（默认 21:00）
- **THEN** 扫描所有 `enabled` 项目
- **AND** 对有活跃 run（`state != CLEANED`）的项目跳过
- **AND** 对其余项目创建 PENDING run（`triggerType=SCHEDULED`）

#### 场景：分发调度

- **WHEN** 到达分发间隔（每 30s）
- **THEN** 查 PENDING run 按创建时间排队，取可用并发槽位数（默认 2）的 run 发送 START 事件

#### 场景：超时取消

- **WHEN** 到达超时检测间隔（每 5min）
- **THEN** 查 24h 内创建、超过 `run-timeout-minutes`（默认 120min）无状态流转的 run（排除 PENDING 排队态与 CLEANED 终态），触发取消

#### 场景：清晨清理

- **WHEN** 到达清晨清理时刻（默认 6:00）
- **THEN** 对在跑的非终态 run 走 `requestCancel` 取消流程
- **AND** PENDING run 直接 `deleteById` 删除（从未启动，不留记录；8/14 改，原"标 CANCELED"），明夜重新触发

### 需求：资源池并发执行

#### 场景：夜间并发跑多仓库

- **WHEN** 夜间触发多个 enabled 项目
- **THEN** 按 `schedule.max-concurrency`（默认 2）并发执行，单 run 失败不影响其他
- **AND** 单 run 占一个容器，池满时其余以 PENDING 排队

### 需求：issue 级修复跟踪

#### 场景：记录单漏洞修复

- **WHEN** 扫描阶段获取到一个 issue
- **THEN** 在 `dev_issue` 记录其 source/metadata/三维 severity/title/effortInMinutes/状态
- **AND** 后续修复、验证、提交各阶段回写该 issue 的状态、commit 与诊断信息

### 需求：启用仓库自动授权

#### 场景：创建项目并授权

- **WHEN** 创建项目（默认 `enabled=true`）
- **THEN** 平台先经 `GiteaUtil.validateRepoAndBranch` 校验仓库与分支存在（404 分别报 `DEV_PROJECT_REPO_NOT_FOUND`/`DEV_PROJECT_BRANCH_NOT_FOUND`，先校验后变更零副作用）
- **AND** 用 admin token 把 bot 加为该仓库 collaborator（`write`）
- **AND** 授权成功后才持久化项目记录
- **AND** AI 后续仅对该仓库拥有写权限

#### 场景：启停项目不动授权

- **WHEN** 项目被启用（重新启用已禁用项目）或停用
- **THEN** 仅持久化 `enabled` 翻转，不增删 bot 协作者——bot 权限随项目生命周期（创建/删除）收放，避免停用后 bot 克隆/调度失败（Gitea 对无权限私有仓库统一返回 `Repository not found`）
- **AND** 管理员 `toggleSchedule` 与 owner 编辑走同一语义，仅切 `enabled`，不管理 collaborator

#### 场景：删除项目并回收授权

- **WHEN** 项目被删除
- **THEN** 移除 bot 对该仓库的访问权限
- **AND** 删除项目额外守卫：存在非终态 run（`state != CLEANED`）即拒绝（`DEV_PROJECT_HAS_ACTIVE_RUN`）

#### 场景：校验失败阻止变更

- **WHEN** 创建或改分支时仓库/分支不存在（Gitea 返回 404）
- **THEN** 报错并阻止本次变更，字段不持久化

#### 场景：同一仓库只能对应一个项目

- **WHEN** 创建项目时，归一化后的仓库链接已存在（无论原始写法是否相同）
- **THEN** 报错拒绝创建
- **AND** 避免多个项目共享同一仓库的 collaborator 而互相踩踏
