# pr-submit 规格

## 目的

将已验证修复以 PR + AI 诊断报告交付人工评审,AI 无合并权,合并即清理分支。

## 需求

### 需求:提交修复 PR

#### 场景:汇总修复开 PR

- **WHEN** 运行完成所有已 `VERIFIED` 修复
- **THEN** 推修复分支并向源分支开 PR，附统一格式诊断评论（title + 三维 severity + description + suggestion + codeSnippet）

#### 场景:合并后清理

- **WHEN** PR 被合并或关闭
- **THEN** 修复分支自动删除

#### 场景:失败清理

- **WHEN** 运行失败或取消未成功提 PR
- **THEN** 已创建的修复分支被主动删除
