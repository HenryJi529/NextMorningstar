## 1. 演示账号与项目

- [x] 1.1 创建演示账号（插入 `sys_user` + 绑定角色），可登录查看仪表盘与触发 run。(8/16:henry/sherry/SpiderMan 三账号,按 SQL 子查询划归 5 项目;演示账号 `dev:run:cancel`/`dev:project:schedule` 权限点已确认,可进平台运维页)
- [x] 1.2 部署 demo 项目（公司项目 + NextMorningstar）。(8/14 配置 5 真实仓库接入;8/16 经 `StateMachineServiceTest` 全链路造数 6 run 含 2 失败,PR 合并/关闭/开放三态已经 cron 回写验证——ACCEPTED/REJECTED 落库,KPI 有数;NextMorningstar 自跑一轮归 nightly-unattended-run 1.2)

## 2. 交付素材（对应 dev-plan 阶段 10，8/20–8/30 冲刺，deadline 8/30）

- [x] 2.1 梳理手头现有材料（demo 数据 / 架构报告 `dev-report` / 演示账号）。
- [x] 2.2 用 drawio 完善架构图（系统拓扑）。(8/22 定稿 `docs/dev-项目架构图.drawio`：放射状布局 + 四色图例 自研组件/三方工具/外部服务/数据存储)
- [ ] 2.3 优化前端 about 页面（`/dev/about` 平台介绍）。改动属 demo-deliverables 交付环节，不改已归档的 `pipeline-ui` 主 spec，在 demo-deliverables 内闭环记录。
- [ ] 2.4 编写汇报材料（元叙事 / 安全模型 / KPI 叙事链 / 未来规划；未来规划页素材见 `docs/dev-report.md` 第十三节）。
- [ ] 2.5 编写 PPT。
- [ ] 2.6 视频录制和剪辑（分镜 9 镜头已定，旁白逐字稿待写）。
