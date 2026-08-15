# pipeline-ui 规格

## 目的

提供流水线的项目操作入口(/dev 我的项目,项目归属人)、平台级运行观测(/dev/admin 系统管理,平台管理员)与平台叙事说明(/dev/about)。状态机可视化组件我的项目页/系统管理页共用。

## ADDED Requirements

### Requirement: 我的项目(项目归属人)

`/dev` 面向项目归属人:系统 SHALL 提供吸顶二级子菜单切换自己的项目、项目卡操作、当前任务状态机可视化、历史任务单表。

#### Scenario: 切换与接入项目

- **WHEN** 用户打开 `/dev`
- **THEN** 顶栏下方吸顶二级子菜单以项目胶囊列出自己的项目(按登录用户 id 匹配 `adminId` 过滤)
- **AND** 提供"接入新项目"弹窗表单(归属人灰显为当前用户,创建后自动选中)

#### Scenario: 查看与操作选中的项目

- **WHEN** 选中某个项目
- **THEN** 展示项目卡(链接/分支/enabled/上限/归属人)与历史任务单表
- **AND** 历史任务表 PR 列直接渲染 `RunDetail.prLink` + `prStatus` 徽章,无 PR 显示 —(PR 不拆表,`prId`/`prStatus` 本就是 run 的列)
- **AND** 修复数列渲染 `RunDetail.deliveredIssueCount`(交付口径:仅 SUCCEEDED 的 run 非 null,为该 run 下 VERIFIED/ACCEPTED/REJECTED issue 总数;非 SUCCEEDED 显示 —)
- **AND** owner 可编辑配置(弹窗)、手动触发、启停、删除;非 owner 仅只读

#### Scenario: 当前任务可视化

- **WHEN** 选中项目存在活跃 run(state 非 CLEANED/FAILED)
- **THEN** 渲染 7 节点状态机(启动/同步/扫描/修复/验证/提交/清理),当前节点高亮 + 耗时 + 当前阶段说明一行,轮询更新
- **AND** 头部展示阶段失败徽章(如"同步失败 ×1")
- **AND** 流水线下方展示回退环弧线(验证→修复):休眠时淡虚线,RESTORING 激活时虚线流动动画
- **AND** 漏斗计数条展示"扫描发现 / 本轮入选 / 已修复 / 验证通过"(本轮入选 = 经 maxIssuesPerRun 截断的入选数)
- **WHEN** 无活跃 run
- **THEN** 该区块不渲染

#### Scenario: 手动触发满槽排队

- **WHEN** 手动触发时并发槽已满(决策 46 混合并发槽)
- **THEN** run 以 PENDING 排队,前端展示"排队中"而非假装已启动

### Requirement: 系统管理页(平台管理员)

`/dev/admin` 面向平台管理员:系统 SHALL 提供平台 KPI 总览、项目列表(只读 + 停用)、正在运行熔断、最近完成列表;入口与路由按 permissions 显隐。

#### Scenario: 平台总览

- **WHEN** 管理员打开 `/dev/admin`
- **THEN** 展示 KPI:接入仓库数、累计交付修复数(`deliveredIssueCount`)、PR 合并率、进行中占槽数(`executingRunCount`)
- **AND** KPI 与"正在运行"列表同口径(排除 PENDING)

#### Scenario: 布局顺序

- **WHEN** 渲染系统管理页
- **THEN** 自上而下为:KPI 行 → 项目列表(整行)→ 正在运行 → 最近完成(收尾)

#### Scenario: 正在运行熔断

- **WHEN** 存在进行中的 run
- **THEN** 每个 run 一张卡(项目名、触发方式、迷你状态机、耗时)
- **AND** 管理员可取消(`DELETE /dev/admin/run/{id}`)

#### Scenario: 最近完成与项目列表

- **WHEN** 管理员查看最近完成任务列表与项目列表
- **THEN** run 列表按时间倒序展示结果/修复数/PR/耗时;项目列表只读,仅提供"停用"操作(`POST /dev/admin/project/{id}/disable`),无编辑无删除

#### Scenario: 入口显隐

- **WHEN** 登录用户 permissions 不含 `dev:run:cancel`/`dev:project:disable`
- **THEN** 不渲染系统管理页入口,直接访问路由被守卫拦截

### Requirement: 平台说明页

`/dev/about` 面向演示叙事:系统 SHALL 展示流水线怎么跑、安全保障、三层身份、三步接入引导。

#### Scenario: 平台叙事

- **WHEN** 用户打开 `/dev/about`
- **THEN** 展示 Hero + 流水线 7 阶段说明(带执行者徽章:平台/AI/人)+ 安全三卡(凭证隔离/最小权限/出站白名单)+ 三层身份(owner/bot/平台管理员)+ 三步接入引导与 CTA
