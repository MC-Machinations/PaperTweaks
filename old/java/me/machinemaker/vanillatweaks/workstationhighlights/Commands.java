package me.machinemaker.vanillatweaks.workstationhighlights;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.memory.MemoryKey;

import java.util.Optional;

class Commands extends BaseModuleCommand<WorkstationHighlights> {

    public Commands(WorkstationHighlights module) {
        super(module);
    }

    @CommandAlias("findworkstation")
    @Description("Finds the nearest villager's workstation")
    @CommandPermission("vanillatweaks.workstationhighlights.findworkstation")
    public void findStation(Player player) {
        Optional<AbstractVillager> villagerOptional = player.getNearbyEntities(3, 3, 3).stream().filter(entity -> entity.getType() == EntityType.VILLAGER).map(entity -> (AbstractVillager) entity).findAny();
        if (!villagerOptional.isPresent()) {
            player.sendMessage(Lang.NO_VILLAGER.p());
            return;
        }
        Location jobSite = villagerOptional.get().getMemory(MemoryKey.JOB_SITE);
        if (jobSite == null) {
            player.sendMessage(Lang.NO_JOB_SITE.p());
            return;
        }
        AreaEffectCloud cloud = (AreaEffectCloud) jobSite.getWorld().spawnEntity(jobSite.add(0.5, 1, 0.5), EntityType.AREA_EFFECT_CLOUD);
        cloud.setParticle(Particle.HEART);
        cloud.setReapplicationDelay(10);
        cloud.setRadius(0.5f);
        cloud.setRadiusPerTick(0f);
        cloud.setRadiusOnUse(0f);
        cloud.setDuration(200);
        player.sendMessage(Lang.HIGHLIGHTED_SITE.p());
    }
}
