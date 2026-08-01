## 1. 代码同步

- [ ] 1.1 `SyncAction` 用**后端命令行 git** `clone --recursive` 到共享卷 `~/dev-workspaces/<runId>/repo`(含 submodule)。
- [ ] 1.2 切到 `dev_project.branchName`;凭证经 credential helper 注入,不进容器。

## 2. 代码还原

- [ ] 2.1 `RestoreAction` 后端命令行 git `checkout . && clean -fd` 还原工作区。
- [ ] 2.2 验证还原后工作区与初始克隆一致。
