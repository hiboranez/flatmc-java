<div align="center">

# FlatMC Java Edition

### FlatMC 2D 沙盒游戏的 Java 历史实现

[![Java](https://img.shields.io/badge/Java-Desktop-orange?logo=openjdk)](https://www.java.com/)
[![Version](https://img.shields.io/badge/Version-0.4.0-blue)](#版本记录)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Archived-lightgrey)](#项目状态)

[English](README.md) | **简体中文**

</div>

> [!IMPORTANT]
> FlatMC Java Alpha 已停止开发和维护，FlatMC 已由 **FlatCraft** 取代。本仓库仅作为历史记录保留，不保证与 FlatCraft 兼容。

## 项目简介

FlatMC Java Alpha 是 FlatMC 最早的 Java 实现。本仓库现同时保存桌面客户端和多人专用服务器。客户端包含单人世界、生存与创造模式、合成、实体、命令和多人游戏；服务器负责权威世界模拟、账号命令、管理功能和玩家同步。

## 主要功能

- 2D 方块世界、地形生成和世界存档。
- 生存与创造模式、物品栏、工具、合成和僵尸。
- 时间、光照、重力、伤害、回血和物品掉落。
- 多人聊天、账号、传送、家、出生点保护和管理命令。
- 默认使用 `25565` 端口的 Java TCP 专用服务器。

## 仓库结构

```text
flatmc-java/
├── src/                 # 桌面客户端源码
├── data/                # 客户端数据和资源
├── server/
│   ├── src/             # 专用服务器源码
│   └── data/            # 服务器数据和资源
├── CHANGELOG.zh-CN.md
├── LICENSE
├── README.md
└── README.zh-CN.md
```

## 版本记录

| 版本 | 日期 | 主要内容 |
| --- | --- | --- |
| 0.1.0 | 2023-11-07 | 基础移动、方块交互和游戏模式 |
| 0.2.0 | 2023-11-10 | 死亡、掉落、飞行和地图扩展 |
| 0.2.1 | 2023-11-11 | 随机地形和渲染优化 |
| 0.2.2 | 2023-11-12 | 工具、耐久、树木和保存优化 |
| 0.2.3 | 2023-11-17 | 时间、光照、火把和草方块变化 |
| 0.3.0 | 2023-11-21 | 背包、怪物、攻击冷却和 UI |
| 0.3.1 | 2023-11-24 | 矿物、工具、合成和工作台 |
| 0.3.2 | 2023-11-25 | 背包快捷操作、中文和物理调整 |
| 0.3.3 | 未确认 | 已知存在，详细历史记录尚未恢复 |
| 0.4.0 | 未确认 | 多人联机与专用服务器 |

首个规范化 GitHub Release 为 `0.1.0`。后续历史版本将在记录确认后再发布。

## 已知限制

- 当前仓库是历史保存快照，不包含每个旧版本对应的完整源码提交。
- 当前没有附带可验证的原始 `0.1.0` 安装包。
- 历史存档和网络协议不兼容 FlatCraft。
- 部分资源和第三方组件可能不适用源码许可证。

## 项目状态

**已归档 / 已被取代**

本项目已停止开发，FlatCraft 是 FlatMC 的后继项目。本仓库不再计划维护或增加功能。

## 许可证

除另有说明外，本项目采用 [MIT License](LICENSE)。仓库内附带的素材和第三方组件可能适用单独条款。
