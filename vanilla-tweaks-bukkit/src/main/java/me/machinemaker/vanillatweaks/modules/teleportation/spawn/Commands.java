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
package me.machinemaker.vanillatweaks.modules.teleportation.spawn;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import io.papermc.lib.PaperLib;
import me.machinemaker.vanillatweaks.cloud.cooldown.CooldownBuilder;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.teleportation.back.Back;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@ModuleCommand.Info(value = "spawn", descriptionKey = "modules.spawn.commands.root", infoOnRoot = false)
class Commands extends ModuleCommand {

    private static final CommandArgument<CommandDispatcher, World> WORLD_ARG = WorldArgument.of("world");

    static final Map<UUID, BukkitTask> AWAITING_TELEPORT = Maps.newHashMap();
    static final CloudKey<Void> SPAWN_CMD_COOLDOWN_KEY = SimpleCloudKey.of("vanillatweaks:spawn_cmd_cooldown");

    private final JavaPlugin plugin;
    private final Config config;
    @Inject
    Commands(JavaPlugin plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        var builder = this.player();

        final var cooldownBuilder = CooldownBuilder.<CommandDispatcher>builder(context -> Duration.ofSeconds(this.config.cooldown))
                .key(SPAWN_CMD_COOLDOWN_KEY)
                .notifier((context, cooldown, secondsLeft) -> context.getCommandContext().getSender().sendMessage(translatable("modules.spawn.teleporting.cooldown", RED, text(secondsLeft))));


        this.manager.command(cooldownBuilder.applyTo(builder)
                .permission(modulePermission("vanillatweaks.spawn.current"))
                .handler(handleSpawnCmd())
        ).command(cooldownBuilder.applyTo(builder)
                .permission(modulePermission("vanillatweaks.spawn.other"))
                .argument(WORLD_ARG, RichDescription.translatable("modules.spawn.commands.other"))
                .handler(handleSpawnCmd())
        );
    }

    private CommandExecutionHandler<CommandDispatcher> handleSpawnCmd() {
        return sync((context, player) -> {
            if (AWAITING_TELEPORT.containsKey(player.getUniqueId())) {
                return;
            }
            Location spawnLoc = context.getOptional(WORLD_ARG).orElse(player.getWorld()).getSpawnLocation();
            context.getSender().sendMessage(translatable("modules.spawn.teleporting", GOLD));
            if (this.config.delay > 0) {
                AWAITING_TELEPORT.put(player.getUniqueId(), new SpawnTeleportRunnable(player, context.getSender(), spawnLoc, this.config.delay * 20, (p) -> AWAITING_TELEPORT.remove(p.getUniqueId())).runTaskTimer(this.plugin, 1L, 1L));
            } else {
                Back.setBackLocation(player, player.getLocation()); // Set back location
                if (spawnLoc.getChunk().isLoaded()) {
                    player.teleport(spawnLoc);
                } else {
                    PaperLib.teleportAsync(player, spawnLoc);
                }
            }
        });
    }
}
