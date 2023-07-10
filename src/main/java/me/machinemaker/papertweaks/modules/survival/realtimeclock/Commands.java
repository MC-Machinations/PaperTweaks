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
package me.machinemaker.papertweaks.modules.survival.realtimeclock;

import cloud.commandframework.Command;
import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import net.kyori.adventure.audience.Audience;
import org.bukkit.World;
import org.bukkit.entity.Player;

@ModuleCommand.Info(value = "gametime", aliases = {"gtime", "gt"}, descriptionKey = "modules.real-time-clock.commands.root", miniMessage = true, infoOnRoot = false)
class Commands extends ModuleCommand {

    private final MessageService messageService;

    @Inject
    Commands(final MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.builder();

        this.register(builder
            .permission(this.modulePermission("vanillatweaks.realtimeclock.local"))
            .senderType(PlayerCommandDispatcher.class)
            .handler(context -> {
                final Player player = PlayerCommandDispatcher.from(context);
                final Duration duration = Duration.of(player.getWorld().getGameTime() / 20, ChronoUnit.SECONDS);
                this.sendGameTime(context.getSender(), duration, player.getWorld());
            })
        );
        this.register(builder
            .permission(this.modulePermission("vanillatweaks.realtimeclock.other"))
            .argument(WorldArgument.of("world"), RichDescription.translatable("modules.real-time-clock.commands.specific-world"))
            .handler(context -> {
                final World world = context.get("world");
                final Duration duration = Duration.of(world.getGameTime() / 20, ChronoUnit.SECONDS);
                this.sendGameTime(context.getSender(), duration, world);
            })
        );
    }

    private void sendGameTime(final Audience audience, final Duration duration, final World world) {
        final int seconds = duration.toSecondsPart();
        final int minutes = duration.toMinutesPart();
        if (duration.toDaysPart() > 0) {
            this.messageService.showTimeDays(audience, duration.toDaysPart(), duration.toHoursPart(), minutes, seconds, world);
        } else if (duration.toHoursPart() > 0) {
            this.messageService.showTimeHours(audience, duration.toHoursPart(), minutes, seconds, world);
        } else {
            this.messageService.showTimeMinutes(audience, minutes, seconds, world);
        }
    }
}
