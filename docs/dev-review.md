# 代码评审指南

## 仓库结构说明

本仓库是开发者的个人网站项目（monorepo），「代码工坊 · AI 自动值守的代码质量优化流水线」是其中的 dev 模块 。网站的其他业务模块（博客、相册、导航等）与全站基础设施（认证与授权框架、通用组件、部署脚本等）为既有成果，不属于本次参赛内容，评委无需细读。


## 建议阅读顺序

1. 设计文档（先看，了解全貌）
	- `docs/dev-report.md`：最终汇报材料：痛点、核心流程、可靠性、架构、 安全模型、KPI、开发过程、答辩 Q&A 
	- `docs/dev-项目架构图.drawio(.svg/.png)`：系统架构图（四色图例：自研组件/三方工具/外部服务/数据存储）
	- `docs/dev-状态转移图.drawio(.svg/.png)`：18 态状态机完整状态转移图
	- `docs/dev-plan.md`：开发计划与设计决策记录（52条决策，含安全模型设计过程）
	- `openspec/specs/`：10 个能力规格（流水线各环节的正式 spec）
	- `openspec/changes/archive/`：全部变更的归档留痕（提案 + 任务清单，可回溯开发过程）

2. 后端核心（重点）—— `backend/web/src/main/java/com/morningstar/dev/`
	- `statemachine/`：18 态状态机内核 —— `State`/`Event`/`StateMachineService`/`StateTransition`/`Trigger` + `AbstractAction`（执行模板：异步、重试、兜底）
	- `statemachine/action/`：8 个真实动作 ——  Start（起沙盒）→ `Sync（git 同步）→ Scan（双通道扫描）→ Fix(AI 修复）→ Verify（两道防线验证）→ Submit（提交 PR)→ Clean（清理）,Restore（失败回滚）; CommonSteps 为共享步骤
	- `statemachine/trigger/ + statetransition/`：状态间自动接力的触发器与转移定义（四要素职责分明）
	- `statemachine/ZombieRunRecovery.java`： 重启恢复 —— 后端重启后把执行中任务逐个接回状态机
	- `scheduled/CronTask.java`：全部定时策略 —— 夜间创建任务、并发分发、卡死强制清理、次日早上取消、PR 状态轮询回写
	- `service/`：项目/任务/管理员业务逻辑（含整轮回退、强制清理）
	- `web/controller/`：REST 入口（项目、任务、管理员三组接口）
	- `util/ProcessUtil.java`：命令执行与凭证脱敏（日志与异常中的 token 统一打码）
	- `util/GiteaUtil.java、util/SonarUtil.java`：Gitea / SonarQube API 客户端

3. 沙盒镜像 —— `deploy/dev-sandbox/`
	Dockerfile + entrypoint.sh + config/：AI 修复的执行环境（JDK + Maven + Node + Claude Code + Sonar Scanner，非 root 运行，镜像内不装 git —— 这是安全模型的关键：能改仓库的凭证绝不进 AI 容器）。

4. 前端 —— `frontend/src/views/dev/`
	- `AboutView.vue`：平台介绍页（对外叙事：机制 → 角色 → 信任 → 行动）
	- `ProjectView.vue`：项目工作台（接入项目、触发任务、跟踪状态）
	- `AdminView.vue`：平台运维页（KPI 五格、并发槽、熔断操作）
	- `components/PipelineStateMachine.vue`：状态机实时可视化组件
	- `frontend/src/router/dev.ts`：路由（含 dev_admin 权限守卫）

5. 数据与配置
	- 数据表: `backend/web/src/main/sql/create.sql` 中 `dev_project` / `dev_run` /
	`dev_action_attempt` / `dev_issue` 四张表;
	- 模块配置: `backend/web/src/main/resources/application-dev.yml` 的
	`morningstar.app.dev.*`（SonarQube/Gitea 双视角地址、双 token、每轮 issue
	上限、重试上限等）;
	- 权限点: `application-perm.yml` 中 `dev_admin` 角色（`dev:run:cancel` /
	`dev:project:schedule`，复用全站权限框架）;
	- 测试: `backend/web/src/test/java/com/morningstar/dev/`（StateMachineService 全链路测试、Util 单测）。

