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
package me.machinemaker.vanillatweaks.modules.survival.trackrawstats;

import cloud.commandframework.minecraft.extras.RichDescription;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ModuleCommand {

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        var builder = manager.commandBuilder("trackrawstats", RichDescription.translatable("modules.track-raw-stats.commands.root"), "trackrs", "trs")
                .permission(ModulePermission.of(lifecycle));

        manager.command(builder
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.trackrawstats.show"))
                .literal("display", RichDescription.translatable("modules.track-raw-stats.commands.show"))
                .argument(ObjectiveArgument.of("objective"), RichDescription.translatable("modules.track-raw-stats.commands.show"))
                .handler(sync((context, player) -> {
                    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                    Tracked tracked = context.get("objective");
                    if (tracked.objective().getDisplaySlot() == DisplaySlot.SIDEBAR) {
                        context.getSender().sendMessage(translatable("modules.track-raw-stats.commands.show.already-displayed", YELLOW, tracked));
                    } else {
                        tracked.objective().setDisplaySlot(DisplaySlot.SIDEBAR);
                        context.getSender().sendMessage(translatable("modules.track-raw-stats.commands.show.success", GREEN, tracked));
                    }
                }))
        ).command(builder
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.trackrawstats.clear"))
                .literal("clear", RichDescription.translatable("modules.track-raw-stats.commands.clear"))
                .handler(sync(context -> {
                    Objective currentlyDisplayed = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(DisplaySlot.SIDEBAR);
                    if (currentlyDisplayed == null || !RawStats.OBJECTIVE_DATA.containsKey(currentlyDisplayed.getName())) {
                        context.getSender().sendMessage(translatable("modules.track-raw-stats.commands.clear.no-display", YELLOW));
                    } else {
                        currentlyDisplayed.setDisplaySlot(null);
                        context.getSender().sendMessage(translatable("modules.track-raw-stats.commands.clear.success", GREEN, RawStats.OBJECTIVE_DATA.get(currentlyDisplayed.getName())));
                    }
                }))
        );
    }
}
