/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@ModuleCommand.Info(value = "portalcoords", aliases = "pcoords", descriptionKey = "modules.nether-portal-coords.commands.root", miniMessage = true, infoOnRoot = false)
class Commands extends ModuleCommand {

    private final Config config;
    private final MessageService messageService;

    @Inject
    Commands(Config config, MessageService messageService) {
        this.config = config;
        this.messageService = messageService;
    }

    @Override
    protected void registerCommands() {
        var builder = this.player();

        this.manager.command(builder
                .permission(modulePermission("vanillatweaks.netherportalcoords"))
                .handler(context -> {
                    Player player = PlayerCommandDispatcher.from(context);
                    Location loc = player.getLocation();
                    if (this.config.overWorlds().contains(player.getWorld())) {
                        this.messageService.coordinatesMsg(context.getSender(), new MessageService.CoordinatesComponent(loc, i -> i / 8), "Nether"); // TODO use coord scale from DimensionType
                    } else if (this.config.netherWorlds().contains(player.getWorld())) {
                        this.messageService.coordinatesMsg(context.getSender(), new MessageService.CoordinatesComponent(loc, i -> i * 8), "Overworld"); // TODO use coord scale from DimensionType
                    } else {
                        this.messageService.invalidWorld(context.getSender());
                    }
                })
        );
    }
}
