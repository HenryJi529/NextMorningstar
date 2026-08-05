## 1. 代码同步

- [ ] 1.1 `SyncAction` 通过临时 alpine/git 容器操作 named volume `ws-<projectId>:/workspace`:首次 `clone --recursive`,后续 `fetch + reset --hard`(决策 10,增量更新)。
- [ ] 1.2 切到 `dev_project.branchName`;凭证以 `-e` 注入临时容器,用完即毁。

## 2. 代码还原

- [ ] 2.1 `RestoreAction` 通过临时 alpine/git 容器执行 `checkout . && clean -fd` 还原工作区。
- [ ] 2.2 验证还原后工作区与初始克隆一致。
