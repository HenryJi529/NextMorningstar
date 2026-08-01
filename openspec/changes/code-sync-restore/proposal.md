## 为什么

修复前需把目标仓库代码拉进容器工作区并切到指定分支;失败或取消时需还原到干净状态,避免污染下一次修复或残留半成品。

## 变更内容

- `SyncAction`:**后端命令行 git** `clone --recursive`(含 submodule)到共享卷工作区,并切到 `dev_project.branchName`(凭证在后端,不进容器)。
- `RestoreAction`:**后端命令行 git** `checkout . && clean -fd`,还原到克隆时状态。
- 工作区目录约定:宿主机 `~/dev-workspaces/<runId>/repo`(共享卷挂载到容器)。

## 能力

### 修改能力
- `fix-runtime`:容器工作区可同步目标代码,并在失败时还原。

## 影响范围

- `SyncAction`/`RestoreAction`:替换 Mock。
