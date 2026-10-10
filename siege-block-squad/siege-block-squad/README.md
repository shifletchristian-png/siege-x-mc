# Siege Block Squad (draft, not yet compiled or tested)

A Minecraft: Java Edition (Fabric 1.21.1) co-op mod. Up to 4 players pick a Rainbow Six Siege style operator, then clear a block-built version of the Oregon map (basement, two floors and a tower, simplified from public descriptions, not Siege map data) and rescue a hostage in the kids dorm.

## What a player gets
- `/operator` shows a clickable list; pick Breacher, Medic, Intel or Anchor. Each gives one gadget.
- `/siege start` builds the fortress, spawns the enemies and a hostage, and teleports the squad in.
- Win by clearing every enemy with the hostage alive; lose if the hostage dies or the whole squad goes down.
- Multiplayer: up to 4 players, via Open to LAN or a dedicated server. Melty auto-connect is not configured because joining has not been tested.

## Siege content
Operator and enemy rows carry `siegeAssetRef` fields. Nothing from Rainbow Six Siege is read yet; see STATUS.md.

## Build
`python3 -I tools/gen.py` (preflight + generate Java), then `gradle build`. Source of truth is `sheets/*.json`.

## Credits and license
Not chosen yet. The owner must pick a content license and remix setting before listing.
