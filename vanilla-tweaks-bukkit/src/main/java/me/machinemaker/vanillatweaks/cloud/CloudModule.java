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
package me.machinemaker.vanillatweaks.cloud;

import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.cloud.cooldown.CommandCooldownManager;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcherFactory;
import me.machinemaker.vanillatweaks.cloud.processors.post.GamemodePostprocessor;
import me.machinemaker.vanillatweaks.modules.ModuleManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.machinemaker.vanillatweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class CloudModule extends AbstractModule {

    private final JavaPlugin plugin;
    private final ScheduledExecutorService executorService;

    public CloudModule(JavaPlugin plugin, ScheduledExecutorService executorService) {
        this.plugin = plugin;
        this.executorService = executorService;
    }

    @Provides
    @Singleton
    CommandCooldownManager<CommandDispatcher, UUID> commandCooldownManager() {
        return new CommandCooldownManager<>(
                CommandDispatcher::getUUID,
                (context, cooldown, secondsLeft) -> context.getCommandContext().getSender().sendMessage(text("Cooling down", RED)),
                executorService);
    }

    @Provides
    @Singleton
    PaperCommandManager<CommandDispatcher> paperCommandManager(CommandDispatcherFactory commandDispatcherFactory,
                                                               ModuleManager moduleManager,
                                                               CommandCooldownManager<CommandDispatcher, UUID> commandCooldownManager,
                                                               MinecraftExceptionHandler<CommandDispatcher> minecraftExceptionHandler) {
        final LoadingCache<CommandSender, CommandDispatcher> senderCache = CacheBuilder.newBuilder().expireAfterAccess(20, TimeUnit.MINUTES).build(commandDispatcherFactory);
        try {
            final PaperCommandManager<CommandDispatcher> manager = new PaperCommandManager<>(
                    plugin,
                    AsynchronousCommandExecutionCoordinator.<CommandDispatcher>newBuilder().build(),
                    commandSender -> {
                        try {
                            return senderCache.get(commandSender);
                        } catch (ExecutionException e) {
                            throw new IllegalArgumentException("Error mapping command sender", e);
                        }
                    },
                    CommandDispatcher::sender
            );
            if (manager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                manager.registerAsynchronousCompletions();
            }

            if (manager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
                manager.registerBrigadier();
            }

            manager.parameterInjectorRegistry().registerInjector(ModuleManager.class, (context, annotationAccessor) -> moduleManager);

            minecraftExceptionHandler.apply(manager, AudienceProvider.nativeAudience());
            commandCooldownManager.registerCooldownManager(manager);
            manager.registerCommandPostProcessor(new GamemodePostprocessor());

            return manager;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize cloud!", e);
        }
    }

    @Provides
    @Singleton
    private MinecraftExceptionHandler<CommandDispatcher> minecraftExceptionHandler() {
        return new MinecraftExceptionHandler<CommandDispatcher>()
                .withArgumentParsingHandler()
                .withCommandExecutionHandler()
                .withInvalidSenderHandler()
                .withInvalidSyntaxHandler()
                .withNoPermissionHandler()
                .withDecorator(component -> join(VanillaTweaks.PLUGIN_PREFIX, component));
    }
}
