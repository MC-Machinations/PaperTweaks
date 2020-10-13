package me.machinemaker.vanillatweaks.mobdrops;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.VanillaTweaksModules;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class MobDrops extends BaseModule implements Listener {

    @Inject
    VanillaTweaksModules config;

    public MobDrops(VanillaTweaks vanillaTweaks) {
        super(vanillaTweaks, config -> config.doubleShulkerShells || config.dragonDrops);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (config.doubleShulkerShells && event.getEntityType() == EntityType.SHULKER) {
            if (event.getEntity().getKiller() != null && !event.getEntity().getKiller().hasPermission("vanillatweaks.mobdrops.doubleshulkershells")) return;
            AtomicBoolean setDrops = new AtomicBoolean(false);
            event.getDrops().forEach(drop -> {
                if (drop.getType() == Material.SHULKER_SHELL) {
                    drop.setAmount(2);
                    setDrops.set(true);
                }
            });
            if (!setDrops.get()) {
                event.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 2));
            }
        }

        if (config.dragonDrops && event.getEntityType() == EntityType.ENDER_DRAGON) {
            if (event.getEntity().getKiller() != null && !event.getEntity().getKiller().hasPermission("vanillatweaks.mobdrops.dragondrops")) return;
            event.getDrops().add(new ItemStack(Material.DRAGON_EGG));
            event.getDrops().add(new ItemStack(Material.ELYTRA));
        }
    }

    @Override
    public void register() {
        this.registerEvents(this);
    }

    @Override
    public void unregister() {
        this.unregisterEvents(this);
    }
}
