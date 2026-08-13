## 1. 演示案例

- [ ] 1.1 生成 demo 仓库(Java17 单模块,埋 5–8 个 sonar 真问题)。
- [ ] 1.2 用 NextMorningstar backend 自跑一轮。

## 2. 无人值守

- [x] 2.1 夜间定时(21:00)触发,按 `schedule.max-concurrency`(默认 2)并发执行（`nightlyCreateRuns` + `dispatchPendingRuns`，阶段 0 已实现）。
- [x] 2.2 单 run 失败不影响其他;池满时排队（并发槽位 + PENDING 队列，阶段 0 已实现）。
- [x] 2.3 `cleanup-cron`(06:00)对在跑的 run 触发 cancel（`cancelOvernightRuns`：PENDING 直接标 CANCELED，其余走 `requestCancel`；删修复分支决定不做，见决策 39）。

## 3. 边界

- [ ] 3.1 并发调度、单 run 超时、取消、窗口硬停的稳定性。
