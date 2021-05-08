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
package me.machinemaker.vanillatweaks.pdc.types;

import com.google.common.collect.Maps;
import net.kyori.adventure.util.Index;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EnumDataType<E extends Enum<E>> implements PersistentDataType<String, E> {

    private static final Map<Class<?>, EnumDataType<?>> ENUM_DATA_TYPE_REGISTRY = Maps.newHashMap();

    private final Class<E> enumClass;
    private final Index<String, E> index;

    private EnumDataType(Class<E> classOfE) {
        this.enumClass = classOfE;
        this.index = Index.create(classOfE, Enum::name);
    }

    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<E> getComplexType() {
        return this.enumClass;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull E complex, @NotNull PersistentDataAdapterContext context) {
        return complex.name();
    }

    @NotNull
    @Override
    public E fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        E e = this.index.value(primitive);
        if (e == null) {
            throw new IllegalArgumentException(primitive + " did not match a value of " + enumClass.getName());
        }
        return e;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> EnumDataType<E> of(Class<E> classOfE) {
        return (EnumDataType<E>) ENUM_DATA_TYPE_REGISTRY.computeIfAbsent(classOfE, ignored -> new EnumDataType<E>(classOfE));
    }
}
