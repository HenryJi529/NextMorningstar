# pr-status-feedback 规格

## 目的

定时轮询已提交 PR 的合并/拒绝结果，反馈到 issue（ACCEPTED/REJECTED 终态）与 run（prStatus），供前端展示修复的最终人工裁决。

## 需求

### 需求：PR 提交时初始化跟踪

#### 场景：SubmitAction 开 PR 成功

- **WHEN** SubmitAction 成功创建 PR 并回填 `run.prId`
- **THEN** `run.prStatus=OPEN`
- **AND** 该 run 进入轮询跟踪

### 需求：定时轮询 PR 结果

#### 场景：扫描待跟踪的 PR

- **WHEN** 到达轮询间隔（`sync-pr-status-cron`，初始默认每 5min，8/17 调为 30s——演示场景缩短人工裁决的页面滞后,`15/30` 与 dispatch 错开半拍）
- **THEN** 查询所有 `prId` 非空且 `prStatus=OPEN` 的 run（不限 `state`，CLEANED 后仍轮询）
- **AND** 对每个 run 调 Gitea API `GET /repos/{owner}/{repo}/pulls/{prId}` 取 `merged`/`state`（单 run 同步失败 try-catch 不中断批量；PR 404/项目已删幂等跳过）

#### 场景：PR 已合并

- **WHEN** PR `merged=true`
- **THEN** 本 run 全部 `VERIFIED` 的 `dev_issue.status=ACCEPTED`（仅 VERIFIED，不动其他状态）
- **AND** `run.prStatus=MERGED`
- **AND** 停止轮询该 run

#### 场景：PR 被关闭未合并

- **WHEN** PR `state=closed` 且 `merged=false`
- **THEN** 本 run 全部 `VERIFIED` 的 `dev_issue.status=REJECTED`
- **AND** `run.prStatus=CLOSED`
- **AND** 停止轮询该 run

#### 场景：PR 仍开放

- **WHEN** PR `state=open`
- **THEN** 不修改 issue/run
- **AND** 下个轮询周期继续跟踪
