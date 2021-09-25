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
package me.machinemaker.vanillatweaks.modules.teleportation.back;

import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import com.google.inject.Inject;
import io.papermc.lib.PaperLib;
import me.machinemaker.vanillatweaks.cloud.cooldown.CooldownBuilder;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ConfiguredModuleCommand;
import org.bukkit.Location;

import java.time.Duration;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ConfiguredModuleCommand {

    static final CloudKey<Void> BACK_COMMAND_COOLDOWN_KEY = SimpleCloudKey.of("vanillatweaks:back_cmd_cooldown");

    private final Config config;

    @Inject
    Commands(Config config) {
        super("back");
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        var builder = playerCmd("back", "modules.back.commands.root");

        final var backCooldownBuilder = CooldownBuilder.<CommandDispatcher>builder(context -> Duration.ofSeconds(this.config.cooldown))
                        .withKey(BACK_COMMAND_COOLDOWN_KEY)
                        .withNotifier((context, cooldown, secondsLeft) -> context.getCommandContext().getSender().sendMessage(translatable("modules.back.commands.root.cooldown", RED, text(secondsLeft))));

        manager.command(backCooldownBuilder.applyTo(builder)
                .handler(sync((context, player) -> {
                    Location loc = Back.BACK_LOCATION.getFrom(player);
                    if (loc == null) {
                        context.getSender().sendMessage(translatable("modules.back.commands.root.fail.no-loc", RED));
                        return;
                    }
                    context.getSender().sendMessage(translatable("modules.back.commands.root.success", GOLD));
                    if (this.config.delay > 0) {
                        new BackTeleportRunnable(player, loc, this.config.delay * 20L, context.getSender()).start();
                    } else {
                        PaperLib.teleportAsync(player, loc);
                    }
                }))
        );

        this.config.createCommands(this, builder);
    }
}
