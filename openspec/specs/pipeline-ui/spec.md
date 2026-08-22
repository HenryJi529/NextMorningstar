# pipeline-ui 规格

## 目的

提供流水线的项目操作入口(/dev 我的项目,项目管理员)、平台级运行观测(/dev/admin 平台运维,平台管理员)与平台叙事说明(/dev/about)。状态机可视化组件我的项目页/平台运维页共用。

## Requirements

### Requirement: 我的项目(项目管理员)

`/dev` 面向项目管理员:系统 SHALL 提供吸顶二级子菜单切换自己的项目、项目卡操作、当前任务状态机可视化、历史任务单表。

#### Scenario: 切换与接入项目

- **WHEN** 用户打开 `/dev`
- **THEN** 顶栏下方吸顶二级子菜单以项目胶囊列出自己的项目(按登录用户 id 匹配 `adminId` 过滤)
- **AND** 提供"接入新项目"弹窗表单(管理员灰显为当前用户,创建后自动选中)

#### Scenario: 查看与操作选中的项目

- **WHEN** 选中某个项目
- **THEN** 展示项目横幅(三行规格表,标签带冒号对齐;左侧信息区 `flex-1` 最小宽度 280px;右侧操作按钮组 xl 断点切换:xl 以下直接 2×2 等宽网格,xl 起一行四个,不无限挤压信息区):`项目描述:`(可空,单行截断 + title 悬浮全文)/`仓库信息:`(链接以 owner/repo 短形展示、点击新标签直达仓库、title 悬浮完整链接;分支随行)/`配置信息:`(单轮任务处理问题上限 Sonar X · AI Y;调度启停 启用/停用)
- **AND** 子菜单只列出登录用户自己的项目(按 `adminId` 过滤),他人项目不可见,故不存在"非 owner 只读"视图
- **AND** 操作 = 手动触发 / 编辑配置(弹窗,禁改仓库链接)/ 停用调度、启用调度(切换"调度启停"开关,按钮四字明示调度语义)/ 删除项目(确认弹窗)
- **AND** 历史任务表列为:任务编号(id 前 8 位等宽字体,title 悬浮全 id,点击复制完整 runId——`CopyableId` 组件:复制图标常显、hover 加深、复制成功打勾 1.5s,非安全上下文降级 execCommand,与管理页运行卡同口径 8 位)/触发方式(手动/调度)/运行结果(状态徽章居中)/发现问题数(居中,`scannedIssueCount` 真实数值,未扫描 —,title 悬浮说明"本次任务扫描发现的问题总数(SonarQube + AI)")/已交付修复数(居中,`deliveredIssueCount`,交付口径:仅 SUCCEEDED 非 null,为该 run 下 VERIFIED/ACCEPTED/REJECTED issue 总数,否则 —;发现问题数/已交付修复数构成"发现 → 交付"递进,漏斗其余过程量(本轮入选/已修复/验证通过)只在当前任务区块展示)/PR(`RunDetail.prLink` + `prStatus` 徽章居中,新标签打开,无 PR 显示 —)/开始时间(居中)/结束时间(居中)/任务耗时;除任务耗时右对齐外全部列居中;`table-fixed` + colgroup 固定列宽(11/9/9/11/11/13/13/13/10,近似均分,时间列不额外加宽);xl 以下隐藏开始/结束时间两列(col/th/td 同步 `hidden xl:table-column|table-cell`,耗时列保留可随时推算)
- **AND** 历史任务表服务端分页(8/17):列表接口 `pageNum`/`pageSize` 必填(返回 `PageResult`),`statuses=SUCCEEDED,FAILED,CANCELED` 终态过滤(进行中的 run 不进历史表,分页计数精确),`sortDir=DESC` 按更新时间倒序(updateTime 主序 + createTime 兜底,8/22 起——此前按创建时间,先创建后完成的 run 会沉底),每页 10 条;PageSwitcher 放"历史任务"标题行右侧(8/17 晚由表底挪入,与卡片标题同一排),仅 ‹ 当前页/总页数 › 三元素(浅灰、hover 橙,仅一页时组件自隐藏——隐藏判断收进 PageSwitcher 自身,父级只传 pageNum/totalPageNum;轮询抽空末页自动回退一页)

#### Scenario: 当前任务可视化

