# fix-runtime 规格

## 目的

为每次漏洞修复运行提供隔离、可驱动、可清理的标准化容器环境。

## 需求

### 需求:运行隔离容器

#### 场景:启动运行容器

- **WHEN** 运行进入容器启动阶段
- **THEN** 确保项目专属 named volume 存在(`morningstar_dev_repo_<projectId>`)
- **AND** 创建并启动一个独立容器(命名 `morningstar_dev_sandbox_<runId>`,由 runId 确定性推导,无需记录容器 ID),挂载 `morningstar_dev_repo_<projectId>:/workspace/repo`,注入所需 env(`MODEL_API_KEY`/`SONARQUBE_TOKEN`)与 `host.docker.internal` 解析

#### 场景:清理容器

- **WHEN** 运行结束(成功/失败/取消)
- **THEN** 按确定性命名(`morningstar_dev_sandbox_<runId>`)删除该运行对应的容器;容器本就不存在("No such container")视为清理成功
- **AND** 保留 `morningstar_dev_repo_<projectId>` volume(volume 持久化为项目级代码缓存,仅项目删除时清理)
