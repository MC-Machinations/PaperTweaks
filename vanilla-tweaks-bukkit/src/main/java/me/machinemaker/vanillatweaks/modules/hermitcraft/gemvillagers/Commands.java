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
package me.machinemaker.vanillatweaks.modules.hermitcraft.gemvillagers;

import cloud.commandframework.bukkit.parsers.location.LocationArgument;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.arguments.PseudoEnumArgument;
import me.machinemaker.vanillatweaks.modules.ConfiguredModuleCommand;
import org.bukkit.Location;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ConfiguredModuleCommand {

    private final GemVillagers gemVillagers;

    @Inject
    Commands(GemVillagers gemVillagers) {
        super("gem-villagers", "gemvillagers");
        this.gemVillagers = gemVillagers;
    }

    @Override
    protected void registerCommands() {
        var builder = playerCmd("gemvillagers", "modules.gem-villagers.commands.root", "gvillagers", "gv");

        manager.command(literal(builder, "spawn")
                .argument(PseudoEnumArgument.single("villager", this.gemVillagers.villagers.keySet()))
                .argument(LocationArgument.optional("loc"))
                .handler(sync((context, player) -> {
                    String villager = context.get("villager");
                    Location loc = context.<Location>getOptional("loc").orElse(player.getLocation());
                    this.gemVillagers.villagers.get(villager).spawnVillager(loc.getWorld(), loc);
                    context.getSender().sendMessage(translatable("modules.gem-villagers.commands.spawn.success", YELLOW, text(villager, GOLD)));
                }))
        );
    }
}
