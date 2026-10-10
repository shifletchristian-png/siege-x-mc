# Status

- Sheets: complete; `python3 -I tools/gen.py --check` passes (preflight clean).
- Source: written, NOT compiled. Gradle could not fetch fabric-loom, Minecraft or Fabric API (build hosts blocked in this workspace). Method names target Minecraft 1.21.1 with Mojang mappings and may need small fixes at first compile.
- Tested in game: no. No Minecraft or Siege install is reachable from this workspace.
- Rainbow Six Siege content: not read yet. Siege is only a companion (never modded or launched). Reading its operator art, sounds or models from the player's install is unproven; its files are packed archives and need inspection on a PC that has the game.
- Multiplayer: maxPlayers 4. `connect` deliberately omitted until a two-instance host/join test is done.
- Melty: unreachable from here (melty.gg blocked). No draft, upload or publish done. list_my_mods, search_mashups, game_info, validate_recipe and one_click_check have not run.
- Listing: title/tagline/description drafted in README; license, credits and remix choice still need the owner.
- Screenshot: none yet.

## Build without local network access
`.github/workflows/build.yml` builds the jar on GitHub's servers on every push (preflight, generate, `gradle build`, upload the jar as an artifact). Compile errors will show in the Actions log and can be fixed from there. Still needs: a GitHub repo for this folder, and melty.gg access (or the owner uploading in Melty Studio) for the listing.

## Siege content research (from the owner's file list, 2026-10-10)
- Siege assets sit in .forge archives (textures, meshes, Wwise soundmedia/soundbank) plus plain .webm videos in \videos. MatchReplay/ is private match data: never use or ship.
- Community readers exist: parzivail/RainbowForge (.NET, archived Mar 2024, license not stated, UID-based, textures+meshes, no sound info) and TrueShadow01/R6-Extractor (GPL-3.0, Python/Windows, operator names and models to glTF, needs an Oodle DLL, may break on game updates).
- Not verified: sound extraction, operator icons, any run against the owner's current install. Nothing was run; files are not reachable from this workspace.
- Licensing: GPL code cannot be bundled into a mod without making the mod GPL; Oodle cannot be redistributed. A mod would need its own small reader, or read only plain files (the .webm videos).
- Plain-file option usable now: videos. Everything else needs testing on a machine with Siege.

## Oregon fortress (2026-10-10)
Fortress is now a simplified block layout of Siege's Oregon map built from public descriptions (liquipedia, siege.gg): basement (laundry, supply, corridor), 1F (dining hall, kitchen, meeting hall, rear stage), 2F (dorm main hall, kids dorm, generator, armory hall), 3-level tower. Hostage in kids dorm. Layout is in sheets/fortress.json and connections.json. This is a Minecraft-built look-alike, not Siege content read from the player's install. Not yet compiled or tested after this change.
