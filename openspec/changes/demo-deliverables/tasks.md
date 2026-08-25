## 1. 演示账号与项目

- [x] 1.1 创建演示账号（插入 `sys_user` + 绑定角色），可登录查看仪表盘与触发 run。(8/16:henry/sherry/SpiderMan 三账号,按 SQL 子查询划归 5 项目;演示账号 `dev:run:cancel`/`dev:project:schedule` 权限点已确认,可进平台运维页)
- [x] 1.2 部署 demo 项目（公司项目 + NextMorningstar）。(8/14 配置 5 真实仓库接入;8/16 经 `StateMachineServiceTest` 全链路造数 6 run 含 2 失败,PR 合并/关闭/开放三态已经 cron 回写验证——ACCEPTED/REJECTED 落库,KPI 有数;NextMorningstar 自跑一轮归 nightly-unattended-run 1.2)

## 2. 交付素材（对应 dev-plan 阶段 10，8/20–8/30 冲刺，deadline 8/30）

- [x] 2.1 梳理手头现有材料（demo 数据 / 架构报告 `dev-report` / 演示账号）。
- [x] 2.2 用 drawio 完善架构图（系统拓扑）。(8/22 定稿 `docs/dev-项目架构图.drawio`：放射状布局 + 四色图例 自研组件/三方工具/外部服务/数据存储)
- [x] 2.3 优化前端 about 页面（`/dev/about` 平台介绍）。改动属 demo-deliverables 交付环节，不改已归档的 `pipeline-ui` 主 spec，在 demo-deliverables 内闭环记录。(8/22 完成：事实纠错——出站白名单降级为后置规划、第三防线实为凭证脱敏、admin token 使用范围、bot 权限实为协作者按仓库收放、代码卷按项目保留；文案打磨——slogan「晚上 AI 修代码，早上你顺手审 PR」、身份三卡、凭证术语统一；板块重排为 机制→角色→信任→行动，理由见 dev-report 1.4；pipeline-ui 主 spec 已破例同步更正)
- [ ] 2.4 编写汇报材料（元叙事 / 安全模型 / KPI 叙事链 / 未来规划；未来规划页素材见 `docs/dev-report.md` 第十三节）。
- [ ] 2.5 编写 PPT。
- [x] 2.6 视频录制和剪辑。(8/25 成片 2'30"，五节结构：平台总览/项目接入/手动触发/夜间调度/PR 合并——手动触发先讲机制、夜间调度再讲值守，PR 节尾部含平台回写与 KPI 落点；纯画面 + 字幕标题，不配旁白)
