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
package me.machinemaker.papertweaks.cloud.processors;

import java.util.stream.Stream;
import org.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.suggestion.FilteringSuggestionProcessor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProcessor;

import static org.incendo.cloud.key.CloudKey.cloudKey;
import static org.incendo.cloud.suggestion.FilteringSuggestionProcessor.Filter.partialTokenMatches;

public class ConditionalCaseInsensitiveSuggestionProcessor<C> implements SuggestionProcessor<C> {

    private static final ConditionalCaseInsensitiveSuggestionProcessor<?> INSTANCE = new ConditionalCaseInsensitiveSuggestionProcessor<>();

    @SuppressWarnings("unchecked")
    public static <C> ConditionalCaseInsensitiveSuggestionProcessor<C> instance() {
        return (ConditionalCaseInsensitiveSuggestionProcessor<C>) INSTANCE;
    }

    public static final CloudKey<Boolean> IGNORE_CASE = cloudKey("papertweaks:suggestions/ignore_case", Boolean.class);

    private final SuggestionProcessor<C> caseSensitive = new FilteringSuggestionProcessor<>(partialTokenMatches(false));
    private final SuggestionProcessor<C> caseInsensitive = new FilteringSuggestionProcessor<>(partialTokenMatches(true));

    @Override
    public Stream<Suggestion> process(final CommandPreprocessingContext<C> context, final Stream<Suggestion> suggestions) {
        final boolean ignoreCase = Boolean.TRUE.equals(context.commandContext().getOrDefault(IGNORE_CASE, false));
        final SuggestionProcessor<C> delegate = ignoreCase ? this.caseInsensitive : this.caseSensitive;
        return delegate.process(context, suggestions);
    }
}
