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
package me.machinemaker.papertweaks.modules.survival.trackrawstats;

import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.utils.boards.Scoreboards;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.Command;
import org.incendo.cloud.minecraft.extras.RichDescription;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static org.incendo.cloud.parser.ParserDescriptor.parserDescriptor;

@ModuleCommand.Info(value = "trackrawstats", aliases = {"trackrs", "trs"}, i18n = "track-raw-stats", perm = "trackrawstats")
class Commands extends ConfiguredModuleCommand {

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        this.register(
            this.literal(builder, "display")
                .required("objective", parserDescriptor(new ObjectiveParser<>(), Tracked.class), RichDescription.translatable("modules.track-raw-stats.commands.arguments.objective"))
                .handler(this.sync((context, player) -> {
                    player.setScoreboard(Scoreboards.main());
                    final Tracked tracked = context.get("objective");
                    if (tracked.objective().getDisplaySlot() == DisplaySlot.SIDEBAR) {
                        context.sender().sendMessage(translatable("modules.track-raw-stats.commands.display.already-displayed", YELLOW, tracked));
                    } else {
                        tracked.objective().setDisplaySlot(DisplaySlot.SIDEBAR);
                        context.sender().sendMessage(translatable("modules.track-raw-stats.commands.display.success", GREEN, tracked));
                    }
                }))
        );
        this.register(
            this.literal(builder, "clear")
                .handler(this.sync(context -> {
                    final @Nullable Objective currentlyDisplayed = Scoreboards.main().getObjective(DisplaySlot.SIDEBAR);
                    if (currentlyDisplayed == null || !RawStats.OBJECTIVE_DATA.containsKey(currentlyDisplayed.getName())) {
                        context.sender().sendMessage(translatable("modules.track-raw-stats.commands.clear.no-display", YELLOW));
                    } else {
                        currentlyDisplayed.setDisplaySlot(null);
                        context.sender().sendMessage(translatable("modules.track-raw-stats.commands.clear.success", GREEN, RawStats.OBJECTIVE_DATA.get(currentlyDisplayed.getName())));
                    }
                }))
        );
    }
}
