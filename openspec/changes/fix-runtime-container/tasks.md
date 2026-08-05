## 1. 镜像

- [x] 1.1 编写 `dev-sandbox/Dockerfile`(JDK17 + maven/node/python + claude CLI + sonar-scanner,**不装 git**——见决策 4)。
- [x] 1.2 配置(settings.json/mcp.json 占位符模板)打进镜像 + `entrypoint` 运行时用 env(`DEEPSEEK_API_KEY`/`SONARQUBE_TOKEN`)替换占位符。

## 2. 容器接入

- [ ] 2.1 `StartAction`:确保 volume 存在(`docker volume create ws-<projectId>`,决策 10),启动容器挂载 `ws-<projectId>:/workspace`(决策 9),注入 `DEEPSEEK_API_KEY`/`SONARQUBE_TOKEN` env + `--add-host=host.docker.internal:host-gateway`,回写 `container_id`。
- [ ] 2.2 `CleanAction`:删除容器(`docker rm -f`),**不删 volume**(volume 持久化为项目缓存,决策 10)。
- [ ] 2.3 `SyncAction`(补充):首次 clone、后续 `git fetch + reset --hard` 增量更新(决策 10)。

## 3. Spike 验证

- [x] 3.1 容器内 `claude -p "ping"` 走 deepseek 通。
- [x] 3.2 容器内 sonarqube MCP 可查 issue(sonar 地址用 `host.docker.internal`)。

## 4. claude 容器化运行坑(待解决,影响 FixAction `docker exec claude`)

- [x] 4.1 **初始化卡住**:确认 `claude -p` 不会触发 onboarding;FixAction 始终用 `-p`,无需额外跳过配置。
- [x] 4.2 **root 跳过权限被拒**:容器以非 root 用户 `bot` 运行,`--dangerously-skip-permissions` 不再报 root 错误(已 build 验证通过)。
