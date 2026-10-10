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
