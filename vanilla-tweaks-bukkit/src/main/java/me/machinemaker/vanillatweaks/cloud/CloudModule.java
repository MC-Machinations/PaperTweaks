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
package me.machinemaker.vanillatweaks.cloud;

import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class CloudModule extends AbstractModule {

    private static final String CLOUD_PKG = "me.machinemaker.vanillatweaks.cloud";
    private static final String PARSER_PKG = CLOUD_PKG + ".parsers";
    private static final String PARSER_ANNOTATION = CLOUD_PKG + ".CloudParser";

    private final JavaPlugin plugin;
    private final Set<Class<? extends ArgumentParser<CommandDispatcher, ?>>> parsers;

    @SuppressWarnings("unchecked")
    public CloudModule(JavaPlugin plugin) {
        this.plugin = plugin;
        this.parsers = Sets.newHashSet();
        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().acceptPackages(PARSER_PKG).scan()) {
            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(PARSER_ANNOTATION)) {
                this.parsers.add((Class<? extends ArgumentParser<CommandDispatcher, ?>>) classInfo.loadClass());
            }
        }
    }

    @Override
    protected void configure() {
        requestStaticInjection(ModuleCommand.class);
        parsers.forEach(parserClass -> this.bind(parserClass).in(Scopes.SINGLETON));
    }

    @Provides
    @Singleton
    PaperCommandManager<CommandDispatcher> paperCommandManager(CommandDispatcherFactory commandDispatcherFactory, ModuleManager moduleManager) {
        try {
            PaperCommandManager<CommandDispatcher> commandManager = new PaperCommandManager<>(
                    plugin,
                    AsynchronousCommandExecutionCoordinator.<CommandDispatcher>newBuilder().build(),
                    commandDispatcherFactory::from,
                    CommandDispatcher::sender
            );
            if (commandManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                commandManager.registerAsynchronousCompletions();
            }

            if (commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
                commandManager.registerBrigadier();
            }

            commandManager.parameterInjectorRegistry().registerInjector(ModuleManager.class, (context, annotationAccessor) -> moduleManager);

            return commandManager;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize cloud!", e);
        }
    }
}
