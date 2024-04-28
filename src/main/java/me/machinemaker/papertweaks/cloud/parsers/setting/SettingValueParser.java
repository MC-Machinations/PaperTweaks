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
package me.machinemaker.papertweaks.cloud.parsers.setting;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.settings.Setting;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

class SettingValueParser<C, S extends Setting<?, C>> implements ArgumentParser<CommandDispatcher, Object> {

    private final CloudKey<S> key;

    SettingValueParser(final CloudKey<S> key) {
        this.key = key;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArgumentParseResult<Object> parse(final CommandContext<CommandDispatcher> commandContext, final CommandInput commandInput) {
        final String string = commandInput.peekString(); // don't consume, as it will be consumed by the delegated parser
        final Optional<S> setting = commandContext.optional(this.key);
        return setting.map(s -> (ArgumentParseResult<Object>) s.argumentParser().parse(commandContext, commandInput)).orElseGet(() -> ArgumentParseResult.failure(new IllegalStateException(string + " isn't preceded by a setting")));
    }

    @Override
    public @NonNull SuggestionProvider<CommandDispatcher> suggestionProvider() {
        return (context, input) -> {
            final Optional<S> setting = context.optional(this.key);
            if (setting.isPresent()) {
                return setting.get().argumentParser().suggestionProvider().suggestionsFuture(context, input);
            } else {
                return CompletableFuture.completedFuture(Collections.emptyList());
            }
        };
    }
}
