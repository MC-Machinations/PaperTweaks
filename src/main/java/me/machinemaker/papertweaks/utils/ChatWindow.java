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
package me.machinemaker.papertweaks.utils;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import me.machinemaker.papertweaks.LoggerFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.slf4j.Logger;

import static me.machinemaker.papertweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.text;

public final class ChatWindow {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatWindow.class);
    private static final int MAX_WIDTH = 310;
    private static final int DEFAULT_WIDTH = MAX_WIDTH;
    private static final Map<Character, Integer> WIDTHS;

    static {
        final ImmutableMap.Builder<Character, Integer> builder = ImmutableMap.builder();
        builder.put(' ', 3).put('!', 1).put('"', 3).put('#', 5).put('$', 6).put('%', 5).put('&', 5).put('\'', 1)
                .put('(', 3).put(')', 3).put('*', 3).put('+', 5).put(',', 1).put('-', 5).put('.', 1).put('/', 5)
                .putAll(Map.of(
                        '0', 5,
                        '1', 5,
                        '2', 5,
                        '3', 5,
                        '4', 5,
                        '5', 5,
                        '6', 5,
                        '7', 5,
                        '8', 5,
                        '9', 5
                ))
                .put(':', 1).put(';', 1).put('<', 4).put('=', 5).put('>', 4).put('?', 5).put('@', 6)
                .put('A', 5).put('B', 5).put('C', 5).put('D', 5).put('E', 5).put('F', 5).put('G', 5)
                .put('H', 5).put('I', 3).put('J', 5).put('K', 5).put('L', 5).put('M', 5).put('N', 5)
                .put('O', 5).put('P', 5).put('Q', 5).put('R', 5).put('S', 5).put('T', 5).put('U', 5)
                .put('V', 5).put('W', 5).put('X', 5).put('Y', 5).put('Z', 5)
                .put('[', 3).put('\\', 5).put(']', 3).put('^', 5).put('_', 5).put('`', 2)
                .put('a', 5).put('b', 5).put('c', 5).put('d', 5).put('e', 5).put('f', 4).put('g', 5)
                .put('h', 5).put('i', 1).put('j', 5).put('k', 4).put('l', 2).put('m', 5).put('n', 5)
                .put('o', 5).put('p', 5).put('q', 5).put('r', 5).put('s', 5).put('t', 3).put('u', 5)
                .put('v', 5).put('w', 5).put('x', 5).put('y', 5).put('z', 5)
                .put('{', 3).put('|', 1).put('}', 3).put('~', 6)
                .put('ⓘ', 5);

        WIDTHS = builder.build();
    }

    private ChatWindow() {
    }

    public static int calculateWith(final String plainText) {
        int width = 0;
        for (final char c : plainText.toCharArray()) {
            if (WIDTHS.containsKey(c)) {
                width += WIDTHS.get(c) + 1;
            } else {
                LOGGER.warn("{} is not a recognized character", c);
                width += 5 + 1;
            }
        }
        return width;
    }

    public static Component center(final ComponentLike text) {
        final String plainText = PlainTextComponentSerializer.plainText().serialize(text.asComponent());
        final int width = calculateWith(plainText);
        if (width > MAX_WIDTH) {
            throw new IllegalArgumentException(plainText + " was longer than the maximum width");
        }
        final int spaceCount = (DEFAULT_WIDTH - width) / (WIDTHS.get(' ') + 1);
        return join(text(" ".repeat(spaceCount - (spaceCount / 2))), text, text(" ".repeat(spaceCount / 2)));
    }
}
