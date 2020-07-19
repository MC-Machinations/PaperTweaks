package me.machinemaker.vanillatweaks.thundershrine;

import com.google.common.collect.Sets;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.utils.DataType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.stream.Collectors;

public class ThunderShrine extends BaseModule implements Listener {

    final NamespacedKey SHRINE = new NamespacedKey(this.plugin, "shrine");

    private ShrineRunnable runnable;
    EffectRunnable effectRunnable;
    private EntityLoadRunnable loadRunnable = new EntityLoadRunnable();

    final Set<Entity> shrineLocations = Sets.newHashSet();

    private Commands commands;

    public ThunderShrine(VanillaTweaks plugin) {
        super(plugin, config -> config.thunderShrine);
    }

    @Override
    public void register() {
        this.commands = new Commands(this);
        this.registerCommands(commands);
        runnable = new ShrineRunnable(this);
        effectRunnable = new EffectRunnable(this);
        loadRunnable = new EntityLoadRunnable();
        runnable.runTaskTimer(this.plugin, 1L, 10L);
        effectRunnable.runTaskTimer(this.plugin, 1L, 3L);
        shrineLocations.clear();
        loadRunnable.runTaskTimer(this.plugin, 1L, 100L);
    }

    @Override
    public void unregister() {
        this.unregisterCommands(commands);
        runnable.cancel();
        effectRunnable.cancel();
        loadRunnable.cancel();
    }

    class EntityLoadRunnable extends BukkitRunnable {

        @Override
        public void run() {
            Bukkit.getWorlds().forEach(world -> shrineLocations.addAll(world.getEntities().stream().filter(entity -> entity.getType() == EntityType.AREA_EFFECT_CLOUD && entity.getPersistentDataContainer().has(SHRINE, DataType.UUID) && !shrineLocations.contains(entity)).collect(Collectors.toSet())));
        }
    }
}
