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
package me.machinemaker.vanillatweaks.modules.teleportation.spawn;

import cloud.commandframework.Command;
import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import io.papermc.lib.PaperLib;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.cooldown.CooldownBuilder;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ModuleCommand {

    static final Map<UUID, BukkitTask> AWAITING_TELEPORT = Maps.newHashMap();

    private final JavaPlugin plugin;
    private final Config config;
    private final BukkitAudiences audiences;

    @Inject
    Commands(JavaPlugin plugin, Config config, BukkitAudiences audiences) {
        this.plugin = plugin;
        this.config = config;
        this.audiences = audiences;
    }

    private @MonotonicNonNull Command<CommandDispatcher> spawnCommand;
    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        var builder = manager.commandBuilder("spawn", RichDescription.translatable("modules.spawn.commands.root"))
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.spawn.current"))
                .senderType(PlayerCommandDispatcher.class);

        var cooldownBuilder = CooldownBuilder.<CommandDispatcher>builder(context -> Duration.ofSeconds(this.config.cooldown))
                .withNotifier((context, cooldown, secondsLeft) -> context.getCommandContext().getSender().sendMessage(translatable("modules.spawn.teleporting.cooldown", RED, text(secondsLeft))));

        spawnCommand = cooldownBuilder.applyTo(builder)
                .argument(WorldArgument.<CommandDispatcher>newBuilder("world").withDefaultDescription(RichDescription.translatable("modules.spawn.commands.other")).asOptional())
                .handler(commandContext -> {
                    manager.taskRecipe().begin(commandContext).synchronous((context) -> {
                        Player player = PlayerCommandDispatcher.from(context);
                        if (AWAITING_TELEPORT.containsKey(player.getUniqueId())) return;
                        World world = context.getOrDefault("world", player.getWorld());
                        context.getSender().sendMessage(translatable("modules.spawn.teleporting", GOLD));
                        if (this.config.delay > 0) {
                            AWAITING_TELEPORT.put(player.getUniqueId(), new TeleportRunnable(this.spawnCommand, player, audiences.player(player), world.getSpawnLocation(), this.config.delay * 20, (p) -> AWAITING_TELEPORT.remove(p.getUniqueId())).runTaskTimer(this.plugin, 1L, 1L));
                        } else {
                            PaperLib.teleportAsync(player, world.getSpawnLocation());
                        }
                    }).execute();
                }).build();
        manager.command(spawnCommand);
    }
}
