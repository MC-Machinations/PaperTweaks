/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.papertweaks.cloud.processors;

import cloud.commandframework.execution.CommandSuggestionProcessor;
import cloud.commandframework.execution.preprocessor.CommandPreprocessingContext;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import io.leangen.geantyref.TypeToken;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import org.apache.commons.lang3.StringUtils;

public final class SimpleSuggestionProcessor implements CommandSuggestionProcessor<CommandDispatcher> {

    public static final CloudKey<Boolean> IGNORE_CASE = SimpleCloudKey.of("vanillatweaks:suggestions/ignore_case", TypeToken.get(Boolean.class));

    @Override
    public List<String> apply(final CommandPreprocessingContext<CommandDispatcher> context, final List<String> strings) {
        final boolean ignoreCase = Boolean.TRUE.equals(context.getCommandContext().getOrDefault(IGNORE_CASE, false));
        final BiPredicate<String, String> predicate = ignoreCase ? StringUtils::startsWithIgnoreCase : StringUtils::startsWith;
        final String input;
        if (context.getInputQueue().isEmpty()) {
            input = "";
        } else {
            input = context.getInputQueue().peek();
        }

        final List<String> suggestions = new LinkedList<>();
        for (final String suggestion : strings) {
            if (predicate.test(suggestion, input)) {
                suggestions.add(suggestion);
            }
        }
        return suggestions;
    }
}
