## 为什么

需要面向项目经理的配置入口(选仓库/分支、手动触发/停止)与汇报用大屏(状态流转、修复成功率、节省人月)。

## 变更内容

- 复用 `frontend/` 与 system 模块认证。
- 配置页:项目 CRUD(仓库/分支/enabled/max_sonar_issues_per_run/max_ai_issues_per_run)、手动触发 run、手动停止。
- 大屏:run 实时状态机流转(SSE/WS)、修复成功率、节省人月(每 bug 20min)、高频缺陷 Top。

## 能力

### 新增能力
- `pipeline-ui`:对流水线的可视化配置与运行观测。

## 影响范围

- `frontend/`:新增配置页与大屏。
