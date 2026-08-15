## 0. 基础设施

- [x] 0.1 路由/ico/BaseView 骨架(8/14)。
- [x] 0.2 `types/dev.ts` 全套类型 + `axios/dev.ts` 8 端点(8/14):契约对齐后端序列化规则(枚举 `name()`、UUID→string、`non_null` 下可空字段标 `?:`),返回类型 `AxiosResponse<R<T>>`,无 data 端点标 `R<void>`;拦截器死代码修复(`code !== SUCCESS` + `msg`)。
- [x] 0.3 `GET /dev/run` 列表接口(8/15):`projectId`/`adminId` 可选过滤、都不带查全量、按 `create_time` 倒序,读公开;`dev_run` 无 admin_id 列,adminId 过滤先查归属项目再 `in`。axios 加 `getAllRun`。
- [x] 0.4 `GET /dev/admin/stats` 平台统计(8/15):`pojo/bo/Stats`(projectCount/enabledProjectCount/executingRunCount/pendingRunCount/maxConcurrency/deliveredIssueCount/prTotal/prMerged——占槽分母 maxConcurrency 来自配置 `morningstar.app.dev.schedule.max-concurrency`,面板展示必需的唯一配置项;百分比/占槽比均由前端算,后端只出原始计数与配置),读公开不加 `@PreAuthorize`(统计是"看数"非"操作",不发明第三个权限点);修复口径 = **属于 status=SUCCEEDED 的 run** 且 issue 状态 VERIFIED/ACCEPTED/REJECTED(失败/取消的 run 会 Restore 回滚,其 FIXED/VERIFIED issue 未交付,不计入)。axios 加 `getDevStats`。
- [x] 0.5 `ProjectDetail` 展示扩充(8/15):PO `Project` 不加展示字段,bo `ProjectDetail extends Project`(`@SuperBuilder`)补 `adminName`(取归属人 `username`,唯一登录名辨识度高;nickname 可选常为空,不做 fallback 链);`ProjectService` 所有返回 Project 的方法统一经 `toDetail` 扩充(单条转换为正典,批量即 `stream().map(this::toDetail)`,MVP 不做批量查询优化),Controller 返回类型随迁;前端同步形态:`ProjectDetail extends Project`(`adminName?: string`),axios 项目端点返回类型全部用 `ProjectDetail`。
- [x] 0.6 `RunDetail` 展示扩充(8/15):同 0.5 思路,bo `RunDetail extends Run` 补 `projectName`(项目可能已删而 run 是日志型数据不随删,归属项目为 null 时字段留空降级)与 `prLink`(复用 `GiteaUtil.getPrLink` 拼 `backendOrigin/owner/repo/pulls/{prId}`,与 PR body 文件链接同 origin 口径;`project == null` 或 `prId == null` 时留空降级)及 `deliveredIssueCount`(**交付口径,同 Stats.deliveredIssueCount**:仅 `status=SUCCEEDED` 的 run 非 null,值为该 run 下 VERIFIED/ACCEPTED/REJECTED issue 总数;失败/取消的 run 会 Restore 回滚,其 issue 未交付,字段留 null);`RunService` 出口统一经 `toDetail` 扩充;前端 `RunDetail extends Run`(`projectName?: string` + `prLink?: string` + `deliveredIssueCount?: number`),axios run 端点随迁。
- [x] 0.7 `Run.triggerType` 触发方式(8/15):PO `Run` 加嵌套枚举 `TriggerType { MANUAL, SCHEDULED }` + 字段 `triggerType`(列 `trigger_type`,`trigger` 是 MySQL 保留字;枚举不叫 `Trigger` 避免与状态机 `statemachine/Trigger` 接口撞名);`createRun(projectId, triggerType)` 由调用方声明——`triggerRun` 传 `MANUAL`、`CronTask` 传 `SCHEDULED`;真实属性进 PO 而非 Detail;前端 `RunTriggerType` 枚举 + `Run.triggerType`,展示映射 MANUAL→手动触发/SCHEDULED→夜间调度。
- [x] 0.8 UI 原型定稿(8/15,`frontend/prototype/dev.html`,Vue3 CDN + Tailwind):浅色橙主题(底 `#f3f4f6`、白卡 slate-200 边、主色 orange-500、语义浅底徽章 emerald/rose/amber/violet);三页 IA、吸顶二级子菜单、通栏布局、回退环等交互均以原型为准,真实页面照原型实现。
- [x] 0.9 `RunDetail.actionAttemptBriefs` 阶段流水(8/15):bo `ActionAttemptBrief`(`actionType/attemptNo/status/createTime/updateTime`,不含 `result`——ScanResult 带全量 issueKeys 体积大;查询层列裁剪使 result 列不出库),`toDetail` 统一填充(getRun/listRun/triggerRun 出口一致;cancelRun 两处仅需 PO 鉴权,改为自己 `selectById` 不借 getRun);前端画图口径:节点耗时 = attempt 的 createTime→updateTime,阶段失败徽章 = FAILED 按 actionType 分组计数,回退环激活次数 = RESTORE 条数。配套 `CopyUtil` 健壮化(infra):泛型签名改 `Object`、两侧沿父类链按名字匹配字段(跨类 PO→Brief 拷贝安全、target 缺字段跳过、同名遮蔽只留子类、static/synthetic 不拷);前端 `types/dev.ts` 加 `ActionType`/`ActionStatus` 枚举与 `ActionAttemptBrief` 接口。

