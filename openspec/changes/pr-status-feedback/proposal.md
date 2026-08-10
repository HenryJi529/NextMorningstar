## 为什么

PR 提交后是否被人工合并/拒绝，是修复价值的最终裁决，需反馈到平台：issue 落定 `ACCEPTED`/`REJECTED` 终态、run 记录 PR 结果、前端展示。这是 SUBMIT 之后独立的「PR 生命周期跟踪」环节——gitea-pr-submit 只管提交，不跟踪结果；run 提交后到 `CLEANED` 终态，但 PR 还在 Gitea 等人评审。

## 变更内容

- 新增定时任务（建议每 5min）：扫描 `prId` 非空且 `prStatus=OPEN` 的 run。
- 调 Gitea API `GET /repos/{owner}/{repo}/pulls/{prId}` 取 `merged`/`state`。
- 状态映射：
  - `merged=true` → 本 run 全部 issue.Status=`ACCEPTED`，run.prStatus=`MERGED`
  - `state=closed & merged=false` → 全部 issue.Status=`REJECTED`，run.prStatus=`CLOSED`
  - `state=open` → 不变，继续轮询
- PR 达终态（MERGED/CLOSED）后停止轮询该 run。
- 前端展示 run 的 PR 合并/拒绝状态。

## 能力

### 新增能力
- `pr-status-feedback`:定时轮询已提交 PR 的合并/拒绝结果，反馈到 issue 与 run。

## 影响范围

- `Run`:新增 `prStatus` 字段（OPEN/MERGED/CLOSED），与 `prId` 配套。
- `dev_run` 表:新增 `pr_status` 列。
- SubmitAction:开 PR 成功后初始化 `prStatus=OPEN`。
- 新增定时任务（cron）+ Gitea PR 查询逻辑。
- 前端:run 列表/详情展示 PR 状态。
