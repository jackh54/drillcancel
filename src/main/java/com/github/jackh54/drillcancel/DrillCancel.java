package com.github.jackh54.drillcancel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Locale;

public final class DrillCancel extends JavaPlugin implements Listener {

    private static final Component MESSAGE =
            Component.text("You cannot mine a spawner with a drill", NamedTextColor.RED);

    // Break events being handled on this thread (Folia runs each region on its own thread). A stack, because
    // a plugin can fire another BlockBreakEvent while one is being handled.
    private final ThreadLocal<ArrayDeque<BlockBreakEvent>> breaks = ThreadLocal.withInitial(ArrayDeque::new);

    private List<String> nameContains;
    private List<String> loreContains;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        nameContains = lowered(getConfig().getStringList("name-contains"));
        loreContains = lowered(getConfig().getStringList("lore-contains"));
        getServer().getPluginManager().registerEvents(this, this);
        if (getServer().getPluginManager().isPluginEnabled("MythicTools")) {
            SpawnerGuard.register(this);
            getLogger().info("MythicTools tools can no longer delete spawners.");
        } else {
            getLogger().warning("MythicTools is not enabled; only drill breaks of spawners are cancelled.");
        }
    }

    /** The break event for this block that is being handled right now on this thread, or null. */
    BlockBreakEvent breakOf(Block block) {
        for (BlockBreakEvent event : breaks.get()) {
            if (event.getBlock().equals(block)) return event;
        }
        return null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void trackBreak(BlockBreakEvent event) {
        breaks.get().push(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void untrackBreak(BlockBreakEvent event) {
        breaks.get().remove(event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.SPAWNER) return;
        if (!isDrill(event.getPlayer().getInventory().getItemInMainHand())) return;
        event.setCancelled(true);
        event.getPlayer().sendActionBar(MESSAGE);
    }

    private boolean isDrill(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (matches(meta.displayName(), nameContains)) return true;
        List<Component> lore = meta.lore();
        if (lore == null) return false;
        for (Component line : lore) {
            if (matches(line, loreContains)) return true;
        }
        return false;
    }

    private static boolean matches(Component text, List<String> needles) {
        if (text == null || needles.isEmpty()) return false;
        String plain = PlainTextComponentSerializer.plainText().serialize(text).toLowerCase(Locale.ROOT);
        for (String needle : needles) {
            if (plain.contains(needle)) return true;
        }
        return false;
    }

    private static List<String> lowered(List<String> values) {
        return values.stream().map(s -> s.toLowerCase(Locale.ROOT)).filter(s -> !s.isBlank()).toList();
    }
}
