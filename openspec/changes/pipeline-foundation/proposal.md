## 为什么

AI 漏洞修复流水线的状态机骨架(状态/事件/编排 Trigger/重试/取消)与数据模型(`dev_project`/`dev_run`/`dev_action_attempt`)已就绪,但 8 个 Action 仍为 Mock,且缺少 HTTP 入口、漏洞级数据载体与夜间触发。需要先补齐"地基":漏洞记录表、对外接口、定时调度骨架,使流水线可被手动触发与定时驱动,并在 mock 模式下端到端跑通,为后续真实 Action 实现铺路。

## 变更内容

- 新增 `dev_issue` 表(14 字段,`sonar_` 前缀统一)与 `Issue` PO(含 `Severity`/`Type`/`Status` 枚举)/`IssueMapper`。
- `dev_project` 加 `admin_id`/`name`/`sonar_project_key`/`enabled`;`Project` PO 同步更新。
- 新增 `ProjectService`/`RunService` 接口与实现,所有方法带 `adminId` 权限校验。
- 新增 `CreateProjectRequestVo`/`UpdateProjectRequestVo`(link/sonarProjectKey 不可变)。
- `ResponseCode` 新增 `DEV_PROJECT_NOT_FOUND`/`DEV_PROJECT_ACCESS_DENIED`/`DEV_RUN_NOT_FOUND`/`DEV_RUN_ACCESS_DENIED`。
- `AbstractAction` 不额外加时间字段 — `createTime`/`updateTime` 已满足。
- `dev_run` 不加 `finished_at` — `update_time` 即可(与决策 6 一致)。
- 待实现:`ProjectController`/`RunController`、定时调度骨架、Gitea 仓库授权。

## 能力

### 新增能力
- `dev-vuln-pipeline`:可手动/定时触发、可查询与取消的漏洞修复流水线运行,以 issue 级粒度跟踪修复进度。

## 影响范围

- `backend/web/src/main/sql/create.sql`:`dev_issue` 建表、`dev_project`/`dev_run` 加字段。
- `backend/web/src/main/java/com/morningstar/dev/`:新增 issue PO/Mapper、Controller、定时任务;修改 `Project`/`Run` PO、`AbstractAction`。
- `backend/web/src/main/resources/application-*.yml`:`morningstar.app.dev` 相关配置。
