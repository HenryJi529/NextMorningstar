# Next Morningstar

下一代晨星小站

## 模块划分

- 三国杀
- 开放图床
- 技术博客(支持Metaweblog API)
- 开发者导航
- 爱人相册
- 神兔笑话(存储在Github上)
- 聚合阅读源
- 短链接
- 综合管理后台


## 技术选型

- 工具: 
    - Git
    - Apifox
    - VSCode, DataGrip, IntelliJ IDEA
- 网关: Nginx
- 前端: 
    - Vue3, Vite
    - ECharts
- 后端: 
    - Spring Boot, SpringMVC, WebSocket
    - MySQL, Mybatis, PageHelper, Redis, Spring Data Redis, Caffeine
    - Spring Task
    - EasyExcel
    - Swagger
    - 七牛Kodo


## Morningstar Kill

### 相关资料

- 游戏规则
    - [三国杀游戏规则 - 圍紀實驗室](https://scratchpad.fandom.com/zh/wiki/三国杀游戏规则?variant=zh-hans)
    - [三国杀官方规则集3.0-凌天翼](https://gltjk.com/sanguosha/rules/)
    - [《三国杀标准版》规则解析](https://mp.weixin.qq.com/mp/homepage?__biz=MzIzMTk2ODkxMw==&hid=16&sn=81879d14e8df2b69ee3ba01413bfd1d9)
- 细节资料
    - [三国杀维基 - 三国杀武将列表](https://sanguosha.fandom.com/zh/wiki/三国杀武将列表)
    - [萌娘百科 - 三国杀](https://zh.moegirl.org.cn/三国杀)
- 参考项目
    - [游卡Web三国杀官网](https://web.sanguosha.com/)
    - [Game-as-a-Service/Legends-of-The-Three-Kingdoms](https://github.com/Game-as-a-Service/Legends-of-The-Three-Kingdoms)

### 项目需求

本项目的目标是实现Web三国杀整体框架，包括但不限于游戏流程、武将技能、各模式的牌堆等，并解决游卡官方Web三国杀的痛点问题

- 武将设计不平衡，新武将的强度远远高于旧武将，且技能愈发复杂
- 由于游卡本身还经营着桌游卡牌产品，因而旧版英雄无法重做，而是需要另立版本，使得同一英雄存在过多的版本
- 游戏AI非常低能，无法应对挂机场景，大大降低了游戏体验


## 功能代办

- 参照三国杀官方Web端与Github上现存的其他仿三国杀项目制定需求
- 基础游戏框架: 事件响应系统、对战功能
- 武将选择系统: 禁用、保留、看技能
- 游戏回放
- 用户信息维护
- 友好聊天系统
- 武将胜率统计[用于选将]
