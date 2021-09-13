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
package me.machinemaker.vanillatweaks.modules.survival.trackstats;

import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ModuleCommand {

    private final Scoreboard board;

    @Inject
    Commands(Scoreboard board) {
        this.board = board;
    }

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        var builder = manager.commandBuilder("trackstats", RichDescription.translatable("modules.track-stats.commands.root"), "tstats", "ts")
                .permission(ModulePermission.of(lifecycle))
                .senderType(PlayerCommandDispatcher.class);

        manager.command(builder
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.trackstats.show"))
                .literal("display", RichDescription.translatable("modules.track-stats.commands.show"))
                .argument(EnumArgument.of(Stat.class, "stat"), RichDescription.translatable("modules.track-stats.commands.arguments.stat"))
                .handler(sync((context, player) -> {
                    player.setScoreboard(this.board);
                    Stat stat = context.get("stat");
                    if (this.board.getObjective(stat.objName()).getDisplaySlot() == DisplaySlot.SIDEBAR) {
                        context.getSender().sendMessage(translatable("modules.track-stats.commands.show.already-displayed", YELLOW, translatable(stat, GOLD)));
                    } else {
                        this.board.getObjective(stat.objName()).setDisplaySlot(DisplaySlot.SIDEBAR);
                        context.getSender().sendMessage(translatable("modules.track-stats.commands.show.success", GREEN, translatable(stat, GOLD)));
                    }
                }))
        ).command(builder
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.trackstats.clear"))
                .literal("clear", RichDescription.translatable("modules.track-stats.commands.clear"))
                .handler(sync((context, player) -> {
                    Objective currentlyDisplayed = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(DisplaySlot.SIDEBAR);
                    if (currentlyDisplayed == null) {
                        context.getSender().sendMessage(translatable("modules.track-stats.commands.clear.no-display", YELLOW));
                    } else {
                        currentlyDisplayed.setDisplaySlot(null);
                        context.getSender().sendMessage(translatable("modules.track-stats.commands.clear.success", GREEN, translatable(Objects.requireNonNull(Stat.OBJ_NAMES.value(currentlyDisplayed.getName())), GOLD)));
                    }
                })
        ));
    }
}
