## 1. 配置拆分

- [x] 1.1 `GiteaProperties`:`origin` 改名 `publicOrigin`,新增 `containerOrigin`;`GiteaUtil` 三处引用迁到 `publicOrigin`。
- [x] 1.2 `application-dev.yml` / `application-prod.yml`:gitea 段同步改名 + 补 `container-origin`。

## 2. SyncResult

- [x] 2.1 新建 `SyncResult extends ActionResult`(字段: `gitUrl` / `branchName` / `commitSha`),`@JsonSubTypes` 注册 `Action.Type.SYNC_NAME`。

## 3. SyncAction

- [x] 3.1 构造器注入 + super 传 Event + getType()。
- [x] 3.2 探测:`processUtil.test("rev-parse --is-inside-work-tree")`,返回 false=首次(决策 20)。
- [x] 3.3 首次路径:清空 `/workspace/repo`(`alpine find -mindepth 1 -delete`,保重试幂等)→ `clone --branch <branchName>`(MVP 无子模块,无 `--recursive`)。clone 带 `--add-host` + `http.extraHeader`。
- [x] 3.4 增量路径:`fetch origin <branchName>`(带 `--add-host` + `http.extraHeader`)+ `switch -C <branchName> origin/<branchName>` + `git clean -fdx`。所有 git 命令统一加 `-c safe.directory=/workspace/repo`(clone 除外)。
- [x] 3.5 属主修正:`docker exec --user root <containerName> chown -R bot:bot /workspace/repo`(if-else 之后一把收)。
- [x] 3.6 结果:`git rev-parse HEAD` 取 commitSha,进 `SyncResult(gitUrl/branchName/commitSha)`;任何 git 操作失败外层 catch → FAILED 结果(不裸抛)。
- [x] 3.7 ✅ 验收:首次 clone、增量更新、换分支全链路通过。

## 4. 代码还原

- [ ] 4.1 `RestoreAction` 通过临时 alpine/git 容器执行 `checkout . && clean -fd` 还原工作区。
- [ ] 4.2 验证还原后工作区与初始克隆一致。
