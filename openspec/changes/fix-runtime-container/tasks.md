## 1. 镜像

- [x] 1.1 编写 `dev-sandbox/Dockerfile`(JDK17 + maven/node/python + claude CLI + sonar-scanner,**不装 git**——见决策 4)。
- [ ] 1.2 配置(settings.json/mcp.json 占位符模板)打进镜像 + `entrypoint` 运行时用 env(`DEEPSEEK_API_KEY`/`SONARQUBE_TOKEN`)替换占位符。

## 2. 容器接入

- [ ] 2.1 `StartAction` 创建并启动容器,挂载 `/workspace/<runId>`。
- [ ] 2.2 `CleanAction` 删除容器。
- [ ] 2.3 `dev_run.container_id` 记录映射。

## 3. Spike 验证

- [x] 3.1 容器内 `claude -p "ping"` 走 deepseek 通。
- [ ] 3.2 容器内 sonarqube MCP 可查 issue(sonar 地址用 `host.docker.internal`)。