- **WHEN** 选中某个项目
- **THEN** 当前任务区块常驻渲染 7 节点状态机(启动/同步/扫描/修复/验证/提交/清理),统一 3s 轮询:状态机/漏斗由活跃快照驱动(列表接口 `projectId` + 第 1 页 1 条,最新一条非终态即活跃——单项目同时至多一个活跃 run),历史表按当前页轮询(列表出口均带全量 Detail)
- **AND** 存在活跃 run 时:当前节点高亮 + 耗时 + 当前阶段说明一行;无活跃 run 时渲染空闲骨架(节点全灰、漏斗全 —、头部提示"暂无进行中的任务"、阶段说明引导手动触发);节点内嵌图标:未到达/当前节点各显示专属 antd 图标(启动 Rocket/同步 Sync/扫描 Scan/修复 Tool/验证 SafetyCertificate/提交 CloudUpload/清理 Clear,当前节点橙色、取消中琥珀、未到达浅灰),已完成节点统一打勾
- **AND** 存在活跃 run 且非 PENDING 时头部提供"取消"按钮(`DELETE /dev/run/{id}`,确认弹窗说明"取消不会立即中断,等当前阶段动作到达检查点后回滚清理"后发出取消指令);PENDING 排队中不提供
- **AND** run 进入 CANCELING 后流水线整体转为取消态视觉(当前节点琥珀脉冲、头部右侧"取消中"徽章——与平台运维卡头同位置、阶段标题"取消中"并说明等待检查点回滚清理),取消按钮不再显示(status 非 RUNNING)
- **AND** 头部"已耗时"为本地 1s 秒表(真实时间减 `createTime`,`fmtElapsed` 秒级 H:MM:SS 格式,与历史表"X.X 分"口径区分),与轮询解耦
- **AND** 可视化数据来自 `RunDetail.actionAttemptBriefs` 阶段执行流水(`actionType`/`attemptNo`/`status`/起止时间):节点耗时 = attempt 的 createTime→updateTime,RESTORE 条数即回退环激活次数
- **AND** 流水线区不做响应式重排:容器 720px 最小宽度(7 节点 48px + 6 连接线 64px),窄屏横向滚动,不挤压节点
- **AND** 头部展示阶段失败徽章(如"同步失败 ×1",流水中 FAILED 按 actionType 分组计数)
- **AND** 头部 runId 前 8 位可点击复制完整 runId(`CopyableId` 组件,排错时复制 runId 去查 DB/日志)
- **AND** 流水线下方展示回退环弧线(验证→修复):休眠时淡虚线,RESTORING 激活时虚线流动动画
- **AND** 漏斗计数条展示"扫描发现问题 / 本轮入选 / 已修复 / 验证通过"(数据源 `RunDetail.scannedIssueCount`/`selectedIssueCount`/`currentFixedIssueCount`/`currentVerifiedIssueCount`;已修复/验证通过为累计口径——含其后状态,漏斗单调不减;**四值统一以 SCAN 成功为闸**,此前全部 null 显示 —,不返回 0 冒充"已扫完但没有";扫描发现问题显示真实数值,不做 99+ 折叠)

#### Scenario: 手动触发满槽排队

- **WHEN** 手动触发时并发槽已满(决策 46 混合并发槽)
- **THEN** run 以 PENDING 排队,前端展示"排队中"而非假装已启动

### Requirement: 平台运维页(平台管理员)

`/dev/admin` 面向平台管理员:系统 SHALL 提供平台 KPI 总览、项目列表(只读 + 调度启停)、当前任务熔断、最近完成列表;入口与路由按 `dev_admin` 角色显隐,操作按钮按权限点控制。

#### Scenario: 平台总览

- **WHEN** 管理员打开 `/dev/admin`
- **THEN** 展示 KPI(顺序固定):接入仓库数(`projectCount`,附启用中 `enabledProjectCount`)、并发任务数(`executingRunCount`/`maxConcurrency`,附排队中 `pendingRunCount`)、累计交付修复数(`deliveredIssueCount`,附已采纳 `acceptedIssueCount`)、PR 合并率(`prMerged`/`prTotal` 前端算百分比)、累计节约人天(`savedPersonDays`,按已采纳修复的估算工时,压轴)
- **AND** KPI 与"当前任务"列表同口径(排除 PENDING)
- **AND** KPI/项目列表/当前任务/最近完成四路统一 3s 轮询(各带分页参数,8/17 起)
- **AND** Stats 额外下发调度时段 `scheduledStartTime`/`scheduledEndTime`(LocalTime 序列化为 HH:mm:ss,源自配置 `create-cron`/`cleanup-cron`,展示时截取到分);调度时段不进页面内容区,而是由 BaseView 拉一次 stats 后在顶栏导航右侧常驻胶囊(钟表图标 + "夜间调度 HH:mm–HH:mm",橙色浅底 `bg-orange-50` 配主题,非白底与顶栏区分——8/16 晚由深灰 `bg-slate-800` 调浅,用户评深色太别扭),三个 dev 页面共享

#### Scenario: 布局顺序

- **WHEN** 渲染平台运维页
- **THEN** 自上而下为:KPI 行 → 项目列表(整行)→ 当前任务 → 最近完成(收尾)

#### Scenario: 当前任务熔断

