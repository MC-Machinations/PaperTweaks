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
package me.machinemaker.vanillatweaks.cloud;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public final class SuggestionProviders {

    private SuggestionProviders() {
    }

    private static final BiFunction<?, String, List<String>> EMPTY = (c, s) -> Collections.emptyList();

    @SuppressWarnings("unchecked")
    public static <C> BiFunction<C, String, List<String>> empty() {
        return (BiFunction<C, String, List<String>>) EMPTY;
    }
}
