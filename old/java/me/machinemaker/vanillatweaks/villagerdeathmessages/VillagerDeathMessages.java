package me.machinemaker.vanillatweaks.villagerdeathmessages;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.Lang;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;

import java.io.File;

public class VillagerDeathMessages extends BaseModule implements Listener {

    Config config = new Config();

    public VillagerDeathMessages(VanillaTweaks plugin) {
        super(plugin, config -> config.villagerDeathMsgs);
        config.init(plugin, new File(plugin.getDataFolder(), "villagerdeathmessages"));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER && config.showMessageOnDeath) {
            Location loc = event.getEntity().getLocation();
            Bukkit.broadcast(Lang.VILLAGER_DEATH.toString().replace("%x%", String.valueOf(loc.getBlockX())).replace("%y%", String.valueOf(loc.getBlockY())).replace("%z%", String.valueOf(loc.getBlockZ())).replace("%world%", loc.getWorld().getName()), "vanillatweaks.villagerdeathmessages.death");
        }
    }

    @EventHandler
    public void onEntityConvert(EntityTransformEvent event) {
        if (event.getTransformReason() == TransformReason.INFECTION && config.showMessageOnConversion) {
            Bukkit.broadcast(Lang.VILLAGER_CONVERSION.toString(), "vanillatweaks.villagerdeathmessages.conversion");
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

    @Override
    public void reload() {
        config.reload();
    }
}
