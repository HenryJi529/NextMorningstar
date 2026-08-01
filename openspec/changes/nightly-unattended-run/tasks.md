## 1. 演示案例

- [ ] 1.1 生成 demo 仓库(Java17 单模块,埋 5–8 个 sonar 真问题)。
- [ ] 1.2 用 NextMorningstar backend 自跑一轮。

## 2. 无人值守

- [ ] 2.1 夜间定时(21:00)触发,按 `runtime.concurrency`(默认 2)并发执行。
- [ ] 2.2 单 run 失败不影响其他;池满时排队。
- [ ] 2.3 `window-end`(07:00)对在跑的 run 触发 cancel(走取消流程 + 删 ai-fix 分支),未启动的标 `SKIPPED`。

## 3. 边界

- [ ] 3.1 并发调度、单 run 超时、取消、窗口硬停的稳定性。
