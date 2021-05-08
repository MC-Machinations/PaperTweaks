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
package me.machinemaker.vanillatweaks.modules.survival.netherportalcoords;

import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.IntUnaryOperator;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.MappedTranslatableComponent.mapped;
import static net.kyori.adventure.text.MappedTranslatableComponent.mappedBuilder;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ModuleCommand {

    private final Config config;

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        var builder = this.manager.commandBuilder("portalcoords", RichDescription.of(mapped("modules.nether-portal-coords.commands.root")), "pcoords");

        this.manager
                .command(builder
                        .permission(ModulePermission.of(lifecycle, "vanillatweaks.netherportalcoords"))
                        .senderType(PlayerCommandDispatcher.class)
                        .handler(context -> {
                            Player player = PlayerCommandDispatcher.from(context);
                            Location loc = player.getLocation();
                            if (this.config.overWorlds().contains(player.getWorld())) {
                                Component coords = coords(loc, i -> i / 8);
                                context.getSender().sendMessage(msg(coords, "Nether"));
                            } else if (this.config.netherWorlds().contains(player.getWorld())) {
                                Component coords = coords(loc, i -> i * 8);
                                context.getSender().sendMessage(msg(coords, "Overworld"));
                            } else {
                                context.getSender().sendMessage(mapped("modules.nether-portal-coords.invalid-world", RED));
                            }
                        })
        );
    }

    private Component coords(Location loc, IntUnaryOperator op) {
        return mappedBuilder("modules.nether-portal-coords.coord-format", GREEN).arg("x", text(op.applyAsInt(loc.getBlockX()), GOLD)).arg("y", text(loc.getBlockY(), GOLD)).arg("z", text(op.applyAsInt(loc.getBlockZ()), GOLD)).build();
    }

    private Component msg(Component coords, String world) {
        return mappedBuilder("modules.nether-portal-coords.msg-format").arg("world", text(world, YELLOW)).arg("coords", coords).build();
    }

    @Inject
    Commands(Config config) {
        this.config = config;
    }
}
