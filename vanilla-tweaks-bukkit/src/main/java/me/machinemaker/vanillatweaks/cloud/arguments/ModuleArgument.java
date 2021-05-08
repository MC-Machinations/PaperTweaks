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
package me.machinemaker.vanillatweaks.cloud.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.common.collect.Lists;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Queue;

import static net.kyori.adventure.text.Component.text;

public class ModuleArgument extends CommandArgument<CommandDispatcher, ModuleBase> {

    private static final String ARGUMENT_NAME = "module";

    private ModuleArgument(@Nullable Boolean enabled) {
        super(true, ARGUMENT_NAME, new Parser(enabled), "", ModuleBase.class, null, RichDescription.of(text("TODO"))); // TODO - description
    }

    public static ModuleArgument enabled() {
        return new ModuleArgument(true);
    }

    public static ModuleArgument all() {
        return new ModuleArgument(null);
    }

    public static ModuleArgument disabled() {
        return new ModuleArgument(false);
    }

    public record Parser(@Nullable Boolean enabled) implements ArgumentParser<CommandDispatcher, ModuleBase> {

        @Override
        public @NonNull
        ArgumentParseResult<@NonNull ModuleBase> parse(@NonNull CommandContext<@NonNull CommandDispatcher> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
            ModuleManager moduleManager = commandContext.inject(ModuleManager.class).orElseThrow();
            final String input = inputQueue.peek();
            Optional<ModuleLifecycle> lifecycle = moduleManager.getLifecycle(input);
            if (lifecycle.isEmpty()) {
                return ArgumentParseResult.failure(new IllegalArgumentException(input + " is not a valid module")); // TODO lang
            }
            if (this.enabled != null) {
                if (this.enabled && !lifecycle.get().getState().isRunning()) {
                    return ArgumentParseResult.failure(new IllegalArgumentException(input + " must be enabled!")); // TODO lang
                }
                if (!this.enabled && lifecycle.get().getState().isRunning()) {
                    return ArgumentParseResult.failure(new IllegalArgumentException(input + " must be disabled!")); // TODO lang
                }
            }
            inputQueue.remove();
            return ArgumentParseResult.success(moduleManager.getModules().get(input));
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<CommandDispatcher> commandContext, @NonNull String input) {
            ModuleManager manager = commandContext.inject(ModuleManager.class).orElseThrow();
            if (this.enabled == null) {
                return Lists.newArrayList(manager.getModules().keySet());
            } else if (this.enabled) {
                return manager.getModules().keySet().stream().filter(name -> manager.getLifecycle(name).map(lifecycle -> lifecycle.getState().isRunning()).orElse(false)).toList();
            } else {
                return manager.getModules().keySet().stream().filter(name -> manager.getLifecycle(name).map(lifecycle -> !lifecycle.getState().isRunning()).orElse(false)).toList();
            }
        }
    }

    public static ModuleBase getModule(CommandContext<CommandDispatcher> context) {
        return context.get(ARGUMENT_NAME);
    }
}
