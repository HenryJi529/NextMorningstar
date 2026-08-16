## 为什么

修复前需把目标仓库代码拉进容器工作区并切到指定分支;失败或取消时需还原到干净状态,避免污染下一次修复或残留半成品。

## 变更内容

- `SyncAction`:通过临时 alpine/git 容器(镜像名写死,不进配置)操作 named volume `morningstar_dev_repo_<projectId>:/workspace/repo`。✅ 8/7 落地,实测通过(首次 clone、增量更新、换分支)。
  - **探测策略**:`processUtil.test("rev-parse --is-inside-work-tree")` 返回 false=首次(dev-plan 决策 20),`test` 内部 catch 转 boolean,不抛异常。
  - **首次克隆**:先清空 `/workspace/repo`(`alpine find -mindepth 1 -delete`,保证 SYNC 重试幂等——上次 clone 半截残骸会阻塞 clone),再 `clone --branch <branchName>`(MVP 不处理子模块,无 `--recursive`)。
  - **增量更新**:`fetch origin <branchName>`(带 `--add-host` + `http.extraHeader`) + `switch -C <branchName> origin/<branchName>`(在任何 HEAD 状态下强制将目标分支指到远端并切过去,不用 `reset --hard`——上一轮 FixAction 可能把 HEAD 停在修复分支) + `git clean -fdx`(清除 untracked/ignored,保证每轮开工工作区绝对干净)。
  - **凭证安全**:clone/fetch URL 一律无凭证形式(host/owner/repo.git),token 通过 `git -c http.extraHeader=Authorization: token <value>` 直接拼入命令参数(Java 字符串拼接,当次生效),**不拼进 remote URL**(token 进 URL 会被 git 原样写入 volume 里 `.git/config`,持久化泄露)。
  - **属主修正**:所有 git 命令统一带 `-c safe.directory=/workspace/repo`(volume 属主 bot,git 容器以 root 运行,不加 git 报 "dubious ownership";clone 除外);末尾 `docker exec --user root <containerName> chown -R bot:bot /workspace/repo`(root 写入的文件 bot 无法修改),if-else 之后一把收。
  - **结果**:成功后 `git rev-parse HEAD` 取 commitSha,与 branchName 一起落 `SyncResult(gitUrl, branchName, commitSha)` 进 `action_attempt.result`(大屏/追溯用);失败统一 catch → FAILED 结果(不裸抛,dev-plan 决策 18)。
  - **新增**:`SyncResult extends ActionResult`(gitUrl/branchName/commitSha),注册到 `@JsonSubTypes`。
- `RestoreAction`:✅ 8/7 落地,实测通过。通过临时 alpine/git 容器执行 7 步还原 + 属主修正:`reset --hard HEAD` → `clean -fdx` → `switch <originalBranch>` → `branch -D fix/<runId>`(先 `rev-parse --verify` 探测) → `reset --hard origin/<originalBranch>` → `rev-parse HEAD`(取证 commitSha 进 `RestoreResult`) → `docker exec --user root chown -R bot:bot`。**纯本地操作,不依赖远端**(无 `--add-host`/`http.extraHeader`/`GiteaProperties`)。失败统一 catch → FAILED(不裸抛)。
- **状态机重构**(dev-plan 决策 24):`FixingStateTransition`、`VerifyingStateTransition` 简化为无条件 `FIX_FAILED`/`VERIFY_FAILED → RESTORING`(移除 `ActionAttemptMapper`/`MaxAttemptsProperties`/`CancelTracker` 依赖)。所有 FIX/VERIFY 重试+取消决策收敛到 `RestoredTrigger`:通过 `latestFix`/`latestVerify` 时间戳判断最新失败来源,按对应重试上限决定续修或放弃;取消检查嵌入重试条件。`RestoredStateTransition` 新增 `FIX_FAILED`/`VERIFY_FAILED → FAILED`。`StartedStateTransition` 补充 `CLEAN → CLEANING`(cancel 路径对齐)。
- **Gitea 双视角地址**(dev-plan 决策 19):`GiteaProperties.origin` → `publicOrigin`(对外地址:后端 API、PR 链接)+ 新增 `containerOrigin`(容器网络内地址:临时 git 容器 clone/fetch/push)。每环境显式配全、无回退。`GiteaUtil` 三处 `origin` 引用随迁 `publicOrigin`。
- 临时 git 容器统一带 `--add-host host.docker.internal:host-gateway`(Docker 20.10+ 全内置,跨平台一致,8/6 实测不报错),clone/fetch URL 使用 `containerOrigin`。

## 能力

### 修改能力
- `fix-runtime`:容器工作区可同步目标代码,并在失败时还原。

## 影响范围

- `SyncAction`/`RestoreAction`:替换 Mock。
- `SyncResult`:新增,`@JsonSubTypes` 补注册。
- `GiteaProperties`:`origin` → `publicOrigin` + 新增 `containerOrigin`。
- `GiteaUtil`:`formatRepoLink`/`collaboratorUrl`/auth headers 三处 `origin` → `publicOrigin`。
- `application-dev.yml` / `application-prod.yml`:gitea 段改名 + 补 `container-origin`。
- `ProcessUtil.test()`:新增探测方法(dev-plan 决策 20)。
