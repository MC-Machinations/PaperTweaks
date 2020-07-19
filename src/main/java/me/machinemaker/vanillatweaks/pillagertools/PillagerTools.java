package me.machinemaker.vanillatweaks.pillagertools;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PillagerTools extends BaseModule implements Listener {

    final Config config = new Config();
    private Commands commands;

    public PillagerTools(VanillaTweaks plugin) {
        super(plugin, config -> config.pillagerTools);
        config.init(plugin, new File(plugin.getDataFolder(), "pillagertools"));
        plugin.commandManager.getCommandCompletions().registerStaticCompletion("pillagertools/toggles", Arrays.stream(ToggleOption.values()).map(ToggleOption::name).map(String::toLowerCase).collect(Collectors.toSet()));
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.PILLAGER &&  (!config.patrolLeaders || config.patrols) && event.getSpawnReason() == SpawnReason.PATROL) {
            Pillager pillager = (Pillager) event.getEntity();
            if (!config.patrolLeaders && pillager.isPatrolLeader() && pillager.getPatrolTarget() != null) {
                event.setCancelled(true);
            }
            if (!config.patrols && pillager.getPatrolTarget() != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onStatusEffectChange(EntityPotionEffectEvent event) {
        if (!config.badOmen && event.getCause() == Cause.PATROL_CAPTAIN) event.setCancelled(true);
    }

    @Override
    public void register() {
        this.commands = new Commands(this);
        this.registerCommands(commands);
        this.registerEvents(this);
    }

    @Override
    public void unregister() {
        this.unregisterCommands(commands);
        this.unregisterEvents(this);
    }

    @Override
    public void reload() {
        config.reload();
    }

    enum ToggleOption {
        PATROLS,
        PATROL_LEADERS,
        BAD_OMEN
    }
}
