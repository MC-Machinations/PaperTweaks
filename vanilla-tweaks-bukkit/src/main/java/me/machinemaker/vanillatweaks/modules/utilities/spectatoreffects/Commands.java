/*
 * GNU General Public License v3
 *
 * VanillaTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021 Machine_Maker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.machinemaker.vanillatweaks.modules.utilities.spectatoreffects;

import cloud.commandframework.minecraft.extras.RichDescription;
import me.machinemaker.vanillatweaks.cloud.MetaKeys;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class Commands extends ModuleCommand {

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        var builder = manager
                .commandBuilder("toggle-effect", RichDescription.translatable("modules.spectator-effects.commands.root"), "teffect")
                .senderType(PlayerCommandDispatcher.class)
                .meta(MetaKeys.GAMEMODE_KEY, GameMode.SPECTATOR); // TODO probably change to a permission

        manager.command(builder
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.spectatortoggle.nightvision"))
                .literal("night-vision")
                .handler(commandContext -> {
                    manager.taskRecipe().begin(commandContext).synchronous(context -> {
                        toggleEffect(PlayerCommandDispatcher.from(context), PotionEffectType.NIGHT_VISION);
                    }).execute();
                })).command(builder
                        .permission(ModulePermission.of(lifecycle, "vanillatweaks.spectatortoggle.conduitpower"))
                        .literal("conduit-power")
                        .handler(commandContext -> {
                            manager.taskRecipe().begin(commandContext).synchronous(context -> {
                                toggleEffect(PlayerCommandDispatcher.from(context), PotionEffectType.CONDUIT_POWER);
                            }).execute();
                        })
                );
    }

    private void toggleEffect(Player player, PotionEffectType type) {
        if (player.getPotionEffect(type) == null) {
            player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, 0, false, false, true));
        } else {
            player.removePotionEffect(type);
        }
    }

}
