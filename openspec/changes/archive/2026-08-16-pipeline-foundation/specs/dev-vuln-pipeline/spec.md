# dev-vuln-pipeline 规格

## 目的

提供可手动/定时触发、可查询与取消的代码质量优化流水线运行,并以 issue 级粒度跟踪每个问题的修复过程。

## 需求

### 需求:流水线可被手动触发与跟踪

操作者可通过 HTTP 接口为已配置项目触发一次修复运行,并查询运行状态与其下 issue 列表。

#### 场景:手动触发运行

- **WHEN** 调用接口为某 `enabled` 项目触发运行
- **THEN** 创建一条 `dev_run`(初始 `PENDING`),状态机自动驱动后续阶段
- **AND** 可通过接口查询该 run 的当前状态与其关联的 `dev_issue` 列表

#### 场景:取消运行

- **WHEN** 对运行中的 run 发起取消
- **THEN** 在安全点终止流程,run 进入取消处理

### 需求:夜间定时驱动

#### 场景:定时创建

- **WHEN** 到达配置的定时时刻(默认 21:00)
- **THEN** 扫描所有 `enabled` 项目
- **AND** 对有活跃 run(非 CLEANED/FAILED)的项目跳过
- **AND** 对其余项目创建 PENDING run

#### 场景:分发调度

- **WHEN** 到达分发间隔(每 30s)
- **THEN** 查 PENDING run 按创建时间排队，取可用并发槽位数(默认 2)的 run 发送 START 事件

#### 场景:超时取消

- **WHEN** 到达超时检测间隔(每 5min)
- **THEN** 查 24h 内创建、超过 60min 无响应的 run，触发取消

#### 场景:清晨清理

- **WHEN** 到达清晨清理时刻(6:00)
- **THEN** 取消所有 24h 内非终态活跃 run
- **AND** PENDING run 直接标记 CANCELED
- **AND** 已启动 run 走 requestCancel 流程

### 需求:issue 级修复跟踪

#### 场景:记录单漏洞修复

- **WHEN** 扫描阶段获取到一个 sonar issue
- **THEN** 在 `dev_issue` 记录其 `issue_key`、`rule_key`、`severity`、状态
- **AND** 后续修复、验证、提交各阶段回写该 issue 的状态、commit 与诊断报告

### 需求:启用仓库自动授权

#### 场景:启用项目

- **WHEN** 创建项目(默认 `enabled=true`)或启用一个已禁用项目
- **THEN** 平台用授权 token 把 bot 加为该仓库 collaborator(`write`)
- **AND** 授权成功后才持久化 `enabled=true`
- **AND** AI 后续仅对该仓库拥有写权限

#### 场景:授权失败阻止启用

- **WHEN** 启用时仓库不存在(Gitea 返回 404)
- **THEN** 报错并阻止本次启用,`enabled` 不被持久化

#### 场景:禁用或删除项目

- **WHEN** 项目被禁用或删除
- **THEN** 移除 bot 对该仓库的访问权限

#### 场景:同一仓库只能对应一个项目

- **WHEN** 创建项目时,归一化后的仓库链接已存在(无论原始写法是否相同)
- **THEN** 报错拒绝创建
- **AND** 避免多个项目共享同一仓库的 collaborator 而互相踩踏
