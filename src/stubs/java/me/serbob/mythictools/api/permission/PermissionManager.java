package me.serbob.mythictools.api.permission;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Compile-time stub of MythicTools' public API (signatures only). It is not packaged:
 * at runtime the real class comes from the MythicTools jar.
 */
public class PermissionManager {

    public static PermissionManager getInstance() {
        throw new UnsupportedOperationException("stub");
    }

    public void addPermissionHook(PermissionHook hook) {
        throw new UnsupportedOperationException("stub");
    }

    public boolean isProtected(Player player, Location location) {
        throw new UnsupportedOperationException("stub");
    }

    public Set<PermissionHook> getActivePermissionHooks() {
        throw new UnsupportedOperationException("stub");
    }
}
