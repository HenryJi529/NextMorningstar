# pipeline-ui 规格

## 目的

提供流水线的工作台操作(/dev,项目归属人)与平台级运行观测(/dev/admin,平台管理员)。状态机可视化组件两页共用。

## 需求

### 需求:我的工作台(项目归属人)

#### 场景:切换与接入项目

- **WHEN** 用户打开 `/dev`
- **THEN** 顶栏 Tab 列出自己的项目(按登录用户 id 匹配 `adminId` 过滤)
- **AND** 提供"接入新项目"入口(创建后自动选中)

#### 场景:查看与操作选中的项目

- **WHEN** 选中某个项目
- **THEN** 展示项目卡(链接/分支/enabled/上限)与 Run 历史、PR 历史
- **AND** owner 可编辑配置、手动触发、启停、删除;非 owner 仅只读

#### 场景:当前运行可视化

- **WHEN** 选中项目存在活跃 run(state 非 CLEANED/FAILED)
- **THEN** 渲染 7 节点状态机(启动/同步/扫描/修复/验证/提交/清理),当前节点高亮,轮询更新
- **WHEN** 无活跃 run
- **THEN** 该区块不渲染

#### 场景:手动触发满槽排队

- **WHEN** 手动触发时并发槽已满(决策 46 混合并发槽)
- **THEN** run 以 PENDING 排队,前端展示"排队中"而非假装已启动

### 需求:系统管理页(平台管理员)

#### 场景:平台总览

- **WHEN** 管理员打开 `/dev/admin`
- **THEN** 展示 KPI:接入仓库数、累计交付修复数(`deliveredIssueCount`)、PR 合并率、进行中占槽数(`executingRunCount`)
- **AND** KPI 与"正在运行"列表同口径(排除 PENDING)

#### 场景:正在运行熔断

- **WHEN** 存在进行中的 run
- **THEN** 每个 run 一张卡(项目名、迷你状态机、耗时)
- **AND** 管理员可取消(`DELETE /dev/admin/run/{id}`)

#### 场景:最近完成与项目列表

- **WHEN** 管理员查看最近完成任务列表与项目列表
- **THEN** run 列表按时间倒序展示结果/修复数/PR/耗时;项目列表只读,仅提供"停用"操作(`POST /dev/admin/project/{id}/disable`),无编辑无删除

#### 场景:入口显隐

- **WHEN** 登录用户 permissions 不含 `dev:run:cancel`/`dev:project:disable`
- **THEN** 不渲染系统管理页入口,直接访问路由被守卫拦截
