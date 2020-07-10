package me.machinemaker.vanillatweaks.thundershrine;

import com.google.common.collect.Sets;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class EffectRunnable extends BukkitRunnable {

    private final ThunderShrine module;
    private final Set<Entity> toBeRemoved = Sets.newHashSet();

    public EffectRunnable(ThunderShrine module) {
        this.module = module;
    }

    @Override
    public void run() {
        module.shrineLocations.forEach(entity -> {
            if (entity.isDead()) {
                toBeRemoved.add(entity);
            } else entity.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, entity.getLocation().add(0, 1, 0), 1, 0.1, 0.1, 0.1, 1);
        });
        if (toBeRemoved.size() > 0) {
            module.shrineLocations.removeAll(toBeRemoved);
            toBeRemoved.clear();
        }
    }
}
