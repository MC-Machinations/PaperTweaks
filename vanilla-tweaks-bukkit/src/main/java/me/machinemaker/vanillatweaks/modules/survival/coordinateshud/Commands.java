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
package me.machinemaker.vanillatweaks.modules.survival.coordinateshud;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.persistence.PersistentDataType;

import static net.kyori.adventure.text.Component.translatable;

@ModuleCommand.Info(value = "togglehud", aliases = "thud", descriptionKey = "modules.coordinates-hud.commands", infoOnRoot = false)
class Commands extends ModuleCommand {

    private final HUDRunnable hudRunnable;

    @Inject
    Commands(HUDRunnable hudRunnable) {
        this.hudRunnable = hudRunnable;
    }

    @Override
    protected void registerCommands() {
        manager.command(this.player()
                .permission(modulePermission("vanillatweaks.coordinateshud.togglehud"))
                .handler(sync((context, player) -> {
                    if (this.hudRunnable.getPlayers().remove(player)) {
                        context.getSender().sendMessage(translatable("modules.coordinates-hud.hud-off", NamedTextColor.GREEN));
                        context.getSender().sendActionBar(Component.empty());
                        player.getPersistentDataContainer().remove(HUDRunnable.COORDINATES_HUD_KEY);
                    } else {
                        this.hudRunnable.getPlayers().add(player);
                        player.getPersistentDataContainer().set(HUDRunnable.COORDINATES_HUD_KEY, PersistentDataType.BYTE, (byte) 1);
                        context.getSender().sendMessage(translatable("modules.coordinates-hud.hud-on", NamedTextColor.GREEN));
                    }
                }))
        );
    }
}
