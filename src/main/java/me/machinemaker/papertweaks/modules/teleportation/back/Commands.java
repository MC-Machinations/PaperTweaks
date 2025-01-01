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
package me.machinemaker.papertweaks.modules.teleportation.back;

import com.google.inject.Inject;
import java.time.Duration;
import me.machinemaker.papertweaks.cloud.cooldown.CommandCooldown;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.utils.PTUtils;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.Command;
import org.incendo.cloud.key.CloudKey;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static org.incendo.cloud.key.CloudKey.cloudKey;

@ModuleCommand.Info(value = "back", i18n = "back", perm = "back", infoOnRoot = false)
class Commands extends ConfiguredModuleCommand {

    static final CloudKey<Void> BACK_COMMAND_COOLDOWN_KEY = cloudKey("papertweaks:back_cmd_cooldown");

    private final Config config;

    @Inject
    Commands(final Config config) {
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        final CommandCooldown<CommandDispatcher> backCooldown = CommandCooldown.<CommandDispatcher>builder(context -> Duration.ofSeconds(this.config.cooldown))
            .key(BACK_COMMAND_COOLDOWN_KEY)
            .notifier((context, cooldown, secondsLeft) -> context.commandContext().sender().sendMessage(translatable("modules.back.commands.root.cooldown", RED, text(secondsLeft))))
            .build();

        this.register(builder
            .apply(backCooldown)
            .permission(this.modulePermission("vanillatweaks.back"))
            .handler(this.sync((context, player) -> {
                if (BackTeleportRunnable.AWAITING_TELEPORT.containsKey(player.getUniqueId())) {
                    return;
                }
                @Nullable Location loc = Back.BACK_LOCATION.getFrom(player);
                if (loc == null) {
                    context.sender().sendMessage(translatable("modules.back.commands.root.fail.no-loc", RED));
                    return;
                }
                loc = PTUtils.toCenter(loc, false);
                context.sender().sendMessage(translatable("modules.back.commands.root.success", GOLD));
                if (this.config.delay > 0) {
                    new BackTeleportRunnable(player, loc, this.config.delay * 20L, context.sender()).start();
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
