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
package me.machinemaker.vanillatweaks.modules.mobs.countmobdeaths;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ModuleCommand {

    private final CountMobDeaths countMobDeaths;

    @Inject
    Commands(CountMobDeaths countMobDeaths) {
        this.countMobDeaths = countMobDeaths;
    }

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        var builder = manager.commandBuilder("countmobdeaths", RichDescription.translatable("modules.mob-death-count.commands.root"), "cmdeaths", "cmd")
                .senderType(PlayerCommandDispatcher.class)
                .permission(ModulePermission.of(lifecycle));

        manager.command(builder
                .literal("start", RichDescription.translatable("modules.mob-death-count.commands.start"))
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.mobdeathcount.start"))
                .handler(commandContext -> {
                    syncBoardHandle(commandContext, (player, context, countingBoard) -> {
                        countingBoard.setCounting(true);
                        player.setScoreboard(countingBoard.scoreboard());
                        context.getSender().sendMessage(translatable("modules.mob-death-count.started", GREEN));
                    });
                })
        ).command(builder.literal("stop", RichDescription.translatable("modules.mob-death-count.commands.stop"))
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.mobdeathcount.stop"))
                .handler(commandContext -> {
                    syncBoardHandle(commandContext, (player, context, countingBoard) -> {
                        countingBoard.setCounting(false);
                        context.getSender().sendMessage(translatable("modules.mob-death-count.stopped", YELLOW));
                    });
                })
        ).command(builder.literal("reset", RichDescription.translatable("modules.mob-death-count.commands.reset"))
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.mobdeathcount.reset"))
                .handler(commandContext -> {
                    syncBoardHandle(commandContext, (player, context, countingBoard) -> {
                        countingBoard.scoreboard().getEntries().forEach(countingBoard.scoreboard()::resetScores);
                        context.getSender().sendMessage(translatable("modules.mob-death-count.reset", GREEN));
                    });
                })
        ).command(builder.literal("toggle", RichDescription.translatable("modules.mob-death-count.commands.toggle"))
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.mobdeathcount.toggle"))
                .handler(commandContext -> {
                    syncBoardHandle(commandContext, (player, context, countingBoard) -> {
                        if (player.getScoreboard() == countingBoard.scoreboard()) {
                            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                        } else {
                            player.setScoreboard(countingBoard.scoreboard());
                        }
                    });
                })
        );
    }

    @FunctionalInterface
    interface BoardHandle {
        void handle(@NotNull Player player, @NotNull CommandContext<CommandDispatcher> context, @NotNull CountMobDeaths.CountingBoard countingBoard);
    }

    private void syncBoardHandle(@NotNull CommandContext<CommandDispatcher> commandContext, @NotNull BoardHandle boardHandle) {
        manager.taskRecipe().begin(commandContext).synchronous(context -> {
            Player player = PlayerCommandDispatcher.from(context);
            CountMobDeaths.CountingBoard board = this.countMobDeaths.getOrCreateBoard(player);
            boardHandle.handle(player, context, board);
        }).execute();
    }
}
