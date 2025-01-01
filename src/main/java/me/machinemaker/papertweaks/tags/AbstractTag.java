/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2022-2025 Machine_Maker
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
package me.machinemaker.papertweaks.tags;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

abstract class AbstractTag<T extends Keyed> implements Tag<T> {

    private final NamespacedKey key;
    protected final Set<T> tagged;

    protected AbstractTag(NamespacedKey key, @NotNull Collection<T> values) {
        this.key = key;
        this.tagged = Set.copyOf(values);
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public boolean isTagged(T value) {
        return this.tagged.contains(value);
    }

    @Override
    public boolean test(T value) {
        return this.isTagged(value);
    }

    @NotNull
    @Override
    public Set<T> getValues() {
        return this.tagged;
    }

    static abstract class Builder<T extends Keyed, A extends AbstractTag<T>, C extends Builder<T, A, C>> {

        protected final NamespacedKey key;
        protected final Set<T> tagged;

        protected Builder(NamespacedKey key) {
            this(key, Collections.emptySet());
        }

        protected Builder(NamespacedKey key, Set<T> tagged) {
            this.key = key;
            this.tagged = new HashSet<>(tagged);
        }

        @SuppressWarnings("unchecked")
        private C self() {
            return (C) this;
        }

        public final C endsWith(String suffix) {
            return this.add(v -> this.nameOf(v).endsWith(suffix));
        }

        public final C startsWith(String prefix) {
            return this.add(v -> this.nameOf(v).startsWith(prefix));
        }

        public final C contains(String contains) {
            return this.add(v -> this.nameOf(v).contains(contains));
        }

        public final C add(Predicate<T> predicate) {
            return this.add(this.allValues().stream().filter(predicate).toList());
        }

        public final C add(org.bukkit.Tag<T> tag) {
            return this.add(tag.getValues());
        }

        public final C add(Tag<T> tag) {
            return this.add(tag.getValues());
        }

        public final C add(Collection<T> values) {
            this.tagged.addAll(values);
            return this.self();
        }

        @SafeVarargs
        public final C add(T... values) {
            //noinspection ManualArrayToCollectionCopy
            for (T value : values) {
                //noinspection UseBulkOperation
                this.tagged.add(value);
            }
            return this.self();
        }

        public final C remove(Predicate<T> predicate) {
            this.tagged.removeIf(predicate);
            return this.self();
        }

        public final C remove(org.bukkit.Tag<T> tag) {
            return this.remove(tag.getValues());
        }

        public final C remove(Tag<T> tag) {
            return this.remove(tag.getValues());
        }

        public final C remove(Collection<T> values) {
            this.tagged.removeAll(values);
            return this.self();
        }

        @SafeVarargs
        public final C remove(T... values) {
            for (T value : values) {
                this.tagged.remove(value);
            }
            return this.self();
        }

        public final C verify(int size) {
            if (this.tagged.size() != size) {
                throw new IllegalStateException(String.format("%s does not have the expected size. expected: %d, actual: %d", this.key, size, this.tagged.size()));
            }
            return this.self();
        }

        public abstract String nameOf(T value);

        public abstract Collection<T> allValues();

        public abstract A build();
    }
}
