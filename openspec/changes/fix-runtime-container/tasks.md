## 1. 镜像

- [ ] 1.1 编写 `dev-fix-runtime.Dockerfile`(JDK17 + maven/git/node/python + claude CLI + sonar-scanner)。
- [ ] 1.2 模型 settings 与 `.mcp.json` 通过运行时挂载注入。

## 2. 容器接入

- [ ] 2.1 `StartAction` 创建并启动容器,挂载 `/workspace/<runId>`。
- [ ] 2.2 `CleanAction` 删除容器。
- [ ] 2.3 `dev_run.container_id` 记录映射。

## 3. Spike 验证

- [ ] 3.1 容器内 `claude -p "ping"` 走 deepseek 通。
- [ ] 3.2 容器内 sonarqube MCP 可查 issue(sonar 地址用 `host.docker.internal`)。