## 1. 我的项目(`/dev`)

- [ ] 1.1 项目切换为吸顶二级子菜单(项目胶囊,按登录用户 id 匹配 `adminId` 过滤;与顶栏同一 sticky 容器)+"接入新项目"弹窗表单(复用配置校验,归属人灰显当前用户,创建后自动选中)。
- [ ] 1.2 项目卡:链接/分支/enabled/上限/归属人展示;owner 操作(编辑配置弹窗、手动触发、启停、删除);非 owner 只读。
- [ ] 1.3 当前任务状态机可视化(有活跃 run 才渲染):7 节点流水线 + 当前节点高亮 + 耗时 + 当前阶段说明一行,轮询更新;头部阶段失败徽章(如"同步失败 ×1",数据源 `actionAttemptBriefs`(0.9):FAILED 按 actionType 分组计数,前端聚合,无需专用接口);流水线下方回退环弧线(验证→修复,RESTORING 是节点间回路不是节点:休眠淡虚线、激活虚线流动动画);漏斗计数条"扫描发现 / 本轮入选 / 已修复 / 验证通过"(本轮入选 = 经 maxIssuesPerRun 截断的入选数;"扫描发现"总数只能从 action_attempt.result 的 issueKeys 解析,**MVP 暴露方式未定,可先隐藏**);满槽触发停留 PENDING 时展示"排队中"(决策 46)。
- [ ] 1.4 历史任务表(单表通栏,PR 不拆表——`prId`/`prStatus` 本就是 run 的列):任务 id/结果/触发方式(`triggerType`,0.7)/修复数(`deliveredIssueCount`,0.6;非 SUCCEEDED 为 null 显示 —)/PR(有则渲染 `prLink` 链接(后端已拼好,0.6) + prStatus 徽章,无则 —)/开始时间(`createTime`)/结束时间(终态 run 的 `updateTime`)/耗时(前端算差值)。

## 2. 系统管理页(`/dev/admin`)

- [ ] 2.1 KPI 行(来自 `getDevStats`,0.4):接入仓库数(附"N 个启用中"=`enabledProjectCount`)/累计交付修复数/PR 合并率(`prMerged`/`prTotal` 算百分比,附"n/m 已合并")/进行中占槽(`executingRunCount`/`maxConcurrency`,附排队中 `pendingRunCount`);与"正在运行"列表同口径(排除 PENDING)。
- [ ] 2.2 项目列表(整行,紧跟 KPI;只读 + "停用" `adminDisableProject`,无编辑无删除):名称/归属人(`adminName`,0.5 已就绪)/分支/enabled。
- [ ] 2.3 正在运行:每个进行中 run 一张卡(项目名、触发方式、迷你状态机、耗时——状态机/耗时数据源同 0.9 `actionAttemptBriefs`,列表出口已带)+ 管理员"取消"(`adminCancelRun`)。
- [ ] 2.4 最近完成任务列表(`getAllRun` 终态过滤,放最后):runId/项目/结果/修复数/PR/耗时,倒序。
- [ ] 2.5 入口与路由守卫:permissions 含 `dev:run:cancel`/`dev:project:disable` 才可见可进。

## 3. 平台说明页(`/dev/about`)

- [ ] 3.1 Hero + 流水线怎么跑(7 阶段说明,带执行者徽章:平台/AI/人)。
- [ ] 3.2 安全三卡(凭证隔离/最小权限/出站白名单)+ 三层身份(owner/bot/平台管理员)。
- [ ] 3.3 三步接入引导 + CTA 跳"我的项目"。

## 4. 后置(演示后)

- [ ] 4.1 run 实时状态机流转(SSE/WS 推送替代轮询)。
- [ ] 4.2 修复成功率、节省人月(20min/bug)。
- [ ] 4.3 高频缺陷 Top。
