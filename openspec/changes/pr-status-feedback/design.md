## 上下文

SubmitAction 开 PR 后回填 `Run.prId`，但 PR 是否被人工合并/拒绝需要持续跟踪。这是 CLEAN 之后的独立环节——run 已终态（`CLEANED`），但 PR 还在 Gitea 等人评审。gitea-pr-submit 只负责「提交」，结果跟踪是另一职责。AI 无合并权，合并/关闭永远由人工裁决，平台只负责观测并反馈。

## 目标 / 非目标

**目标:** 定时轮询 PR 结果，反馈到 issue/run，前端可见。
**非目标:** 不自动合并/关闭 PR（人工裁决）；不在状态机 `state` 里加状态（PR 结果是观测，不是流程推进）。

## 决策

### 决策 1:run 新增 prStatus 字段，不碰 state 状态机

PR 合并/拒绝是 Gitea 侧的客观观测，不是 pipeline 流程推进。放 `Run.prStatus`（OPEN/MERGED/CLOSED），与现有 `prId` 配套。**不扩展 `state`**（CLEANED 是终态，扩展会污染状态机）、**不扩展 `status`**（那是 run 执行观测，语义不同）。新增字段语义最清晰。

### 决策 2:PR 整体映射，不做 issue 级部分

PR 是整体合并单元（Gitea 不部分合并）。`merged` → 本 run 全部 issue `ACCEPTED`；`closed & !merged` → 全部 `REJECTED`。不做「PR 里某些 commit 合了某些没合」的细粒度——那需要解析 PR 的 commit 列表与 issue 映射，复杂且 Gitea 不原生支持部分合并。

### 决策 3:定时轮询，达终态即停

新增 cron（每 5min，复用 schedule 配置体系 `sync-pr-status-cron`）。只扫 `prId` 非空 + `prStatus=OPEN` 的 run（不限 `state`——CLEANED 后仍轮询）。PR 一旦 `MERGED`/`CLOSED` 即回写并停止轮询该 run。幂等：重复查同状态无副作用。

### 决策 4:兑现 issue 终态 ACCEPTED/REJECTED

这正兑现 `Issue.Status` 预留的 `ACCEPTED`/`REJECTED`——它们之前无写入点（dev-plan 决策 31 删 `FAILED` 时保留这两个作为 SUBMIT 后终态预留），现在由 PR 结果落定。issue 状态流转补全：`SELECTED → FIXED → VERIFIED →（PR merged）ACCEPTED /（PR closed）REJECTED`。

### 决策 5:复用 GiteaProperties backendOrigin

PR 查询从后端发起（不在容器内），用 `GiteaProperties.backendOrigin`（后端视角地址,8/14 订正字段名:原 `publicOrigin`）+ token 调 Gitea API，与现有 Gitea 调用一致。
