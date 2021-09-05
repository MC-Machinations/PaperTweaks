package me.machinemaker.vanillatweaks.mobgriefing;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.VanillaTweaksModules;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class MobGriefing extends BaseModule implements Listener {

    @Inject
    VanillaTweaksModules config;

    public MobGriefing(VanillaTweaks vanillaTweaks) {
        super(vanillaTweaks, config -> config.antiCreeperGrief || config.antiGhastGrief || config.antiEndermanGrief);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (config.antiEndermanGrief && event.getEntityType() == EntityType.ENDERMAN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (config.antiGhastGrief && event.getEntityType() == EntityType.FIREBALL && ((Fireball) event.getEntity()).getShooter() instanceof Ghast) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (config.antiCreeperGrief && event.getEntityType() == EntityType.CREEPER) {
            event.setFire(false);
            event.setRadius(0);
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
