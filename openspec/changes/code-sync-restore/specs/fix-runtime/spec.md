# fix-runtime 规格(增量)

## 需求

### 需求:工作区代码同步与还原

#### 场景:首次同步

- **WHEN** 运行进入仓库同步阶段,`processUtil.test("rev-parse --is-inside-work-tree")` 返回 `false`(volume 内不是 git 仓库)
- **THEN** 后端起临时 alpine 容器清空 `/workspace/repo`(`find -mindepth 1 -delete`,保证重试幂等——上次 clone 半截残骸会阻塞 clone)
- **AND** 后端起临时 alpine/git 容器(镜像写死、`--add-host` 统一带),将目标仓库 `clone --branch <branchName>` 到 `/workspace/repo`(MVP 不处理子模块,无 `--recursive`)
- **AND** clone URL 用 `containerOrigin` 拼,无凭证形式(host/owner/repo.git);token 通过 `git -c http.extraHeader=Authorization: token <value>` 当次生效,**不拼进 remote URL**(防 token 持久化写入 volume 里 `.git/config`)
- **AND** 取证 commit sha(`git rev-parse HEAD`)与 branchName、gitUrl 存入 `SyncResult`(落 `action_attempt.result`)

#### 场景:增量同步

- **WHEN** volume 内已是 git 仓库(`processUtil.test("rev-parse --is-inside-work-tree")` 返回 `true`)
- **THEN** 后端起临时 alpine/git 容器执行 `fetch origin <branchName>`(带 `--add-host` + `http.extraHeader` 认证)
- **AND** `switch -C <branchName> origin/<branchName>`(在任何 HEAD 状态下强制将目标分支指到远端最新并切过去,不用 `reset --hard`——上一轮 Fix 可能把 HEAD 停在修复分支上)
- **AND** `git clean -fdx`(清除 untracked/ignored,保证每轮开工工作区绝对干净)
- **AND** 取证 commit sha 与 branchName、gitUrl 存入 `SyncResult`

#### 场景:属主修正

- **WHEN** 同步完成(git 操作以 root 写入文件)
- **THEN** 后端起 `docker exec --user root <containerName> chown -R bot:bot /workspace/repo`(统一在 if-else 与 rev-parse 之后执行,两路径一把收;bot 是 sandbox 容器的运行用户,root 写入的文件 bot 无法修改)
- **AND** 所有 git 命令统一带 `-c safe.directory=/workspace/repo`(volume 属主 bot,git 容器以 root 运行,不加 git 报 "dubious ownership";clone 除外——目录尚不存在)

#### 场景:同步失败

- **WHEN** 任何 git 操作失败(清空/clone/fetch/switch/clean/rev-parse/chown)
- **THEN** 外层 `catch (ProcessExecutionException)` 统一兜底,返回 FAILED `SyncResult`(message 含命令+退出码+stderr;不设 commitSha——失败自然没拿到)
- **AND** **不裸抛**(决策 18:裸抛 = attempt 停 RUNNING + run 卡中间态占并发槽等 60min 超时)
- **AND** 探测用的 `processUtil.test()` 不抛异常——它内部已 catch 转 boolean,不触发外层 catch

#### 场景:失败还原

- **WHEN** 运行失败或取消进入还原阶段
- **THEN** 后端起临时 alpine/git 容器执行 `checkout . && clean -fd`,丢弃所有未提交改动
