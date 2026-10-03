package com.github.jackh54.drillcancel;

import me.serbob.mythictools.api.permission.PermissionHook;
import me.serbob.mythictools.api.permission.PermissionManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * MythicTools protection hook that keeps its tools from deleting spawners.
 *
 * <p>MythicTools' area tools call {@code Block.breakNaturally()} on every block in their 3x3, including the
 * block that was hit, and they ignore cancelled break events and their own block blacklist. A stacked
 * spawner removed that way never reaches VirtualSpawner, so the whole stack is lost. MythicTools asks its
 * permission hooks about the hit block (and cancels the swing if it is protected) and about each block in
 * the area (and skips protected ones), so answering "protected" for spawners stops both cases.
 *
 * <p>Only references MythicTools classes, so it must only be loaded when MythicTools is installed.
 */
final class SpawnerGuard extends PermissionHook {

    private final DrillCancel plugin;

    private SpawnerGuard(DrillCancel plugin) {
        this.plugin = plugin;
    }

    static void register(DrillCancel plugin) {
        PermissionManager.getInstance().addPermissionHook(new SpawnerGuard(plugin));
    }

    @Override
    public String pluginName() {
        // Empty name = always load, without adding an entry to MythicTools' hooks.yml.
        return "";
    }

    @Override
    public boolean isProtected(Player player, Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.SPAWNER) return false;
        // The block being broken has already been handled by the break's own listeners: if they kept it
        // (we or VirtualSpawner cancelled), MythicTools must not delete it; if the break goes ahead,
        // VirtualSpawner has already given the drop. Every other spawner in the area is always kept.
        BlockBreakEvent breaking = plugin.breakOf(block);
        return breaking == null || breaking.isCancelled();
    }
}
