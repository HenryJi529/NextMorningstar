# admin-operations 规格

## 目的

平台管理员(`dev_admin` 角色)拥有跨项目归属的熔断能力:取消任何进行中的 Run、停用任何项目;操作打后端日志留痕。管理员不具备项目所有权语义:不能改他人项目配置,停用只写 `enabled=false` 且无对应启用能力。

## 需求

### 需求:管理员取消任何 Run

#### 场景:取消他人项目的进行中 Run

- **WHEN** 拥有 `dev:run:cancel` 权限的用户调用 `DELETE /dev/admin/run/{id}`,且目标 Run 处于可取消状态
- **THEN** Run 进入取消流程(与 owner 取消同一状态机路径)
- **AND** 不校验 `adminId` 归属
- **AND** 后端日志记录操作人、目标 Run、所属项目及取消前状态

#### 场景:取消不可取消状态的 Run

- **WHEN** 目标 Run 处于 PENDING/SUBMITTED/CLEANING/CLEANED 状态,管理员调用取消接口
- **THEN** 请求返回成功,状态机忽略取消请求(与 owner 取消路径行为一致)
- **AND** 后端日志记录该次调用

#### 场景:无权限用户调用管理员取消接口

- **WHEN** 不具备 `dev:run:cancel` 权限的用户调用 `DELETE /dev/admin/run/{id}`
- **THEN** 请求被权限框架拒绝(403)
- **AND** Run 状态不变

### 需求:管理员停用任何项目

#### 场景:停用他人项目

- **WHEN** 拥有 `dev:project:disable` 权限的用户调用 `POST /dev/admin/project/{id}/disable`
- **THEN** 目标项目 `enabled=false`
- **AND** 不校验 `adminId` 归属
- **AND** 后端日志记录操作人与目标项目
- **AND** 进行中的 Run 不受影响(继续走完状态机)

#### 场景:重复停用

- **WHEN** 目标项目已是 `enabled=false`,管理员再次调用停用接口
- **THEN** 接口返回成功(幂等空操作)

### 需求:管理员无启用与配置编辑能力

#### 场景:接口层面不存在管理员启用路径

- **WHEN** 管理员希望恢复被停用的项目
- **THEN** 系统中不存在管理员可用的 enable 端点
- **AND** 仅项目 owner 可通过既有 update 接口将 `enabled` 置回 true

#### 场景:管理员尝试修改他人项目配置

- **WHEN** `dev_admin` 角色用户对不属于自己的项目调用既有 update/delete 接口
- **THEN** 既有 `adminId` 归属校验拒绝该请求(行为与现状一致,管理员角色不豁免)
