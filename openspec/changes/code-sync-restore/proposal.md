## 为什么

修复前需把目标仓库代码拉进容器工作区并切到指定分支;失败或取消时需还原到干净状态,避免污染下一次修复或残留半成品。

## 变更内容

- `SyncAction`:通过临时 alpine/git 容器操作 named volume `ws-<projectId>:/workspace`,首次 `clone --recursive` / 后续 `fetch + reset --hard`(决策 10),并切到 `dev_project.branchName`(凭证 `-e` 注入,用完即毁)。
- `RestoreAction`:通过临时 alpine/git 容器执行 `checkout . && clean -fd`,还原工作区。
- 工作区:named volume `ws-<projectId>:/workspace`,容器与临时 git 容器共享。

## 能力

### 修改能力
- `fix-runtime`:容器工作区可同步目标代码,并在失败时还原。

## 影响范围

- `SyncAction`/`RestoreAction`:替换 Mock。
