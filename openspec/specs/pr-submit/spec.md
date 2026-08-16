# pr-submit 规格

## 目的

将已验证修复以 PR + AI 诊断报告（PR body）交付人工评审，AI 无合并权。

## 需求

### 需求：提交修复 PR

#### 场景：汇总修复开 PR

- **WHEN** 运行完成所有已 `VERIFIED` 修复
- **THEN** 后端起临时 alpine/git 容器推修复分支（`fix/<runId>`），并调 Gitea API 向源分支开 PR，body 附统一格式诊断报告（Sonar/AI 分分支：title + 三维 severity + 代码片段链接（跳转源码对应行）+ 修改记录链接（跳转 commit），AI 额外 type/description）
- **AND** 回写 `Run.prId` + `prStatus=OPEN`

#### 场景：合并后清理（决定不做）

- **WHEN** PR 被合并或关闭
- **THEN** 修复分支自动删除 —— **决定不做**（8/13）：删除分支危险且越界，平台只观测不删除；残留分支由仓库所有者在 Gitea 手动处理

#### 场景：失败清理（决定不做）

- **WHEN** 运行失败或取消未成功提 PR
- **THEN** 已创建的修复分支被主动删除 —— **决定不做**（8/13）：删除分支危险且越界，平台只观测不删除；残留分支名带 runId 唯一不冲突，由人工在 Gitea 手动清理
