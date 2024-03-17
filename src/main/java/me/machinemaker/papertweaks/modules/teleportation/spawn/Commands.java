/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.modules.teleportation.spawn;

import com.google.inject.Inject;
import java.time.Duration;
import me.machinemaker.papertweaks.cloud.cooldown.CommandCooldown;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.teleportation.back.Back;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.incendo.cloud.Command;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.minecraft.extras.RichDescription;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static org.incendo.cloud.bukkit.parser.WorldParser.worldParser;
import static org.incendo.cloud.key.CloudKey.cloudKey;

@ModuleCommand.Info(value = "spawn", descriptionKey = "modules.spawn.commands.root", infoOnRoot = false)
class Commands extends ModuleCommand {

    static final CloudKey<Void> SPAWN_CMD_COOLDOWN_KEY = cloudKey("papertweaks:spawn_cmd_cooldown");
    private final Config config;

    @Inject
    Commands(final Config config) {
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        final CommandCooldown<CommandDispatcher> commandCooldown = CommandCooldown.<CommandDispatcher>builder(context -> Duration.ofSeconds(this.config.cooldown))
            .key(SPAWN_CMD_COOLDOWN_KEY)
            .notifier((context, cooldown, secondsLeft) -> context.commandContext().sender().sendMessage(translatable("modules.spawn.teleporting.cooldown", RED, text(secondsLeft))))
            .build();


        this.register(builder
            .apply(commandCooldown)
            .permission(this.modulePermission("vanillatweaks.spawn.current"))
            .handler(this.handleSpawnCmd())
        );
        this.register(builder
            .apply(commandCooldown)
            .permission(this.modulePermission("vanillatweaks.spawn.other"))
            .required("world", worldParser(), RichDescription.translatable("modules.spawn.commands.other"))
            .handler(this.handleSpawnCmd())
        );
    }

    private CommandExecutionHandler<CommandDispatcher> handleSpawnCmd() {
        return this.sync((context, player) -> {
            if (SpawnTeleportRunnable.AWAITING_TELEPORT.containsKey(player.getUniqueId())) {
                return;
            }
            final Location spawnLoc = context.<World>optional("world").orElse(this.config.defaultsToMainWorld ? Bukkit.getWorlds().get(0) : player.getWorld()).getSpawnLocation();
            context.sender().sendMessage(translatable("modules.spawn.teleporting", GOLD));
            if (this.config.delay > 0) {
                new SpawnTeleportRunnable(player, context.sender(), spawnLoc, this.config.delay * 20).start();
            } else {
                Back.setBackLocation(player, player.getLocation()); // Set back location
                if (spawnLoc.getChunk().isLoaded()) {
                    player.teleport(spawnLoc);
                } else {
                    player.teleportAsync(spawnLoc);
                }
            }
        });
    }
}
