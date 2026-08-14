## 上下文

dev 模块权限现状(决策 38):读接口公开(登录即可),写接口(create/update/delete/trigger/cancel)校验 `adminId` 归属。平台侧无任何跨归属写能力。平台已有通用权限框架:`application-perm.yml` 声明角色/权限点 + 角色-权限映射(`CommonInitializer.initializePerm()` 启动时同步入库),`application-dev.yml` 的 `user`/`user-role` 段配置初始用户与角色分配;登录时 `UserDetailsServiceImpl` 把权限 tag 装入 `LoginUser.permissions` → authorities,Controller 用 `@PreAuthorize("hasAuthority('...')")` 拦截(blog/proxy 模块已在用)。dev 模块尚未接入。

## 目标 / 非目标

**目标:** 平台管理员可跨归属取消 Run、停用项目;操作打日志留痕。
**非目标:** 不做管理员编辑项目配置(配置错误由 owner 自己改,权责分离);不做管理员创建/删除项目;不建 `disabled_by` 状态字段;**不建审计表/查询接口**(MVP 查日志,留档见决策 6);不做角色管理 UI(角色分配走 yml + 既有 sys 接口)。

## 决策

### 决策 1:复用既有权限框架,新增 `dev_admin` 角色

不新建角色体系、不动 `sys_user` 表。`application-perm.yml` 追加:

```yaml
roles:
  - tag: "dev_admin"
    status: true
    name: "工坊管理员"
permissions:
  - tag: "dev:run:cancel"
    status: true
    name: "工坊任务取消权限"
  - tag: "dev:project:disable"
    status: true
    name: "工坊项目禁用权限"
role-permissions:
  dev_admin:
    - "dev:run:cancel"
    - "dev:project:disable"
```

`application-dev.yml` 的 `user-role` 段给演示账号(henry)加 `dev_admin`。权限点拆两个而非一个 `dev:admin`,与既有粒度(`blog:article:manage` 等一操作一点位)对齐,将来增减管理员能力不改角色定义。

### 决策 2:管理员接口独立 `/dev/admin/**`,不与 owner 接口复用

不改造 `DELETE /dev/run/{id}` 为"owner 或管理员均可"(那要在 service 层判归属 + 查权限,两路径纠缠)。管理员走独立端点:

- `DELETE /dev/admin/run/{id}` — `@PreAuthorize("hasAuthority('dev:run:cancel')")`,不校验归属
- `POST /dev/admin/project/{id}/disable` — `@PreAuthorize("hasAuthority('dev:project:disable')")`,只置 `enabled=false`

owner 侧接口零改动。

### 决策 3:`enabled` 保持单布尔,管理员只写 false

不为"谁停的"加 `disabled_by` 列——"owner 与管理员拉锯"是无真实输入触发的假设性失败,MVP 不建模(与"受控部署不写无用守卫"一致)。规则:

- owner 对自己的项目:开/关自由(既有 update 接口)
- 管理员:只有 disable 端点,**无 enable 端点**——接口层面就不存在管理员开启能力
- disable 幂等:已停用再停用为成功空操作
- 被管理员停用后 owner 可 re-enable;发生拉锯时查后端日志线下沟通
- 将来确需区分:加 `disabled_by` 可空列 + 一条校验即可,不影响现有表结构演进

disable 不级联取消进行中的 Run——停用只影响后续调度;进行中的 Run 由管理员显式取消,两个动作独立。

### 决策 4:管理员角色无项目所有权语义,但不主动禁止创建

"管理员不能创建任何项目"指**权限模型**:`dev_admin` 角色不携带项目创建/所有权相关能力,管理员页面不提供创建入口。后端**不加**"是管理员就禁止创建"的校验(无用守卫)——管理员账号同时作为普通登录用户创建自有项目不受影响,这也是平台自跑项目的前提(下条)。

### 决策 5:平台自跑项目(NextMorningstar nightly)归属专门服务账号

不归属管理员。建普通服务账号(如 `morningstar-nightly`)作为 `adminId`,走种子数据/普通创建流程入库;每晚 21:00 cron 照常触发,占用并发槽、走同一状态机,无任何特例。与 Gitea 侧提交 PR 的 bot(HaibaraAi369)身份链对应:owner(服务账号)/ bot(提交者)/ 平台管理员(熔断者)三层分离。本条为部署约定,无代码改动。

### 决策 6:留痕打日志,不建审计表(8/15 简化)

原方案新建 `dev_admin_operation` 表 + `GET /dev/admin/operations` 查询接口,评估后**降级**:管理员操作用 `log.info` 记录(操作人 username/动作/目标项目名/目标 Run id/当时状态),MVP 阶段审计靠查后端日志,频率极低、团队内部沟通足够。

**留档(将来产品化时捡回):** 表结构 `dev_admin_operation(id int auto_increment, operator_id binary(16), operation varchar(32), project_id binary(16), run_id binary(16) null, detail text, create_time datetime)`;`detail` 存人读快照(项目名/run id/取消前状态),项目删除后记录仍可读;查询接口读公开、按 id 倒序;写路径与操作同事务。届时 `StateMachineService.requestCancel` 需改返回 `boolean`(区分接受/静默忽略),accepted 才落记录。

## 实现要点

- `AdminController` + 薄 Service:取消路径复用 `RunService.getRun`(404 用既有 `DEV_RUN_NOT_FOUND`)+ `StateMachineService.requestCancel`(非可取消状态由它 log.warn 忽略,与 owner 路径行为一致);停用路径 `projectMapper.selectById` + `LambdaUpdateWrapper` 置 `enabled=false`(已停用直接返回,幂等)。**不改任何存量类**。
- 响应码复用既有 `DEV_PROJECT_NOT_FOUND`/`DEV_RUN_NOT_FOUND`;403 由 Security 框架 + `GlobalExceptionHandler` 处理(blog 模块已验证该链路)。

## 权限矩阵(终态)

| 能力 | 普通登录用户(含 nightly 服务账号) | `dev_admin` |
|---|---|---|
| 读所有项目/Run/配置 | ✅ | ✅ |
| 创建项目(成为 owner) | ✅ | ✅(作为普通用户身份,非角色能力) |
| 改/删/启停自己的项目 | ✅(owner) | ✅(仅对自己拥有的项目,同左) |
| 触发/取消自己的 Run | ✅(owner) | ✅(仅对自己拥有的项目,同左) |
| 取消任何 Run | ❌ | ✅ |
| 停用任何项目 | ❌ | ✅(只写 false,无 enable) |
| 改他人项目配置 | ❌ | ❌ |
