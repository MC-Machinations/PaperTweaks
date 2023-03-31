/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2022-2023 Machine_Maker
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
package me.machinemaker.vanillatweaks.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Entities {

    private Entities() {
    }

    public static <E extends Entity> Collection<E> getEntitiesOfType(final Class<E> classOfE, final World world, final Predicate<E> predicate) {
        return world.getEntitiesByClass(classOfE).stream().filter(predicate).toList();
    }

    public static <E extends Entity> @Nullable E getSingleNearbyEntityOfType(final Class<E> classOfE, final Location location, final double dx, final double dy, final double dz) {
        return getSingleNearbyEntityOfType(classOfE, location, dx, dy, dz, e -> true);
    }

    public static <E extends Entity> @Nullable E getSingleNearbyEntityOfType(final Class<E> classOfE, final Location location, final double dx, final double dy, final double dz, final Predicate<E> predicate) {
        final Collection<E> entities = getNearbyEntitiesOfType(classOfE, location, dx, dy, dz, predicate);
        return entities.stream().findAny().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Entity> Collection<E> getNearbyEntitiesOfType(final E entity, final double dx, final double dy, final double dz) {
        return getNearbyEntitiesOfType((Class<E>) entity.getClass(), entity.getLocation(), dx, dy, dz);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Entity> Collection<E> getNearbyEntitiesOfType(final E entity, final double dx, final double dy, final double dz, final Predicate<E> predicate) {
        return getNearbyEntitiesOfType((Class<E>) entity.getClass(), entity.getLocation(), dx, dy, dz, predicate);
    }

    public static <E extends Entity> Collection<E> getNearbyEntitiesOfType(final Class<E> classOfE, final Location location, final double dx, final double dy, final double dz) {
        return getNearbyEntitiesOfType(classOfE, location, dx, dy, dz, e -> true);
    }

    public static <E extends Entity> Collection<E> getNearbyEntitiesOfType(final Class<E> classOfE, final Location location, final double dx, final double dy, final double dz, final Predicate<E> predicate) {
        Preconditions.checkNotNull(location.getWorld(), "Cannot use a location with a null world");
        return getNearbyEntitiesOfType(classOfE, location.getWorld(), location, dx, dy, dz, predicate);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Entity> Collection<E> getNearbyEntitiesOfType(final Class<E> classOfE, final World world, final Location location, final double dx, final double dy, final double dz, final Predicate<E> predicate) {
        final List<E> nearby = Lists.newArrayList();
        for (final Entity nearbyEntity : world.getNearbyEntities(location, dx, dy, dz)) {
            if (classOfE.isAssignableFrom(nearbyEntity.getClass()) && predicate.test((E) nearbyEntity)) {
                nearby.add((E) nearbyEntity);
            }
        }
        return nearby;
    }
}
