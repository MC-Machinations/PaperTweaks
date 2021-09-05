package me.machinemaker.vanillatweaks.spectatortoggle;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class Commands extends BaseModuleCommand<SpectatorToggleEffect> {

    public Commands(SpectatorToggleEffect module) {
        super(module);
    }

    @CommandAlias("togglenightvision|togglenv|tnv")
    @Conditions("gamemode:SPECTATOR")
    @CommandPermission("vanillatweaks.spectatortoggle.nightvision")
    public void toggleNightVision(Player player) {
        if (!this.module.plugin.modules.spectatorNightVision) {
            player.sendMessage("Not enabled");
            return;
        }
        if (player.getPotionEffect(PotionEffectType.NIGHT_VISION) == null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, true));
        } else player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    @CommandAlias("toggleconduitpower|togglecp|tcp")
    @Conditions("gamemode:SPECTATOR")
    @CommandPermission("vanillatweaks.spectatortoggle.conduitpower")
    public void toggleConduitPowder(Player player) {
        if (!this.module.plugin.modules.spectatorConduitPower) {
            player.sendMessage("Not enabled");
            return;
        }
        if (player.getPotionEffect(PotionEffectType.CONDUIT_POWER) == null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, Integer.MAX_VALUE, 0, false, false, true));
        } else player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
    }
}
