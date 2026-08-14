## 0. 基础设施

- [x] 0.1 路由/ico/BaseView 骨架(8/14)。
- [x] 0.2 `types/dev.ts` 全套类型 + `axios/dev.ts` 8 端点(8/14):契约对齐后端序列化规则(枚举 `name()`、UUID→string、`non_null` 下可空字段标 `?:`),返回类型 `AxiosResponse<R<T>>`,无 data 端点标 `R<void>`;拦截器死代码修复(`code !== SUCCESS` + `msg`)。

## 1. 配置页

- [ ] 1.1 项目 CRUD(仓库/分支/enabled/max_sonar_issues_per_run/max_ai_issues_per_run)。
- [ ] 1.2 手动触发 run、手动停止。

## 2. 大屏

- [ ] 2.1 run 实时状态机流转(SSE/WS 推送)。
- [ ] 2.2 修复成功率、节省人月(20min/bug)。
- [ ] 2.3 高频缺陷 Top(MVP 可后置)。