- **WHEN** 存在进行中的 run
- **THEN** 每个 run 一张卡(项目名、runId 前 8 位可点击复制完整 runId、触发方式、迷你状态机、秒级 H:MM:SS 实时已耗时,与我的项目页"已耗时"同 `fmtElapsed` 口径);卡头右侧仅展示占槽信息(调度时段在顶栏胶囊,见"平台总览");进行中列表来自列表接口 `statuses=RUNNING,CANCELING` + `sortDir=ASC` 首页大页快照(覆盖并发槽与排队,8/17 起;8/18 起按创建时间升序,先创建的先跑/先排,贴合分发顺序)
- **AND** PENDING 排队的 run 显示"排队中"徽章,不提供取消按钮
- **AND** 管理员可取消(`DELETE /dev/admin/run/{id}`,确认弹窗说明"取消不会立即中断,等当前阶段动作到达检查点后回滚清理"后发出取消指令);CANCELING 中的 run 同步转取消态视觉(卡头"取消中"琥珀徽章 + 迷你状态机当前段琥珀、标题"取消中",与我的项目页同款),取消按钮不再显示

#### Scenario: 最近完成与项目列表

- **WHEN** 管理员查看最近完成任务列表与项目列表
- **THEN** 最近完成表服务端分页(8/17:`statuses=SUCCEEDED,FAILED,CANCELED` 终态过滤 + `pageNum`/`pageSize` 必填,每页 8 条按更新时间倒序(`sortDir=DESC`,8/22 起 updateTime 主序)——分页后不能再靠前端正过滤,否则进行中的 run 混入首页造成缺行);最近完成表与我的项目页历史任务表同口径(8/16 晚定稿):列为 项目(居中;归属项目已删除的 run(`projectName` 为 null 降级)渲染"已删除项目"虚线框标记,悬浮说明记录保留作为历史存档)/任务编号(居中,id 前 8 位,点击复制完整 runId,`CopyableId` 组件)/触发方式(手动/调度,居中)/运行结果(徽章居中)/发现问题数(居中,真实数值,title 悬浮说明)/已交付修复数(居中,非 SUCCEEDED —)/PR(居中,`prLink` + `prStatus` 徽章,新标签打开)/开始时间(居中)/结束时间(居中)/任务耗时(右);`table-fixed` + colgroup 列宽 15/12/7/8/9/9/9/11/11/9(10 列,8/18 起;PR 窄化至 9%,省宽补项目列——项目名变长文本最吃宽),xl 以下隐藏开始/结束时间两列;项目列表列为 项目名称(居中)/项目管理员(居中)/仓库链接(居中,owner/repo 短形,点击新标签跳转,title 悬浮完整链接)/仓库分支(居中)/单轮 Sonar 处理上限/单轮 AI 处理上限(单轮上限拆两列,居中,title 悬浮说明"单轮任务处理的 SonarQube/AI 发现问题数上限")/调度启停(居中,启用/停用)/操作(居中),只读,仅提供调度启停操作(`POST /dev/admin/project/{id}/schedule` 双向切换,确认弹窗,按钮随当前状态显示"停用调度/启用调度"),无编辑无删除;项目列表同样服务端分页(每页 10 条,按接入时间倒序,8/17 起);项目列表与最近完成两表的 PageSwitcher 同样放卡片标题行右侧,与我的项目页同款(‹ 当前页/总页数 › 三元素,仅一页时组件自隐藏)

#### Scenario: 入口显隐

- **WHEN** 登录用户不具备 `dev_admin` 角色
- **THEN** 不渲染平台运维页入口(导航按 `Role.DEV_ADMIN` 显隐),直接访问路由被守卫拦截(路由 meta.permissions=[Role.DEV_ADMIN])
- **AND** 页内操作按钮按权限点二次控制:取消按钮需 `dev:run:cancel`、调度启停按钮需 `dev:project:schedule`

### Requirement: 平台介绍页

`/dev/about` 面向演示叙事:系统 SHALL 展示流水线怎么跑、安全保障、三层身份、三步接入引导。

#### Scenario: 平台叙事

- **WHEN** 用户打开 `/dev/about`
- **THEN** 展示 Hero + 流水线 7 阶段说明(序号圆全橙色统一,无执行者徽章——8/16 晚去掉,标签信息密度低且与描述重复)+ 三层身份(项目管理员/流水线机器人/平台管理员——8/22 正名:机器人不止提交,拉取/提交/推送/开 PR 都以它的名义)+ 安全三卡(凭证隔离/最小权限/凭证脱敏——8/22 纠正:出站白名单未实现,第三卡改为已实现的凭证脱敏,白名单降级为未来规划,见 dev-report 第十三节)+ 三步接入引导与 CTA;板块顺序 机制→角色→信任→行动(8/22 定稿,理由见 dev-report 1.4)
- **AND** 文案不写死调度时间(8/16 晚定稿:"每晚 21:00"改"每晚调度时段自动开跑",具体时间唯一展示点是顶栏调度时段胶囊,调 cron 不产生文案漂移)
