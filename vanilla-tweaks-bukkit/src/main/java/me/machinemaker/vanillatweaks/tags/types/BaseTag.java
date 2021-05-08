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
package me.machinemaker.vanillatweaks.tags.types;

import com.google.common.collect.Lists;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BaseTag<T extends Keyed, C extends BaseTag<T, C>> implements Tag<T> {

    protected final NamespacedKey key;
    protected final Set<T> tagged;
    private final List<Predicate<T>> globalPredicates;

    protected BaseTag(@NotNull Class<T> clazz, @NotNull NamespacedKey key, @NotNull Collection<T> values) {
        this(clazz, key, values, o -> true);
    }

    protected BaseTag(@NotNull Class<T> clazz, @NotNull NamespacedKey key, @NotNull Collection<T> values, @NotNull Predicate<T> globalPredicate) {
        this.key = key;
        this.tagged = clazz.isEnum() ? createEnumSet(clazz) : new HashSet<>();
        this.tagged.addAll(values);
        this.globalPredicates = Collections.singletonList(globalPredicate);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <E> Set<E> createEnumSet(Class<E> enumClass) {
        assert enumClass.isEnum();
        return (Set<E>) EnumSet.noneOf((Class<Enum>) enumClass);
    }

    protected abstract C getThis();

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @NotNull
    @Override
    public Set<T> getValues() {
        return tagged;
    }

    @Override
    public boolean isTagged(@NotNull T item) {
        return tagged.contains(item);
    }

    @SafeVarargs
    @NotNull
    public final C add(@NotNull Tag<T>... tags) {
        for (Tag<T> tag : tags) {
            add(tag.getValues());
        }
        return getThis();
    }

    @SuppressWarnings("varargs")
    @SafeVarargs
    @NotNull
    public final C add(@NotNull T... values) {
        this.tagged.addAll(Lists.newArrayList(values));
        return getThis();
    }

    @NotNull
    public C add(@NotNull Collection<T> collection) {
        this.tagged.addAll(collection);
        return getThis();
    }

    @NotNull
    public C add(@NotNull Predicate<T> filter) {
        return add(getAllPossibleValues().stream().filter(globalPredicates.stream().reduce(Predicate::or).orElse(t -> true)).filter(filter).collect(Collectors.toSet()));
    }

    @NotNull
    public C contains(@NotNull String with) {
        return add(value -> getName(value).contains(with));
    }

    @NotNull
    public C endsWith(@NotNull String with) {
        return add(value -> getName(value).endsWith(with));
    }

    @NotNull
    public C startsWith(@NotNull String with) {
        return add(value -> getName(value).startsWith(with));
    }

    @SafeVarargs
    @NotNull
    public final C not(@NotNull Tag<T>... tags) {
        for (Tag<T> tag : tags) {
            not(tag.getValues());
        }
        return getThis();
    }

    @SuppressWarnings("varargs")
    @SafeVarargs
    @NotNull
    public final C not(@NotNull T... values) {
        Lists.newArrayList(values).forEach(this.tagged::remove);
        return getThis();
    }

    @NotNull
    public C not(@NotNull Collection<T> values) {
        this.tagged.removeAll(values);
        return getThis();
    }

    @NotNull
    public C not(@NotNull Predicate<T> filter) {
        not(getAllPossibleValues().stream().filter(globalPredicates.stream().reduce(Predicate::or).orElse(t -> true)).filter(filter).collect(Collectors.toSet()));
        return getThis();
    }

    @NotNull
    public C notContains(@NotNull String with) {
        return not(value -> getName(value).contains(with));
    }

    @NotNull
    public C notEndsWith(@NotNull String with) {
        return not(value -> getName(value).endsWith(with));
    }

    @NotNull
    public C notStartsWith(@NotNull String with) {
        return not(value -> getName(value).startsWith(with));
    }

    @NotNull
    public C ensureSize(@NotNull String label, int size) {
        long actual = this.tagged.stream().filter(globalPredicates.stream().reduce(Predicate::or).orElse(t -> true)).count();
        if (size != actual) {
            throw new IllegalStateException(key.toString() + ": " + label + " - Expected " + size + " values, got " + actual);
        }
        return getThis();
    }

    @NotNull
    protected abstract Set<T> getAllPossibleValues();

    @NotNull
    protected abstract String getName(@NotNull T value);
}