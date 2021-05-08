/*
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
package me.machinemaker.vanillatweaks.modules.survival.realtimeclock;

import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.minecraft.extras.RichDescription;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.MappedTranslatableComponent.mapped;
import static net.kyori.adventure.text.MappedTranslatableComponent.mappedBuilder;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ModuleCommand {

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        var builder = manager
                .commandBuilder("gametime", RichDescription.of(mapped("modules.real-time-clock.commands.root")), "gtime", "gt")
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.realtimeclock"));

        manager.command(builder
                .senderType(PlayerCommandDispatcher.class)
                .handler(context -> {
                    Player player = PlayerCommandDispatcher.from(context);
                    Duration duration = Duration.of(player.getWorld().getGameTime() / 20, ChronoUnit.SECONDS);
                    context.getSender().sendMessage(buildRuntimeComponent(duration, player.getWorld().getName()));
                })
        ).command(builder
                .argument(WorldArgument.of("world"))
        );
    }

    private static Component buildRuntimeComponent(Duration duration, @NotNull String world) {
        var builder = mappedBuilder("modules.real-time-clock.show-time.minutes", YELLOW)
                .arg("m", duration.toMinutesPart())
                .arg("s", duration.toSecondsPart())
                .arg("world", text(world));

        if (duration.toHoursPart() > 0) {
            builder.key("modules.real-time-clock.show-time.hours").arg("h", duration.toHoursPart());
        }

        if (duration.toDaysPart() > 0) {
            builder.key("modules.real-time-clock.show-time.days").arg("d", duration.toDaysPart());
        }
        return builder.build();
    }
}
