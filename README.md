# DrillCancel

Small Paper/Folia plugin that stops MythicTools drills from mining spawners. The break is cancelled and the player sees a red action bar message: `You cannot mine a spawner with a drill`.

An item counts as a drill when its name or lore contains one of the strings in `config.yml` (case-insensitive, colors ignored). Lore matching is the default because it survives anvil renames.

Build with `mvn package`. A prebuilt jar is in `release/`.
