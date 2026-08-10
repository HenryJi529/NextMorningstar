## 1. 数据模型

- [ ] 1.1 `Run` 新增 `prStatus` 字段（枚举 OPEN/MERGED/CLOSED）。
- [ ] 1.2 `dev_run` 表新增 `pr_status` 列（DB 迁移，默认 OPEN）。
- [ ] 1.3 SubmitAction 开 PR 成功回填 `prId` 时同步设 `run.prStatus=OPEN`。

## 2. 定时轮询任务

- [ ] 2.1 新增 cron 配置 `pr-feedback-cron`（建议每 5min）。
- [ ] 2.2 扫描 `prId` 非空 + `prStatus=OPEN` 的 run（不限 `state`，CLEANED 后仍轮询）。
- [ ] 2.3 调 Gitea API `GET /repos/{owner}/{repo}/pulls/{prId}` 取 `merged`/`state`。

## 3. 状态回写

- [ ] 3.1 `merged=true` → 本 run 全部 issue `ACCEPTED`，`run.prStatus=MERGED`。
- [ ] 3.2 `state=closed & merged=false` → 全部 issue `REJECTED`，`run.prStatus=CLOSED`。
- [ ] 3.3 `state=open` → 不变，下周期续轮询。

## 4. 前端

- [ ] 4.1 run 列表/详情展示 PR 状态（OPEN/MERGED/CLOSED）。

## 5. 验证

- [ ] 5.1 提交 PR 后人工 merge → issue 全 ACCEPTED、run.prStatus=MERGED、停止轮询。
- [ ] 5.2 关闭 PR（不 merge）→ issue 全 REJECTED、run.prStatus=CLOSED。
- [ ] 5.3 PR 保持 open → 不变，持续轮询。
- [ ] 5.4 同一 run 重复轮询同状态 → 无副作用（幂等）。
