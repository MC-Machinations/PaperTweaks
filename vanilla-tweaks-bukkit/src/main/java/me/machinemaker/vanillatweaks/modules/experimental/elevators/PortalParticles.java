package me.machinemaker.vanillatweaks.modules.experimental.elevators;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.entity.Marker;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

class PortalParticles extends BukkitRunnable {

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            for (Marker marker : world.getEntitiesByClass(Marker.class)) {
                if (Elevators.IS_ELEVATOR.has(marker)) {
                    if (!Tag.WOOL.isTagged(marker.getLocation().getBlock().getType())) {
                        marker.remove();
                        marker.getWorld().dropItem(marker.getLocation(), new ItemStack(Material.ENDER_PEARL));
                        return;
                    }
                    marker.getWorld().spawnParticle(Particle.REVERSE_PORTAL, marker.getLocation().add(0, 0.5, 0), 1, 0.25, 0, 0.25, 0.02);
                }
            }
        }
    }
}
