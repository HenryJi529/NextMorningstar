# dev-vuln-pipeline 规格(增量)

## 需求

### 需求:资源池并发执行

#### 场景:夜间并发跑多仓库

- **WHEN** 夜间 21:00 触发多个 enabled 项目
- **THEN** 按 `runtime.concurrency`(默认 2)并发执行,单 run 失败不影响其他
- **AND** 单 run 占一个容器,池满时其余排队

### 需求:夜间窗口截止

#### 场景:06:00 硬停

- **WHEN** 到达 `schedule.cleanup-cron`(06:00)
- **THEN** 对在跑的 run 触发 cancel(走取消流程,删修复分支)
- **AND** 未启动的 run 标记 `SKIPPED`,明夜重新触发
