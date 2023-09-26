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
package me.machinemaker.papertweaks.modules.mobs.countmobdeaths;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import com.google.inject.Inject;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.utils.boards.Scoreboards;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

@ModuleCommand.Info(value = "countmobdeaths", aliases = {"cmdeaths", "cmd"}, i18n = "mob-death-count", perm = "mobdeathcount")
class Commands extends ConfiguredModuleCommand {

    private final CountMobDeaths countMobDeaths;

    @Inject
    Commands(final CountMobDeaths countMobDeaths) {
        this.countMobDeaths = countMobDeaths;
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        this.register(
            this.literal(builder, "start")
            .handler(this.sync((player, context, countingBoard) -> {
                countingBoard.setCounting(true);
                player.setScoreboard(countingBoard.scoreboard());
                context.getSender().sendMessage(translatable("modules.mob-death-count.started", GREEN));
            }))
        );
        this.register(
            this.literal(builder, "stop")
            .handler(this.sync((player, context, countingBoard) -> {
                countingBoard.setCounting(false);
                context.getSender().sendMessage(translatable("modules.mob-death-count.stopped", YELLOW));
            }))
        );
        this.register(
            this.literal(builder, "reset")
            .handler(this.sync((player, context, countingBoard) -> {
                countingBoard.scoreboard().getEntries().forEach(countingBoard.scoreboard()::resetScores);
                context.getSender().sendMessage(translatable("modules.mob-death-count.reset", GREEN));
            }))
        );
        this.register(
            this.literal(builder, "toggle")
            .handler(this.sync((player, context, countingBoard) -> {
                if (player.getScoreboard() == countingBoard.scoreboard()) {
                    player.setScoreboard(Scoreboards.main());
                } else {
                    player.setScoreboard(countingBoard.scoreboard());
                }
            }))
        );
    }

    private CommandExecutionHandler<CommandDispatcher> sync(final BoardHandle boardHandle) {
        return this.sync((context, player) -> {
            final CountMobDeaths.CountingBoard board = this.countMobDeaths.getOrCreateBoard(player);
            boardHandle.handle(player, context, board);
        });
    }

    @FunctionalInterface
    interface BoardHandle {

        void handle(Player player, CommandContext<CommandDispatcher> context, CountMobDeaths.CountingBoard countingBoard);
    }
}
