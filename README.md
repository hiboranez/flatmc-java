<div align="center">

# FlatMC Java Edition

### An archived Java implementation of the FlatMC 2D sandbox game

[![Java](https://img.shields.io/badge/Java-Desktop-orange?logo=openjdk)](https://www.java.com/)
[![Version](https://img.shields.io/badge/Version-0.4.0-blue)](#version-history)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Archived-lightgrey)](#project-status)

**English** | [简体中文](README.zh-CN.md)

</div>

> [!IMPORTANT]
> FlatMC Java Alpha is no longer developed or maintained. FlatMC has been succeeded by **FlatCraft**. This repository is preserved as a historical record and is not guaranteed to be compatible with FlatCraft.

## Overview

FlatMC Java Alpha is the original Java implementation of FlatMC. This repository now preserves both the desktop game client and its dedicated multiplayer server. The client supports singleplayer worlds, survival and creative gameplay, crafting, entities, commands, and multiplayer. The server provides authoritative world simulation, account commands, administration, and player synchronization.

## Features

- Two-dimensional block world with terrain generation and saves.
- Survival and creative modes, inventory, tools, crafting, and zombies.
- Time, lighting, gravity, damage, regeneration, and item drops.
- Multiplayer chat, accounts, teleportation, homes, spawn protection, and moderation commands.
- Dedicated Java TCP server on port `25565`.

## Repository Structure

```text
flatmc-java/
├── src/                 # Desktop client source
├── data/                # Client data and resources
├── server/
│   ├── src/             # Dedicated server source
│   └── data/            # Server data and resources
├── CHANGELOG.zh-CN.md
├── LICENSE
├── README.md
└── README.zh-CN.md
```

## Version History

| Version | Date | Summary |
| --- | --- | --- |
| v0.1.0 | 2023-11-07 | Initial movement, block interaction, and game modes |
| v0.2.0 | 2023-11-10 | Death, item drops, flight, and a larger world |
| v0.2.1 | 2023-11-11 | Random terrain and rendering improvements |
| v0.2.2 | 2023-11-12 | Tools, durability, trees, and saving improvements |
| v0.2.3 | 2023-11-17 | Time, lighting, torches, and grass behavior |
| v0.3.0 | 2023-11-21 | Inventory, hostile mobs, combat cooldown, and UI |
| v0.3.1 | 2023-11-24 | Ores, tools, crafting, and workbenches |
| v0.3.2 | 2023-11-25 | Inventory shortcuts, Chinese, and physics adjustments |
| v0.3.3 | Unknown | Historical release; detailed records have not been recovered |
| v0.4.0 | Unknown | Multiplayer and dedicated server |

The first normalized GitHub release is `v0.1.0`. Later historical releases will be published only after their records have been reviewed.

## Known Limitations

- The repository is a preservation snapshot rather than a reconstructed source tree for every historical release.
- No original, verifiable `v0.1.0` binary is currently included.
- Historical save and network formats are not compatible with FlatCraft.
- Some bundled assets may have licensing terms separate from the source code.

## Project Status

**Archived / Superseded**

Development has ended. FlatCraft is the successor to FlatMC. No maintenance or feature releases are planned for this repository.

## License

Except where otherwise noted, this project is licensed under the [MIT License](LICENSE). Bundled assets and third-party components may be subject to separate terms.
