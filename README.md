# DrillCancel

Small Paper/Folia plugin that stops MythicTools tools from deleting spawners.

- Breaking a spawner with a drill is cancelled, and the player sees a red action bar message: `You cannot mine a spawner with a drill`.
  An item counts as a drill when its name or lore contains one of the strings in `config.yml` (case-insensitive, colors ignored).
  Lore matching is the default because it survives anvil renames.
- With MythicTools installed, it also registers a MythicTools protection hook that keeps every MythicTools area tool (drill, shovel)
  away from spawners: a spawner next to the mined block is skipped, and a spawner that was hit is only removed if the break itself
  went ahead.

## Why the hook is needed (1.0.0 did not stop the loss)

MythicTools' area tools call `Block.breakNaturally()` on every block of their 3x3, including the block that was hit. They ignore
cancelled break events, and they never check MythicTools' own `blacklist.yml` block list. `breakNaturally()` fires no event, so
VirtualSpawner never hears about it and the whole stack is lost (CoreProtect logs nothing either). Cancelling the break, as 1.0.0 did,
only made VirtualSpawner skip the break; the drill still deleted the spawner. MythicTools does ask its permission hooks about the hit
block (and cancels the swing when it is protected) and about each block of the area (and skips protected ones), so the hook covers both.

## Build

`mvn package` builds `target/DrillCancel-<version>.jar`. It compiles against signature-only stubs of the MythicTools API in
`src/stubs/java`; they are not packaged, the real classes come from the MythicTools jar at runtime. A prebuilt jar is in `release/`.
