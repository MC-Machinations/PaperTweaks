/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules.hermitcraft.gemvillagers;

import com.google.inject.Inject;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.cloud.parsers.PseudoEnumParser;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import org.bukkit.Location;
import org.incendo.cloud.bukkit.parser.location.LocationParser;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static org.incendo.cloud.component.DefaultValue.dynamic;

@ModuleCommand.Info(value = "gemvillagers", aliases = {"gvillagers", "gv"}, i18n = "gem-villagers", perm = "gemvillagers")
class Commands extends ConfiguredModuleCommand {

    private final GemVillagers gemVillagers;

    @Inject
    Commands(final GemVillagers gemVillagers) {
        this.gemVillagers = gemVillagers;
    }

    @Override
    protected void registerCommands() {
        this.register(
            this.literal(this.player(), "spawn")
                .senderType(PlayerCommandDispatcher.class)
                .required("villager", PseudoEnumParser.singlePseudoEnumParser(this.gemVillagers.villagers.keySet()))
                .optional("loc", LocationParser.locationParser(), dynamic(ctx -> ctx.sender().sender().getLocation()))
                .handler(this.sync((context, player) -> {
                    final String villager = context.get("villager");
                    final Location loc = context.get("loc");
                    this.gemVillagers.villagers.get(villager).spawnVillager(loc.getWorld(), loc);
                    context.sender().sendMessage(translatable("modules.gem-villagers.commands.spawn.success", YELLOW, text(villager, GOLD)));
                }))
        );
    }
}
