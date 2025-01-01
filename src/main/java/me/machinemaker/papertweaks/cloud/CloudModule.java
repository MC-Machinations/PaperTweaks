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
package me.machinemaker.papertweaks.cloud;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.leangen.geantyref.TypeToken;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import me.machinemaker.papertweaks.cloud.cooldown.CommandCooldownManager;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcherFactory;
import me.machinemaker.papertweaks.cloud.parsers.ParserFactory;
import me.machinemaker.papertweaks.cloud.parsers.PseudoEnumParser;
import me.machinemaker.papertweaks.cloud.processors.ConditionalCaseInsensitiveSuggestionProcessor;
import me.machinemaker.papertweaks.cloud.processors.post.GamemodePostprocessor;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.brigadier.CloudBrigadierManager;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.paper.PaperCommandManager;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class CloudModule extends AbstractModule {

    private final JavaPlugin plugin;
    private final ScheduledExecutorService executorService;

    public CloudModule(final JavaPlugin plugin, final ScheduledExecutorService executorService) {
        this.plugin = plugin;
        this.executorService = executorService;
    }

    @Override
    protected void configure() {
        this.install(new FactoryModuleBuilder().build(ParserFactory.class));
    }

    @Provides
    @Singleton
    CommandCooldownManager<CommandDispatcher, UUID> commandCooldownManager() {
        return CommandCooldownManager.create(
            CommandDispatcher::getUUID,
            (context, cooldown, secondsLeft) -> context.commandContext().sender().sendMessage(text("Cooling down", RED)),
            this.executorService);
    }

    @Provides
    @Singleton
    PaperCommandManager<CommandDispatcher> paperCommandManager(final CommandDispatcherFactory commandDispatcherFactory,
                                                               final CommandCooldownManager<CommandDispatcher, UUID> commandCooldownManager,
                                                               final MinecraftExceptionHandler<CommandDispatcher> minecraftExceptionHandler) {
        try {
            final PaperCommandManager<CommandDispatcher> manager = PaperCommandManager.builder(
                SenderMapper.create(
                    commandDispatcherFactory::from,
                    CommandDispatcher::sourceStack
                )
            ).executionCoordinator(ExecutionCoordinator.asyncCoordinator()).buildOnEnable(this.plugin);

            minecraftExceptionHandler.registerTo(manager);
            commandCooldownManager.registerCooldownManager(manager);
            manager.registerCommandPostProcessor(new GamemodePostprocessor());
            manager.suggestionProcessor(ConditionalCaseInsensitiveSuggestionProcessor.instance());

            final CloudBrigadierManager<CommandDispatcher, ?> brigManager = manager.brigadierManager();
            brigManager.registerMapping(new TypeToken<PseudoEnumParser<CommandDispatcher>>() {}, builder -> {
                builder.cloudSuggestions().to(argument -> switch (argument.getStringMode()) {
                    case QUOTED -> StringArgumentType.string();
                    case GREEDY -> StringArgumentType.greedyString();
                    default -> StringArgumentType.word();
                });
            });

            return manager;
        } catch (final Exception e) {
            throw new RuntimeException("Failed to initialize cloud!", e);
        }
    }

    @Provides
    @Singleton
    private MinecraftExceptionHandler<CommandDispatcher> minecraftExceptionHandler() {
        return MinecraftExceptionHandler.<CommandDispatcher>createNative()
            .defaultArgumentParsingHandler()
            .defaultCommandExecutionHandler()
            .defaultInvalidSenderHandler()
            .defaultInvalidSyntaxHandler()
            .defaultNoPermissionHandler();
    }
}
