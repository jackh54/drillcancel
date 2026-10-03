package me.serbob.mythictools.api.permission;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Compile-time stub of MythicTools' public API (signatures only). It is not packaged:
 * at runtime the real class comes from the MythicTools jar.
 */
public abstract class PermissionHook {

    public abstract String pluginName();

    public boolean shouldLoad() {
        throw new UnsupportedOperationException("stub");
    }

    public abstract boolean isProtected(Player player, Location location);
}
