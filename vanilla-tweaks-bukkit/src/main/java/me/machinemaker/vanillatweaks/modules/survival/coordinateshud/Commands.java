/*
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
package me.machinemaker.vanillatweaks.modules.survival.coordinateshud;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import static net.kyori.adventure.text.Component.translatable;

class Commands extends ModuleCommand {

    private final HUDRunnable hudRunnable;

    @Inject
    Commands(HUDRunnable hudRunnable) {
        this.hudRunnable = hudRunnable;
    }

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        manager.command(manager
                .commandBuilder("togglehud", "thud")
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.coordinateshud.togglehud"))
                .senderType(PlayerCommandDispatcher.class)
                .handler(context -> {
                    Player player = (Player) context.getSender().sender();
                    if (this.hudRunnable.getPlayers().remove(player)) {
                        context.getSender().sendMessage(translatable("modules.coordinates-hud.hud-off", NamedTextColor.GREEN));
                        player.getPersistentDataContainer().remove(this.hudRunnable.coordinatesHUDKey);
                    } else {
                        this.hudRunnable.getPlayers().add(player);
                        player.getPersistentDataContainer().set(this.hudRunnable.coordinatesHUDKey, PersistentDataType.BYTE, (byte) 1);
                        context.getSender().sendMessage(translatable("modules.coordinates-hud.hud-on", NamedTextColor.GREEN));
                    }
        }));
    }
}
