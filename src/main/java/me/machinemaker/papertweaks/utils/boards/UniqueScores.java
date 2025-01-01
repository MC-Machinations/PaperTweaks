/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2023-2025 Machine_Maker
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
package me.machinemaker.papertweaks.utils.boards;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class UniqueScores {

    private static final String INVISIBLE_CHAR = "§r";

    public static Pool createPool() {
        return new Pool();
    }

    private UniqueScores() {
    }

    public static final class Pool {

        private int count = 1;
        private final Set<String> entries = new LinkedHashSet<>();

        private Pool() {
        }

        public String generate() {
            final String newSequence = INVISIBLE_CHAR.repeat(this.count);
            this.entries.add(newSequence);
            this.count++;
            return newSequence;
        }

        public Set<String> entries() {
            return Collections.unmodifiableSet(this.entries);
        }
    }
}
