/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2024 Machine_Maker
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
package me.machinemaker.papertweaks.cloud.parsers;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.processors.ConditionalCaseInsensitiveSuggestionProcessor;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.SuggestionProvider;

public class ModuleParser implements ArgumentParser<CommandDispatcher, ModuleBase> {

    private final ModuleManager manager;
    private final @Nullable Boolean enabled;

    @Inject
    ModuleParser(final ModuleManager manager, @Assisted final @Nullable Boolean enabled) {
        this.manager = manager;
        this.enabled = enabled;
    }

    private static Predicate<ModuleLifecycle> predicateFor(final @Nullable Boolean enabled) {
        if (enabled == null) {
            return lifecycle -> true;
        } else if (enabled) {
            return lifecycle -> lifecycle.getState().isRunning();
        } else {
            return lifecycle -> !lifecycle.getState().isRunning();
        }
    }

    @Override
    public ArgumentParseResult<ModuleBase> parse(final CommandContext<CommandDispatcher> commandContext, final CommandInput commandInput) {
        final String input = commandInput.readString();
        final Optional<ModuleLifecycle> lifecycle = this.manager.getLifecycle(input);
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
        try {
            return ArgumentParseResult.success(this.manager.getModule(input).orElseThrow());
        } catch (final NoSuchElementException exception) {
            return ArgumentParseResult.failure(exception);
        }
    }

    @Override
    public @NonNull SuggestionProvider<CommandDispatcher> suggestionProvider() {
        return (BlockingSuggestionProvider.Strings<CommandDispatcher>) (context, input) -> {
            context.set(ConditionalCaseInsensitiveSuggestionProcessor.IGNORE_CASE, true);
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
        };
    }
}
