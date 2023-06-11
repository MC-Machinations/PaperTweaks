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
package me.machinemaker.papertweaks.modules.teleportation.back;

import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import com.google.inject.Inject;
import me.machinemaker.papertweaks.cloud.cooldown.CommandCooldown;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.utils.PTUtils;
import org.bukkit.Location;

import java.time.Duration;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@ModuleCommand.Info(value = "back", i18n = "back", perm = "back", infoOnRoot = false)
class Commands extends ConfiguredModuleCommand {

    static final CloudKey<Void> BACK_COMMAND_COOLDOWN_KEY = SimpleCloudKey.of("vanillatweaks:back_cmd_cooldown");

    private final Config config;

    @Inject
    Commands(Config config) {
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        var builder = this.player();

        final var backCooldown = CommandCooldown.<CommandDispatcher>builder(context -> Duration.ofSeconds(this.config.cooldown))
                        .key(BACK_COMMAND_COOLDOWN_KEY)
                        .notifier((context, cooldown, secondsLeft) -> context.getCommandContext().getSender().sendMessage(translatable("modules.back.commands.root.cooldown", RED, text(secondsLeft))))
                        .build();

        manager.command(backCooldown.applyTo(builder)
                .permission(modulePermission("vanillatweaks.back"))
                .handler(sync((context, player) -> {
                    Location loc = Back.BACK_LOCATION.getFrom(player);
                    if (loc == null) {
                        context.getSender().sendMessage(translatable("modules.back.commands.root.fail.no-loc", RED));
                        return;
                    }
                    loc = PTUtils.toCenter(loc, false);
                    context.getSender().sendMessage(translatable("modules.back.commands.root.success", GOLD));
                    if (this.config.delay > 0) {
                        new BackTeleportRunnable(player, loc, this.config.delay * 20L, context.getSender()).start();
                    } else {
                        Back.setBackLocation(player, player.getLocation());
                        if (loc.getChunk().isLoaded()) {
                            player.teleport(loc);
                        } else {
                            player.teleportAsync(loc);
                        }
                    }
                }))
        );

        this.config.createCommands(this, builder);
    }


}
