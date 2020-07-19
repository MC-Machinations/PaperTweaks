package me.machinemaker.vanillatweaks.coordinateshud;

import com.google.common.collect.Lists;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;

public class CoordinatesHUD extends BaseModule implements Listener {

    final List<Player> enabled = Lists.newArrayList();
    final NamespacedKey coordinatesKey = new NamespacedKey(this.plugin, "coordinatesHUD");
    private HUDRunnable runnable;
    private Commands commands;
    private final Config config = new Config();

    public CoordinatesHUD(VanillaTweaks plugin) {
        super(plugin, config -> config.coordinatesHud);
        config.init(plugin, new File(plugin.getDataFolder(), "coordinateshud"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getPersistentDataContainer().has(this.coordinatesKey, PersistentDataType.BYTE)) {
            enabled.add(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        enabled.remove(event.getPlayer());
    }

    @Override
    public void register() {
        this.commands = new Commands(this);
        this.registerCommands(commands);
        this.registerEvents(this);
        runnable = new HUDRunnable();
        runnable.runTaskTimer(this.plugin, 1L, config.ticks);
    }

    @Override
    public void unregister() {
        this.unregisterCommands(commands);
        this.unregisterEvents(this);
        runnable.cancel();
    }

    private class HUDRunnable extends BukkitRunnable {

        @Override
        public void run() {
            CoordinatesHUD.this.enabled.forEach(player -> {
                long time = (player.getWorld().getTime() + 6000) % 24000;
                long hours = time / 1000;
                Long extra = (time - (hours * 1000)) * 60 / 1000;

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.format(ChatColor.GOLD + "XYZ: "+ ChatColor.RESET + "%d %d %d  " + ChatColor.GOLD + "%2s      %02d:%02d",
                        player.getLocation().getBlockX(),
                        player.getLocation().getBlockY(),
                        player.getLocation().getBlockZ(),
                        getDirection(player.getLocation().getYaw()).c,
                        hours,
                        extra
                )));
            });
        }
    }

    private Direction getDirection(float yaw) {
        int degrees = (Math.round(yaw) + 270) % 360;
        if (degrees <= 22) return Direction.WEST;
        if (degrees <= 67) return Direction.NORTHWEST;
        if (degrees <= 112) return Direction.NORTH;
        if (degrees <= 157) return Direction.NORTHEAST;
        if (degrees <= 202) return Direction.EAST;
        if (degrees <= 247) return Direction.SOUTHEAST;
        if (degrees <= 292) return Direction.SOUTH;
        if (degrees <= 337) return Direction.SOUTHWEST;
        return Direction.WEST;
    }

    private enum Direction {
        NORTH("N"),
        NORTHEAST("NE"),
        EAST("E"),
        SOUTHEAST("SE"),
        SOUTH("S"),
        SOUTHWEST("SW"),
        WEST("W"),
        NORTHWEST("NW");

        final String c;
        Direction(String c) {
            this.c = c;
        }
    }
}
