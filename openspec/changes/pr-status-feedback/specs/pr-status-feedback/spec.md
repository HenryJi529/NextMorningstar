# pr-status-feedback 规格

## 目的

定时轮询已提交 PR 的合并/拒绝结果，反馈到 issue（ACCEPTED/REJECTED 终态）与 run（prStatus），供前端展示修复的最终人工裁决。

## 需求

### 需求:PR 提交时初始化跟踪

#### 场景:SubmitAction 开 PR 成功

- **WHEN** SubmitAction 成功创建 PR 并回填 `run.prId`
- **THEN** `run.prStatus=OPEN`
- **AND** 该 run 进入轮询跟踪

### 需求:定时轮询 PR 结果

#### 场景:扫描待跟踪的 PR

- **WHEN** 到达轮询间隔（默认每 5min）
- **THEN** 查询所有 `prId` 非空且 `prStatus=OPEN` 的 run
- **AND** 对每个 run 调 Gitea API 取 PR 的 `merged`/`state`

#### 场景:PR 已合并

- **WHEN** PR `merged=true`
- **THEN** 本 run 全部 `dev_issue.status=ACCEPTED`
- **AND** `run.prStatus=MERGED`
- **AND** 停止轮询该 run

#### 场景:PR 被关闭未合并

- **WHEN** PR `state=closed` 且 `merged=false`
- **THEN** 本 run 全部 `dev_issue.status=REJECTED`
- **AND** `run.prStatus=CLOSED`
- **AND** 停止轮询该 run

#### 场景:PR 仍开放

- **WHEN** PR `state=open`
- **THEN** 不修改 issue/run
- **AND** 下个轮询周期继续跟踪
