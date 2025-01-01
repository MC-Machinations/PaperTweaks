/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2024-2025 Machine_Maker
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

import java.util.Locale;
import java.util.Map;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.settings.Setting;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.SuggestionProvider;

class SettingParser<C, S extends Setting<?, C>> implements ArgumentParser<CommandDispatcher, S> {

    private final Map<String, S> settings;
    private final CloudKey<S> key;
    private final boolean hideSuggestions;

    SettingParser(final Map<String, S> settings, final CloudKey<S> key, final boolean hideSuggestions) {
        this.settings = settings;
        this.key = key;
        this.hideSuggestions = hideSuggestions;
    }

    @Override
    public ArgumentParseResult<S> parse(final CommandContext<CommandDispatcher> commandContext, final CommandInput commandInput) {
        final String string = commandInput.readString();
        final S value = this.settings.get(string.toLowerCase(Locale.ENGLISH));
        if (value == null) {
            return ArgumentParseResult.failure(new IllegalArgumentException(string));
        }
        commandContext.store(this.key, value);

        return ArgumentParseResult.success(value);
    }

    @Override
    public @NonNull SuggestionProvider<CommandDispatcher> suggestionProvider() {
        if (this.hideSuggestions) {
            return SuggestionProvider.noSuggestions();
        }
        return SuggestionProvider.suggestingStrings(this.settings.keySet());
    }
}
