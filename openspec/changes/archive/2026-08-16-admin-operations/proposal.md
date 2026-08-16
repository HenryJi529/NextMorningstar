# 平台管理员操作(代码工坊)

## 为什么

dev 模块目前只有"读公开、写私有"两级权限(决策 38):写接口一律校验 `adminId`(项目归属人)。平台运行中出现归属人处理不了的运维场景——Run 卡死占并发槽(全局仅 2 个)、项目配置错误反复失败刷表——需要平台管理员角色拥有**跨归属的熔断能力**,但不染指项目所有权。

## 变更内容

- 接入既有权限框架(`application-perm.yml` + `@PreAuthorize`):新增角色 `dev_admin`(工坊管理员)与权限点 `dev:run:cancel`、`dev:project:disable`。
- 新增管理员专属接口(与 owner 接口分离,不复用):
  - `DELETE /dev/admin/run/{id}`:取消**任何**进行中的 Run。
  - `POST /dev/admin/project/{id}/disable`:停用**任何**项目(`enabled=false`,幂等)。
- 管理员操作打 `log.info` 留痕(操作人/动作/目标项目/目标 Run/当时状态),**不建审计表、不提供查询接口**——MVP 阶段查日志即可,将来产品化再补 `dev_admin_operation` 表(表结构见 design 决策 6 留档)。
- `Project.enabled` 保持单布尔字段:owner 可自由启停自己的项目;管理员**只能写 false**(接口层面无 enable 能力),不提供 re-enable。owner 与管理员拉锯风险 MVP 阶段接受,日志可查;将来确有需要再加 `disabled_by` 可空列升级。

## 能力

### 新增能力
- `admin-operations`:平台管理员跨归属取消 Run、停用项目,操作打日志留痕。

## 影响范围

- `application-perm.yml` / `application-dev.yml`:新增 `dev_admin` 角色、两个权限点、演示账号角色分配。
- 新增 `AdminController`(`/dev/admin/**`)+ 薄 Service(复用 `RunService`/`ProjectService` 与 `StateMachineService` 现有方法,**存量代码零改动**)。
- 前端系统管理页:管理员操作入口(归 frontend-dashboard 实现)。
- **不影响**:owner 侧接口、所有 dev 表结构、状态机与调度逻辑。
