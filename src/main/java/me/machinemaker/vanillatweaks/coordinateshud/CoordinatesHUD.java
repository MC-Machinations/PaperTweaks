package me.machinemaker.vanillatweaks.coordinateshud;

import com.google.common.collect.Lists;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CoordinatesHUD extends BaseModule {

    final List<Player> enabled = Lists.newArrayList();
    private HUDRunnable runnable;

    public CoordinatesHUD(VanillaTweaks plugin) {
        super(plugin, config -> config.coordinatesHud);
        this.registerCommands(new Commands(this));
    }

    @Override
    public void register() {
        runnable = new HUDRunnable();
        runnable.runTaskTimer(this.plugin, 1L, 2L);
    }

    @Override
    public void unregister() {
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
        if (degrees <= 22) return Direction.NORTH;
        if (degrees <= 67) return Direction.NORTHEAST;
        if (degrees <= 112) return Direction.EAST;
        if (degrees <= 157) return Direction.SOUTHEAST;
        if (degrees <= 202) return Direction.SOUTH;
        if (degrees <= 247) return Direction.SOUTHWEST;
        if (degrees <= 292) return Direction.WEST;
        if (degrees <= 337) return Direction.NORTHWEST;
        return Direction.NORTH;
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
