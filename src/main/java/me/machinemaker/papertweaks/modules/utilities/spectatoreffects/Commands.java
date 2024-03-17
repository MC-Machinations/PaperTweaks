/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.modules.utilities.spectatoreffects;

import org.incendo.cloud.Command;
import me.machinemaker.papertweaks.cloud.MetaKeys;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@ModuleCommand.Info(value = "toggle-effect", aliases = "teffect", descriptionKey = "modules.spectator-effects.commands.root")
class Commands extends ModuleCommand {

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player()
            .meta(MetaKeys.GAMEMODE_KEY, GameMode.SPECTATOR); // TODO probably change to a permission

        this.register(builder
            .permission(this.modulePermission("vanillatweaks.spectatortoggle.nightvision"))
            .literal("night-vision")
            .handler(this.sync((context, player) -> this.toggleEffect(player, PotionEffectType.NIGHT_VISION)))
        );
        this.register(builder
            .permission(this.modulePermission("vanillatweaks.spectatortoggle.conduitpower"))
            .literal("conduit-power")
            .handler(this.sync((context, player) -> this.toggleEffect(player, PotionEffectType.CONDUIT_POWER)))
        );
    }

    private void toggleEffect(final Player player, final PotionEffectType type) {
        if (player.getPotionEffect(type) == null) {
            player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, 0, false, false, true));
        } else {
            player.removePotionEffect(type);
        }
    }

}
