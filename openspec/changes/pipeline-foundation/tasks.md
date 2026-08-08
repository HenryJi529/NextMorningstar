## 1. 数据模型

- [x] 1.1 `create.sql` 新增 `dev_issue` 表(14 字段,`sonar_` 前缀统一);`dev_project` 加 `admin_id`/`enabled`/`name`/`sonar_project_key`;`dev_run` 不加 `finished_at`(update_time 即可)。
- [x] 1.2 新增 `Issue` PO(with `Severity`/`Type`/`Status` 枚举)与 `IssueMapper`;`Project` PO 加 `adminId`。
- [x] 1.3 `AbstractAction` 不额外加 `start_time`/`end_time` — `createTime`/`updateTime` 已满足(与 Run 同理,不冗余)。

## 2. 接口入口

- [x] 2.1 `ProjectService` 接口与 `ProjectServiceImpl` 实现:CRUD + `adminId` 权限校验。VO:`CreateProjectRequestVo`(name/link/branchName/description/maxFixesPerRun/adminId)、`UpdateProjectRequestVo`(仅 name/branchName/description/maxFixesPerRun 可改;link/sonarProjectKey 不可变)。
- [x] 2.2 `RunService` 接口与 `RunServiceImpl` 实现:`createRun`(无需权限，供调度器使用)/`triggerRun`(含 adminId 权限校验，供 Controller 使用)/`getRun`/`cancelRun`。
- [x] 2.3 `ProjectController` + `RunController` REST 接口。
- [x] 2.4 `ResponseCode` 新增 `DEV_PROJECT_NOT_FOUND`/`DEV_PROJECT_ACCESS_DENIED`/`DEV_RUN_NOT_FOUND`/`DEV_RUN_ACCESS_DENIED`。

## 3. 仓库自动授权(双 token)

- [x] 3.1 项目启用时,用授权 token(admin)调 Gitea API 把 bot 加为该仓库 collaborator(permission=`write`)。
- [x] 3.2 项目禁用时移除 bot collaborator。
- [x] 3.3 授权失败(仓库不存在,或无权访问——受控部署下不会出现)时,向项目经理报错并阻止启用。
- [x] 3.4 仓库链接归一化(`<origin>/<owner>/<repo>`)存储并加 UNIQUE;重复创建同一仓库报错(根因:collaborator 为仓库级,多 project 共享会互相踩踏)。

## 4. 定时调度

- [x] 4.1 `nightlyCreateRuns`:21:00 扫描 `enabled` 项目，对有活跃 run 的项目跳过，其余创建 PENDING run。
- [x] 4.2 `dispatchPendingRuns`:每 30s 查 PENDING run，按并发槽位数(默认 2)捞取并入队(START)。
- [x] 4.3 `cancelTimeoutRuns`:每 5min 查超过 60min 无响应的 run，触发取消(24h 时间窗口防止全表扫描)。
- [x] 4.4 `cancelOvernightRuns`:次日 6:00 取消所有非终态活跃 run(PENDING 直接标 CANCELED，其余走 requestCancel)。
- [x] 4.5 配置 `application-app.yml`:`schedule.create-cron`/`dispatch-cron`/`timeout-cron`/`cleanup-cron`/`run-timeout-minutes`/`max-concurrency`。
- [x] 4.6 `StartedTrigger` 加 `isCancelingRun` 检查：取消时走 CLEAN 而非 SYNC(与 Synced/Scanned/Fixed/Verified/Cleaned 六个 trigger 一致)。

## 5. 验证

- [x] 5.1 mock 模式下,手动触发的 run 从 `PENDING` 跑到 `CLEANED`。
- [x] 5.2 接口能正确查询 run 状态、issue 列表,并能取消运行中的 run。
- [x] 5.3 项目启用后 bot 能 clone 该仓库;禁用后 bot 无权访问。(✅ 8/7 随 SyncAction 全链路冒烟验证通过)
