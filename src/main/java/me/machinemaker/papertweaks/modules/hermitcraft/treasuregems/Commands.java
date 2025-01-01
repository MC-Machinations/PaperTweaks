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
package me.machinemaker.papertweaks.modules.hermitcraft.treasuregems;

import com.google.inject.Inject;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;

import static me.machinemaker.papertweaks.cloud.parsers.PseudoEnumParser.singlePseudoEnumParser;
import static org.incendo.cloud.component.DefaultValue.constant;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

@ModuleCommand.Info(value = "treasuregems", aliases = {"tgems"}, i18n = "treasure-gems", perm = "treasuregems")
class Commands extends ConfiguredModuleCommand {

    private final TreasureGems treasureGems;

    @Inject
    Commands(final TreasureGems treasureGems) {
        this.treasureGems = treasureGems;
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        this.register(
            this.literal(builder, "give")
                .required("head", singlePseudoEnumParser(this.treasureGems.heads.keySet()))
                .optional("count", integerParser(1), constant(1))
                .handler(this.sync((context, player) -> {
                    final ItemStack head = this.treasureGems.heads.get((String) context.get("head")).clone();
                    head.setAmount(context.get("count"));
                    player.getInventory()
                        .addItem(head).values()
                        .forEach(extraHead -> player.getWorld().dropItem(player.getLocation(), extraHead, item -> item.setOwner(player.getUniqueId())));
                }))
        );
    }
}
