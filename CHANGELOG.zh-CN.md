# FlatMC Java Edition Alpha 版本记录

> [!IMPORTANT]
> 本仓库缺少各历史版本对应的完整源码提交和可验证构建。所有规范化版本标签均使用 FlatMC Java Edition 最终 `v0.4.0` 归档源码快照；GitHub 自动生成的源码压缩包不是早期 Release 的原始源码。

## Alpha v0.1.0 · 2023-11-07

FlatMC Java Edition 的首个有明确记录的开发版本。

### 游戏内容

- 加入移动、跳跃、双击奔跑、方块破坏与放置。
- 加入掉落物拾取和鼠标滚轮快捷栏选择。
- 加入生存与创造模式；创造模式不消耗物品并免疫伤害。
- 加入摔落伤害和脱战生命恢复。

### 已知限制

- 尚无完整死亡与重生流程，世界仍然有界。
- 石头破坏后尚不掉落方块。

[English release notes](https://github.com/hiboranez/flatmc-java/releases/tag/v0.1.0)

## Alpha v0.2.0 · 2023-11-10

### 游戏内容

- 加入 F11 全屏、平滑镜头跟随并扩大地图。
- 加入基岩、死亡、重生、死亡掉落和掉落物吸引。
- 加入 Q 键丢弃物品以及创造模式飞行。
- 创造模式取消方块交互距离和邻接限制。

[English release notes](https://github.com/hiboranez/flatmc-java/releases/tag/v0.2.0)

## Alpha v0.2.1 · 2023-11-11

### 游戏内容

- 加入随机地形并再次扩大地图。
- 超出地图边界时玩家死亡。
- 不再渲染视野外方块。

### 修复

- 修复玩家穿模问题。

[English release notes](https://github.com/hiboranez/flatmc-java/releases/tag/v0.2.1)

## Alpha v0.2.2 · 2023-11-12

### 游戏内容

- 加入工具、木制品和随机树林。
- 加入工具耐久，并让对应工具加快方块破坏速度。
- 背包已满时不再继续拾取物品。
- 加入退出自动保存并优化玩家动作。

[English release notes](https://github.com/hiboranez/flatmc-java/releases/tag/v0.2.2)

## Alpha v0.2.3 · 2023-11-17

### 游戏内容

- 加入时间系统、光照渲染和火把。
- 工具耐久改为进度条显示，耗尽后工具损坏。
- 加入草方块蔓延与消亡。

[English release notes](https://github.com/hiboranez/flatmc-java/releases/tag/v0.2.3)

## Alpha v0.3.0 · 2023-11-21

### 游戏内容

- 加入 E 键背包和 Esc 设置页面。
- 加入夜间弱光刷怪、攻击冷却和新版死亡页面。
- 和平难度会清除怪物。
- 加入完整游戏 UI 的早期实现。

[English release notes](https://github.com/hiboranez/flatmc-java/releases/tag/v0.3.0)

## Alpha v0.3.1 · 2023-11-24

### 游戏内容

- 加入更多工具、矿物和随机矿脉。
- 加入背包合成、工作台界面和常用配方。
- 扩充创造模式物品栏。

[English release notes](https://github.com/hiboranez/flatmc-java/releases/tag/v0.3.1)

## Alpha v0.3.2 · 2023-11-25

### 游戏内容

- 加入 Shift + 左键快速合成和 Ctrl + 左键快速整理。
- 加入拖出丢弃、选中物品提示和悬浮名称。
- 加入中文界面。
- 调整世界重力和摩擦力。

[English release notes](https://github.com/hiboranez/flatmc-java/releases/tag/v0.3.2)

## Alpha v0.3.3 · 日期未确认

### 归档说明

- 公开版本表确认该版本存在，但完整更新日志、原始源码和构建尚未恢复。
- 本 Release 仅建立历史索引，不根据前后版本差异推测功能。

[English release notes](https://github.com/hiboranez/flatmc-java/releases/tag/v0.3.3)

## Alpha v0.4.0 · 日期未确认

FlatMC Java Edition 的最终开发阶段。

### 多人游戏

- 加入 Java TCP 多人客户端和默认端口为 `25565` 的专用服务器。
- 加入玩家注册、登录、聊天、私聊、传送请求、家和出生点保护。
- 加入管理员、踢出、封禁等服务器命令。
- 加入玩家、方块、实体、世界状态和声音同步。

### 游戏内容

- 保留单人世界、生存与创造模式、物品栏、合成、僵尸和完整指令体系。
- 修复已知问题并提升运行流畅度。

### 仓库归档

- Java 客户端与专用服务器源码合并到同一仓库。
- 原服务端仓库保留迁移说明并归档。

[English release notes](https://github.com/hiboranez/flatmc-java/releases/tag/v0.4.0)

## 项目状态

**已归档 / 不再维护。** FlatMC 已由 FlatCraft 取代。以上 Release 用于保存版本历史，不保证与 FlatCraft 兼容。

