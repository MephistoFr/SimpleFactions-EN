# SimpleFactions

[![Spigot 1.21.x](https://img.shields.io/badge/Paper-1.21.x-blue)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net)

Complete **Factions** plugin for **Paper 1.21.x** servers (**1.21 to 1.21.11**), fully standalone (no external dependencies, no third-party economy plugin required). Local **JSON** data storage.

## Features

- **Faction management**: create, disband, invite, accept, kick and leave.
- **Rank system**: Leader, Mod (Officer), Member, Recruit, with promotion/demotion (`/f promote`, `/f demote`).
- **Chat**: in-game faction chat and public/faction chat toggle (`/f c`).
- **Power**: max 10 per player, lose 2 on death, gain 1 per kill, regenerates over time (even offline).
- **Claims**: claim/unclaim chunks based on the faction's total power, full territory protection (break, place, chests, interactions).
- **Overclaim**: steal an enemy chunk when the enemy faction's total power drops below its number of claims.
- **HQ**: `/f sethome` and `/f home`.
- **Interactive guide**: `/f gui` (in-game GUI explaining the whole plugin).
- **Complete commands** with tab-completion and permission handling.

## Requirements

- Server running **Paper 1.21.x** (1.21 to 1.21.11) or Spigot/another Paper-API-compatible implementation
- **Java 21**

> Compatible with all Paper API versions from **1.21** to **1.21.11** (tested against each version).

## Installation

1. Place `SimpleFactions-1.0.0.jar` in the server's `plugins/` folder.
2. Restart the server.
3. Data is saved in `plugins/SimpleFactions/data/`:
   - `factions.json`: factions, members, claims, HQ.
   - `players.json`: player power.
4. Everything is configured in `plugins/SimpleFactions/config.yml` (then `/f reload`).

## Commands

| Command | Description | Required rank |
|---|---|---|
| `/f gui` | Open the interactive guide | All |
| `/f create <name>` | Create a faction | None |
| `/f disband` | Disband the faction | Leader |
| `/f invite <player>` | Invite a player | Leader/Mod |
| `/f join <faction>` | Join a faction (on invitation) | None |
| `/f leave` | Leave the faction | Member+ |
| `/f kick <player>` | Kick a player | Leader/Mod |
| `/f promote <player>` | Promote a member | Leader/Mod |
| `/f demote <player>` | Demote a member | Leader/Mod |
| `/f claim` | Claim the current chunk | All members |
| `/f unclaim` | Unclaim the current chunk | All members |
| `/f unclaimall` | Unclaim all territory | Leader/Mod |
| `/f sethome` | Set the faction HQ | All members |
| `/f home` | Teleport to the HQ | All members |
| `/f info [faction]` | Faction statistics | All |
| `/f c [message]` | Faction chat / toggle | All members |
| `/f reload` | Reload the configuration | Admin (`factions.admin`) |
| `/f help` | Text-based help | All |

## Permissions

| Permission | Description | Default |
|---|---|---|
| `factions.command.base` | Access to `/f` | true |
| `factions.command.<subcommand>` | Access to each subcommand (`create`, `claim`, `chat`, etc.) | true |
| `factions.command.gui` | Open the interactive guide | true |
| `factions.admin` | Access to admin commands (`/f reload`) | op |

## Power and Claims system

- Each player has a power between **0 and 10** (configurable).
- The faction's **total power** = the sum of its members' power.
- To claim a chunk: `total power > number of claims`.
- On each death: **−2 power**; on each kill: **+1 power**.
- Regeneration: **+1 per minute**, caught up even offline.
- **Overclaim**: an enemy chunk can be conquered when `enemy faction power < its claims` (enabled via `settings.overclaim`).
- Inside a claim, outside players cannot break/place blocks, open chests or interact with blocks (doors, buttons, furnaces, etc.).

## Configuration

Excerpt from `config.yml`:

```yaml
settings:
  max-power: 10.0          # maximum power per player
  power-per-death: -2.0    # loss on death
  power-per-kill: 1.0      # killer's gain
  power-regen-per-min: 1.0 # regeneration per minute
  max-members: 50          # max members per faction
  invite-expire-seconds: 300
  overclaim: true          # enables overclaim
  autosave-minutes: 5
```

All messages (English by default) can be customized in the `messages:` section.

## Building

```bash
mvn clean package
```

The JAR is generated at `target/SimpleFactions-1.0.0.jar`.

## Support

Report bugs or suggest improvements through the repository's Issues section.

License: [MIT](LICENSE)
