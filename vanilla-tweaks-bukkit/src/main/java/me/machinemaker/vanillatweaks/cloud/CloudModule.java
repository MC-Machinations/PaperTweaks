/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.StaticArgument;
import cloud.commandframework.brigadier.CloudBrigadierManager;
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
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.leangen.geantyref.TypeToken;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import me.machinemaker.mirror.FieldAccessor;
import me.machinemaker.mirror.MethodInvoker;
import me.machinemaker.mirror.Mirror;
import me.machinemaker.mirror.paper.PaperMirror;
import me.machinemaker.vanillatweaks.cloud.arguments.ArgumentFactory;
import me.machinemaker.vanillatweaks.cloud.arguments.PseudoEnumArgument;
import me.machinemaker.vanillatweaks.cloud.cooldown.CommandCooldownManager;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcherFactory;
import me.machinemaker.vanillatweaks.cloud.processors.SimpleSuggestionProcessor;
import me.machinemaker.vanillatweaks.cloud.processors.post.GamemodePostprocessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class CloudModule extends AbstractModule {

    private static final Class<?> VANILLA_COMMAND_WRAPPER_CLASS = PaperMirror.getCraftBukkitClass("command.VanillaCommandWrapper");
    private static final MethodInvoker.Typed<SimpleCommandMap> CRAFT_SERVER_GET_COMMAND_MAP = Mirror.typedFuzzyMethod(PaperMirror.CRAFT_SERVER_CLASS, SimpleCommandMap.class).names("getCommandMap").find();
    private static final FieldAccessor.Typed<Map<String, org.bukkit.command.Command>> COMMAND_MAP_KNOWN_COMMANDS_FIELD = Mirror.typedFuzzyField(SimpleCommandMap.class, new TypeToken<Map<String, org.bukkit.command.Command>>() {}).names("knownCommands").find();

    private static Map<String, org.bukkit.command.Command> getCommandMap() {
        return COMMAND_MAP_KNOWN_COMMANDS_FIELD.get(CRAFT_SERVER_GET_COMMAND_MAP.invoke(Bukkit.getServer()));
    }

    private final JavaPlugin plugin;
    private final ScheduledExecutorService executorService;

    public CloudModule(JavaPlugin plugin, ScheduledExecutorService executorService) {
        this.plugin = plugin;
        this.executorService = executorService;
    }

    @Override
    protected void configure() {
        this.install(new FactoryModuleBuilder().build(ArgumentFactory.class));
    }

    @Provides
    @Singleton
    CommandCooldownManager<CommandDispatcher, UUID> commandCooldownManager() {
        return CommandCooldownManager.create(
                CommandDispatcher::getUUID,
                (context, cooldown, secondsLeft) -> context.getCommandContext().getSender().sendMessage(text("Cooling down", RED)),
                executorService);
    }

    @Provides
    @Singleton
    PaperCommandManager<CommandDispatcher> paperCommandManager(CommandDispatcherFactory commandDispatcherFactory,
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
            ) {
                @Override
                public @NonNull CommandManager<CommandDispatcher> command(@NonNull Command<CommandDispatcher> command) {
                    if (command.getArguments().get(0) instanceof StaticArgument<?> staticArgument) {
                        String main = staticArgument.getName();
                        if (VANILLA_COMMAND_WRAPPER_CLASS.isInstance(getCommandMap().get(main))) {
                            getCommandMap().remove(main);
                        }
                    }
                    return super.command(command);
                }
            };
            if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                manager.registerAsynchronousCompletions();
            }

            if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER) || manager.hasCapability(CloudBukkitCapabilities.COMMODORE_BRIGADIER)) {
                manager.registerBrigadier();
            }

            minecraftExceptionHandler.apply(manager, AudienceProvider.nativeAudience());
            commandCooldownManager.registerCooldownManager(manager);
            manager.registerCommandPostProcessor(new GamemodePostprocessor());
            manager.commandSuggestionProcessor(new SimpleSuggestionProcessor());

            final @Nullable CloudBrigadierManager<CommandDispatcher, ?> brigManager = manager.brigadierManager();
            if (brigManager != null) {
                brigManager.registerMapping(new TypeToken<PseudoEnumArgument.PseudoEnumParser<CommandDispatcher>>() {}, builder -> {
                    builder.cloudSuggestions().to(argument -> switch (argument.getStringMode()) {
                        case QUOTED -> StringArgumentType.string();
                        case GREEDY -> StringArgumentType.greedyString();
                        default -> StringArgumentType.word();
                    });
                });
            }

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
                .withNoPermissionHandler();
    }
}
