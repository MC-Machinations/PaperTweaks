/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.coordinateshud;

import com.google.inject.Inject;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

@ModuleCommand.Info(value = "togglehud", aliases = "thud", descriptionKey = "modules.coordinates-hud.commands", infoOnRoot = false)
class Commands extends ModuleCommand {

    private final HUDRunnable hudRunnable;

    @Inject
    Commands(final HUDRunnable hudRunnable) {
        this.hudRunnable = hudRunnable;
    }

    @Override
    protected void registerCommands() {
        this.register(this.player()
            .permission(this.modulePermission("vanillatweaks.coordinateshud.togglehud"))
            .handler(this.sync((context, player) -> {
                if (this.hudRunnable.contains(player)) {
                    this.hudRunnable.setAndRemove(player);
                    context.getSender().sendMessage(translatable("modules.coordinates-hud.hud-off", GREEN));
                    context.getSender().sendActionBar(Component.empty());
                } else {
                    this.hudRunnable.setAndAdd(player);
                    context.getSender().sendMessage(translatable("modules.coordinates-hud.hud-on", GREEN));
                }
            }))
        );
    }
}
