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
package me.machinemaker.papertweaks.cloud.parsers;

import com.google.common.collect.Iterables;
import java.io.Serial;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;

import static org.incendo.cloud.parser.ParserDescriptor.parserDescriptor;

/**
 * Parser for pseudo-enums.
 *
 * @param <C> Command sender type
 * @since 1.6.0
 */
public class PseudoEnumParser<C> implements ArgumentParser<C, String> {

    public static <C> ParserDescriptor<C, String> singlePseudoEnumParser(final Iterable<String> allowedValues) {
        return pseudoEnumParser(StringParser.StringMode.SINGLE, allowedValues);
    }

    public static <C> ParserDescriptor<C, String> quotedPseudoEnumParser(final Iterable<String> allowedValues) {
        return pseudoEnumParser(StringParser.StringMode.QUOTED, allowedValues);
    }

    public static <C> ParserDescriptor<C, String> greedyFlagYieldingPseudoEnumParser(final Iterable<String> allowedValues) {
        return pseudoEnumParser(StringParser.StringMode.GREEDY_FLAG_YIELDING, allowedValues);
    }

    public static <C> ParserDescriptor<C, String> greedyPseudoEnumParser(final Iterable<String> allowedValues) {
        return pseudoEnumParser(StringParser.StringMode.GREEDY, allowedValues);
    }

    private static <C> ParserDescriptor<C, String> pseudoEnumParser(final StringParser.StringMode stringMode, final Iterable<String> allowedValues) {
        return parserDescriptor(new PseudoEnumParser<>(stringMode, allowedValues), String.class);
    }

    private final Set<String> allowedValues;
    private final StringParser<C> stringParser;

    public PseudoEnumParser(final StringParser.StringMode stringMode, final Iterable<String> allowedValues) {
        this.stringParser = new StringParser<>(stringMode);
        final Set<String> allowedValuesSet = new HashSet<>();
        Iterables.addAll(allowedValuesSet, allowedValues);
        this.allowedValues = Set.copyOf(allowedValuesSet);
    }

    @Override
    public ArgumentParseResult<String> parse(final CommandContext<C> commandContext, final CommandInput commandInput) {
        final ArgumentParseResult<String> result = this.stringParser.parse(commandContext, commandInput);
        if (result.failure().isPresent()) {
            return result;
        } else {
            final String input = result.parsedValue().orElseThrow();
            if (!this.allowedValues.contains(input)) {
                return ArgumentParseResult.failure(new PseudoEnumParseException(input, this.allowedValues, commandContext));
            } else {
                return result;
            }
        }
    }

    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return SuggestionProvider.suggestingStrings(this.allowedValues);
    }

    /**
     * Get the string mode
     *
     * @return String mode
     * @since 1.6.0
     */
    public StringParser.StringMode getStringMode() {
        return this.stringParser.stringMode();
    }

    public static final class PseudoEnumParseException extends ParserException {

        @Serial
        private static final long serialVersionUID = 5198435213837796433L;
        private final String input;
        private final transient Set<String> acceptableValues;

        /**
         * Construct a new pseudo-enum parse exception
         *
         * @param input            Input
         * @param acceptableValues Acceptable values
         * @param context          Command context
         * @since 1.6.0
         */
        public PseudoEnumParseException(final String input, final Set<String> acceptableValues, final CommandContext<?> context) {
            super(PseudoEnumParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM, CaptionVariable.of("input", input), CaptionVariable.of("acceptableValues", String.join(", ", acceptableValues)));
            this.input = input;
            this.acceptableValues = Collections.unmodifiableSet(acceptableValues);
        }

        /**
         * Get the input provided by the sender
         *
         * @return Input
         * @since 1.6.0
         */
        public String getInput() {
            return this.input;
        }

        /**
         * Get the acceptable values for this argument
         *
         * @return The acceptable values
         * @since 1.6.0
         */
        public Set<String> getAcceptableValues() {
            return this.acceptableValues;
        }

    }
}
