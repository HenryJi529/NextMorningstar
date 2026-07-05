## 新增需求

### 需求：业务 API 失败按错误级别记录

前端 API 响应拦截器必须在浏览器控制台中用错误级别报告业务级 API 失败。

#### 场景：API 响应包含非成功业务码

- **WHEN** 收到 `response.data.code <= 0` 的 API 响应
- **THEN** 前端使用 `console.error` 记录响应 code 和 message
- **AND** token 无效或过期的既有处理逻辑保持不变

#### 场景：API 响应包含成功业务码

- **WHEN** 收到 `response.data.code > 0` 的 API 响应
- **THEN** 前端不会为该业务码输出错误日志
