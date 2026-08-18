## 1. 演示案例

- [x] 1.1 ~~生成 demo 仓库(Java17 单模块,埋 5–8 个 sonar 真问题)~~ → 8/14 改用 5 个真实项目仓库:纯前端、前后端分离架构的前端、后端、前后端一体、NextMorningstar 自身(真实仓库已验证有可修 issue,替代埋点方案)。
- [x] 1.2 用 NextMorningstar backend 自跑一轮。(8/18 实测通过,平台修复自身叙事闭环)

## 2. 无人值守

- [x] 2.1 夜间定时(21:00)触发,按 `schedule.max-concurrency`(默认 2)并发执行（`nightlyCreateRuns` + `dispatchPendingRuns`，阶段 0 已实现）。(8/18 云服务器夜间定时真实触发实测:5 仓库,并发 4 先跑完,第 5 个排队后自动补位)
- [x] 2.2 单 run 失败不影响其他;池满时排队（并发槽位 + PENDING 队列，阶段 0 已实现）。(8/18 同上实测覆盖)
- [x] 2.3 `cleanup-cron`(06:00)对在跑的 run 触发 cancel（`cancelOvernightRuns`：PENDING 直接 `deleteById`(从未启动不留记录,8/14 改)，其余走 `requestCancel`；删修复分支决定不做，见决策 39）。

## 3. 边界

- [ ] 3.1 并发调度、单 run 超时、取消、窗口硬停的稳定性。（8/14 已加固:超时查询排除 PENDING——排队不算超时;`triggerRun` 单飞守卫;`hasActiveRun` 口径三处统一)
