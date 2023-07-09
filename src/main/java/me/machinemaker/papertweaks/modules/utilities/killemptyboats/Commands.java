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
package me.machinemaker.papertweaks.modules.utilities.killemptyboats;

import cloud.commandframework.Command;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Boat;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

@ModuleCommand.Info(value = "killboats", descriptionKey = "modules.kill-empty-boats.commands.root", help = false, infoOnRoot = false)
class Commands extends ModuleCommand {

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.builder();

        this.manager.command(builder
            .permission(this.modulePermission("vanillatweaks.killboats"))
            .handler(this.sync(context -> {
                int count = 0;
                for (final World world : Bukkit.getWorlds()) {
                    for (final Boat boat : world.getEntitiesByClass(Boat.class)) {
                        if (boat.getPassengers().isEmpty()) {
                            count++;
                            boat.remove();
                        }
                    }
                }
                context.getSender().sendMessage(translatable("modules.kill-empty-boats.removed-boats", count > 0 ? YELLOW : RED, text(count)));
            }))
        );
    }
}
