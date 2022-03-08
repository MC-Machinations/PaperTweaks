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
package me.machinemaker.vanillatweaks.cloud.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.processors.SimpleSuggestionProcessor;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Predicate;

@DefaultQualifier(NonNull.class)
public class ModuleArgument extends CommandArgument<CommandDispatcher, ModuleBase> {

    private static final String ARGUMENT_NAME = "module";

    @Inject
    private ModuleArgument(ModuleManager manager, @Assisted @Nullable Boolean enabled) {
        super(true, ARGUMENT_NAME, new Parser(enabled, manager), "", ModuleBase.class, null, RichDescription.translatable("commands.arguments.module"));
    }

    private record Parser(@Nullable Boolean enabled, ModuleManager manager) implements ArgumentParser<CommandDispatcher, ModuleBase> {

        @Override
        public ArgumentParseResult<ModuleBase> parse(CommandContext<CommandDispatcher> commandContext, Queue<String> inputQueue) {
            final String input = inputQueue.peek();
            Optional<ModuleLifecycle> lifecycle = this.manager.getLifecycle(input);
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
            try {
                return ArgumentParseResult.success(this.manager.getModule(input).orElseThrow());
            } catch (NoSuchElementException exception) {
                return ArgumentParseResult.failure(exception);
            }
        }

        @Override
        public List<String> suggestions(CommandContext<CommandDispatcher> commandContext, String input) {
            commandContext.set(SimpleSuggestionProcessor.IGNORE_CASE, true);
            final List<String> modules = new ArrayList<>();
            final Predicate<ModuleLifecycle> lifecyclePredicate = predicateFor(this.enabled);
            for (final ModuleBase module : this.manager.getModules().values()) {
                this.manager.getLifecycle(module.getName()).ifPresent(lifecycle -> {
                    if (lifecyclePredicate.test(lifecycle)) {
                        modules.add(module.getName());
                    }
                });
            }
            return modules;
        }
    }

    public static ModuleBase getModule(CommandContext<CommandDispatcher> context) {
        return context.get(ARGUMENT_NAME);
    }

    private static Predicate<ModuleLifecycle> predicateFor(@Nullable Boolean enabled) {
        if (enabled == null) {
            return lifecycle -> true;
        } else if (enabled) {
            return lifecycle -> lifecycle.getState().isRunning();
        } else {
            return lifecycle -> !lifecycle.getState().isRunning();
        }
    }
}
