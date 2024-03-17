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
package me.machinemaker.papertweaks.modules.survival.trackstats;

import com.google.inject.Inject;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.Command;
import org.incendo.cloud.minecraft.extras.RichDescription;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static org.incendo.cloud.parser.ParserDescriptor.parserDescriptor;

@ModuleCommand.Info(value = "trackstats", aliases = {"tstats", "ts"}, i18n = "track-stats", perm = "trackstats")
class Commands extends ConfiguredModuleCommand {

    private final Scoreboard board;

    @Inject
    Commands(final Scoreboard board) {
        this.board = board;
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        this.register(
            this.literal(builder, "show")
                .required("stat", parserDescriptor(new CalculatedStatParser<>(), CalculatedStat.class), RichDescription.translatable("modules.track-stats.commands.arguments.stat"))
                .handler(this.sync((context, player) -> {
                    player.setScoreboard(this.board);
                    final CalculatedStat stat = context.get("stat");
                    if (stat.getObjective(this.board).getDisplaySlot() == DisplaySlot.SIDEBAR) {
                        context.sender().sendMessage(translatable("modules.track-stats.commands.show.already-displayed", YELLOW, translatable(stat, GOLD)));
                    } else {
                        stat.getObjective(this.board).setDisplaySlot(DisplaySlot.SIDEBAR);
                        context.sender().sendMessage(translatable("modules.track-stats.commands.show.success", GREEN, translatable(stat, GOLD)));
                    }
                }))
        );
        this.register(
            this.literal(builder, "clear")
                .handler(this.sync((context, player) -> {
                    final @Nullable Objective currentlyDisplayed = this.board.getObjective(DisplaySlot.SIDEBAR);
                    if (currentlyDisplayed == null || !Stats.REGISTRY.containsKey(currentlyDisplayed.getName())) {
                        context.sender().sendMessage(translatable("modules.track-stats.commands.clear.no-display", YELLOW));
                    } else {
                        currentlyDisplayed.setDisplaySlot(null);
                        context.sender().sendMessage(translatable("modules.track-stats.commands.clear.success", GREEN, translatable(Stats.REGISTRY.get(currentlyDisplayed.getName()), GOLD)));
                    }
                }))
        );
    }
}
