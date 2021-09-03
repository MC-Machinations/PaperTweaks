package me.machinemaker.vanillatweaks.modules.experimental.elevators;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

class PlayerListener implements ModuleListener {

    private final Config config;
    private final JavaPlugin plugin;

    @Inject
    PlayerListener(Config config, JavaPlugin plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (canUseElevator(event) && event.getPlayer().getVelocity().getY() > 0 && isOnElevator(event.getPlayer().getLocation())) {
            Location elevatorLoc = event.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getLocation();
            Collection<Marker> elevators = VTUtils.getNearbyEntitiesOfType(Marker.class, elevatorLoc.add(0.5, 0.5, 0.5), 0.01, config.maxVerticalSearch + 0.01, 0.01, marker -> Elevators.IS_ELEVATOR.has(marker) && marker.getLocation().getBlockY() > elevatorLoc.getBlockY());
            teleportPlayer(event.getPlayer(), elevatorLoc, elevators);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (canUseElevator(event) && event.isSneaking() && isOnElevator(event.getPlayer().getLocation())) {
            Location elevatorLoc = event.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getLocation();
            Collection<Marker> elevators = VTUtils.getNearbyEntitiesOfType(Marker.class, elevatorLoc.add(0.5, 0.5, 0.5), 0.01, config.maxVerticalSearch + 0.01, 0.01, marker -> Elevators.IS_ELEVATOR.has(marker) && marker.getLocation().getBlockY() < elevatorLoc.getBlockY());
            teleportPlayer(event.getPlayer(), elevatorLoc, elevators);
        }
    }

    private void teleportPlayer(Player player, Location start, Collection<Marker> possibleLocations) {
        if (!possibleLocations.isEmpty()) {
            Marker next = Collections.min(possibleLocations, Comparator.comparingDouble(value -> value.getLocation().distanceSquared(start)));
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                player.teleport(new Location(next.getWorld(), next.getLocation().getX(), next.getLocation().getBlockY() + 1D, next.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch()));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.4f, 1f);
            }, 1L);
        }
    }

    private static boolean canUseElevator(PlayerEvent event) {
        return event.getPlayer().hasPermission("vanillatweaks.elevators.use");
    }

    private static boolean isOnElevator(@NotNull Location location) {
        return VTUtils.getSingleNearbyEntityOfType(Marker.class, location.subtract(0, 1, 0).getBlock().getLocation().add(0.5, 0.5, 0.5), 0.1, 0.1, 0.1, Elevators.IS_ELEVATOR::has) != null;
    }
}
