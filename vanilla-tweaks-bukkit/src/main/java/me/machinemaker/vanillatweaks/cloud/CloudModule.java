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

import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import me.machinemaker.vanillatweaks.cloud.cooldown.CommandCooldownManager;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcherFactory;
import me.machinemaker.vanillatweaks.cloud.processors.post.GamemodePostprocessor;
import me.machinemaker.vanillatweaks.modules.ModuleManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.TextComponent.ofChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class CloudModule extends AbstractModule {

    private static final String CLOUD_PKG = "me.machinemaker.vanillatweaks.cloud";
    private static final String PARSER_PKG = CLOUD_PKG + ".parsers";
    private static final String PARSER_ANNOTATION = CLOUD_PKG + ".CloudParser";

    private final JavaPlugin plugin;
    private final ScheduledExecutorService executorService;
    private final Set<Class<? extends ArgumentParser<CommandDispatcher, ?>>> parsers;

    @SuppressWarnings("unchecked")
    public CloudModule(JavaPlugin plugin, ScheduledExecutorService executorService) {
        this.plugin = plugin;
        this.executorService = executorService;
        this.parsers = Sets.newHashSet();
        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().acceptPackages(PARSER_PKG).scan()) {
            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(PARSER_ANNOTATION)) {
                this.parsers.add((Class<? extends ArgumentParser<CommandDispatcher, ?>>) classInfo.loadClass());
            }
        }
    }

    @Override
    protected void configure() {
        parsers.forEach(parserClass -> this.bind(parserClass).in(Scopes.SINGLETON));
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
    PaperCommandManager<CommandDispatcher> paperCommandManager(CommandDispatcherFactory commandDispatcherFactory, ModuleManager moduleManager, CommandCooldownManager<CommandDispatcher, UUID> commandCooldownManager, MinecraftExceptionHandler<CommandDispatcher> minecraftExceptionHandler) {
        final LoadingCache<CommandSender, CommandDispatcher> senderCache = CacheBuilder.newBuilder().expireAfterAccess(20, TimeUnit.MINUTES).build(commandDispatcherFactory);
        try {
            PaperCommandManager<CommandDispatcher> commandManager = new PaperCommandManager<>(
                    plugin,
                    AsynchronousCommandExecutionCoordinator.<CommandDispatcher>newBuilder().build(),
                    commandSender -> {
                        try {
                            return senderCache.get(commandSender);
                        } catch (ExecutionException e) {
                            throw new RuntimeException("Error mapping command sender", e);
                        }
                    },
                    CommandDispatcher::sender
            );
            if (commandManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                commandManager.registerAsynchronousCompletions();
            }

            if (commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
                commandManager.registerBrigadier();
            }

            commandManager.parameterInjectorRegistry().registerInjector(ModuleManager.class, (context, annotationAccessor) -> moduleManager);

            commandCooldownManager.registerCooldownManager(commandManager);

            commandManager.registerCommandPostProcessor(new GamemodePostprocessor());

            minecraftExceptionHandler.apply(commandManager, AudienceProvider.nativeAudience());

            return commandManager;
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
                .withDecorator(component -> ofChildren(
                        text("[", DARK_GRAY),
                        text("VanillaTweaks", BLUE),
                        text("] ", DARK_GRAY),
                        component
                ));
    }
}
