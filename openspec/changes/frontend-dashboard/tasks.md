## 0. 基础设施

- [x] 0.1 路由/ico/BaseView 骨架(8/14)。
- [x] 0.2 `types/dev.ts` 全套类型 + `axios/dev.ts` 8 端点(8/14):契约对齐后端序列化规则(枚举 `name()`、UUID→string、`non_null` 下可空字段标 `?:`),返回类型 `AxiosResponse<R<T>>`,无 data 端点标 `R<void>`;拦截器死代码修复(`code !== SUCCESS` + `msg`)。
- [x] 0.3 `GET /dev/run` 列表接口(8/15):`projectId`/`adminId` 可选过滤、都不带查全量、按 `create_time` 倒序,读公开;`dev_run` 无 admin_id 列,adminId 过滤先查归属项目再 `in`。axios 加 `getAllRun`。
- [x] 0.4 `GET /dev/admin/stats` 平台统计(8/15):`pojo/bo/Stats`(projectCount/executingRunCount/deliveredIssueCount/prTotal/prMerged),读公开不加 `@PreAuthorize`(统计是"看数"非"操作",不发明第三个权限点);修复口径 = **属于 status=SUCCEEDED 的 run** 且 issue 状态 VERIFIED/ACCEPTED/REJECTED(失败/取消的 run 会 Restore 回滚,其 FIXED/VERIFIED issue 未交付,不计入)。axios 加 `getDevStats`。
- [x] 0.5 `ProjectDetail` 展示扩充(8/15):PO `Project` 不加展示字段,bo `ProjectDetail extends Project`(`@SuperBuilder`)补 `adminName`(取归属人 `username`,唯一登录名辨识度高;nickname 可选常为空,不做 fallback 链);`ProjectService` 所有返回 Project 的方法统一经 `toDetail` 扩充(单条转换为正典,批量即 `stream().map(this::toDetail)`,MVP 不做批量查询优化),Controller 返回类型随迁;前端同步形态:`ProjectDetail extends Project`(`adminName?: string`),axios 项目端点返回类型全部用 `ProjectDetail`。
- [x] 0.6 `RunDetail` 展示扩充(8/15):同 0.5 思路,bo `RunDetail extends Run` 补 `projectName`(项目可能已删而 run 是日志型数据不随删,归属项目为 null 时字段留空降级);`RunService` 出口统一经 `toDetail` 扩充;前端 `RunDetail extends Run`(`projectName?: string`),axios run 端点随迁。

## 1. 我的工作台(`/dev`)

- [ ] 1.1 项目 Tab 切换(按登录用户 id 匹配 `adminId` 过滤)+ "接入新项目"入口(创建抽屉,复用配置校验,创建后自动选中)。
- [ ] 1.2 项目卡:链接/分支/enabled/上限展示;owner 操作(编辑配置抽屉、手动触发、启停、删除);非 owner 只读。
- [ ] 1.3 当前运行状态机可视化(有活跃 run 才渲染):7 节点流水线,当前节点高亮 + 耗时,轮询更新;满槽触发停留 PENDING 时展示"排队中"(决策 46)。
- [ ] 1.4 Run 历史 / PR 历史 Tab:Run 表(runId/状态/触发方式/修复数/耗时/时间);PR 表(prId/链接 `project.link + /pulls/{prId}` 前端拼/prStatus)。

## 2. 系统管理页(`/dev/admin`)

- [ ] 2.1 KPI 行(来自 `getDevStats`):接入仓库数/累计交付修复数/PR 合并率/进行中占槽数;与"正在运行"列表同口径(排除 PENDING)。
- [ ] 2.2 正在运行:每个进行中 run 一张卡(项目名、迷你状态机、耗时)+ 管理员"取消"(`adminCancelRun`)。
- [ ] 2.3 最近完成任务列表(`getAllRun` 终态过滤):runId/项目/结果/修复数/PR/耗时,倒序。
- [ ] 2.4 项目列表(只读 + "停用" `adminDisableProject`,无编辑无删除):名称/归属人(`adminName`,0.5 已就绪)/分支/enabled。
- [ ] 2.5 入口与路由守卫:permissions 含 `dev:run:cancel`/`dev:project:disable` 才可见可进。

## 3. 后置(演示后)

- [ ] 3.1 run 实时状态机流转(SSE/WS 推送替代轮询)。
- [ ] 3.2 修复成功率、节省人月(20min/bug)。
- [ ] 3.3 高频缺陷 Top。
