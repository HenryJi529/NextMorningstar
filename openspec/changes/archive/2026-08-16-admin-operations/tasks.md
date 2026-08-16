## 1. 权限接入(纯配置)

- [x] 1.1 `application-perm.yml` 新增角色 `dev_admin`(工坊管理员)、权限点 `dev:run:cancel`(工坊任务取消权限) / `dev:project:disable`(工坊项目禁用权限)及角色-权限映射。
- [x] 1.2 `application-dev.yml` 的 `user-role` 段给演示账号(henry)分配 `dev_admin`;`application-prod.yml`(gitignored)同步给 Henry529 分配。

## 2. 管理员接口

- [x] 2.1 新增 `AdminController`(`/dev/admin/**`)+ 薄 Service。
- [x] 2.2 `DELETE /dev/admin/run/{id}`:`@PreAuthorize("hasAuthority('dev:run:cancel')")`;复用 `RunService.getRun`(不存在抛既有 `DEV_RUN_NOT_FOUND`)+ `StateMachineService.requestCancel`,不校验归属;`log.info` 记录操作人/Run/项目/取消前状态。**不改存量类**。
- [x] 2.3 `POST /dev/admin/project/{id}/disable`:`@PreAuthorize("hasAuthority('dev:project:disable')")`;`LambdaUpdateWrapper` 置 `enabled=false`(已停用直接返回,幂等),不校验归属,不级联取消进行中 Run;`log.info` 记录操作人/项目。

## 3. 前端

- [x] 3.1 `frontend/src/axios/dev.ts` 新增管理员接口定义:`adminCancelRun` / `adminDisableProject`。
- [x] 3.2 系统管理页入口移交 frontend-dashboard tasks 3.1。

## 4. 验证

- [x] 4.1 管理员取消他人项目的进行中 Run → Run 进入取消流程 + 日志留痕。
- [x] 4.2 无权限用户调 `/dev/admin/**` → 403,无副作用。
- [x] 4.3 管理员停用他人项目 → `enabled=false` + 日志留痕;进行中 Run 不受影响;重复停用幂等。
- [x] 4.4 owner 可 re-enable 被管理员停用的项目;管理员无 enable 端点可调。
