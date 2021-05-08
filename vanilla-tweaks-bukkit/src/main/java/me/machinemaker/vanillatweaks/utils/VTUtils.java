/*
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
package me.machinemaker.vanillatweaks.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public final class VTUtils {

    private VTUtils() {
    }


    public static ItemStack getSkull(String name, String texture) {
        return getSkull(name, null, texture, 1);
    }

    public static ItemStack getSkull(String name, @Nullable UUID uuid, String texture, int count) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, count);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setDisplayName(name);
        GameProfile profile = new GameProfile(uuid == null ? UUID.randomUUID() : uuid, null);
        profile.getProperties().put("textures", new Property("textures", texture));
        ReflectionUtils.getField(meta.getClass(), "profile", GameProfile.class).set(meta, profile);
        skull.setItemMeta(meta);
        return skull;
    }

    public static <T> T random(Collection<T> coll) {
        int num = (int) (Math.random() * coll.size());
        for(T t: coll) if (--num < 0) return t;
        throw new AssertionError();
    }

    public static <T> @NotNull Map<CachedHashObjectWrapper<T>, MutableInt> toCachedMapCount(@NotNull List<T> list) {
        Map<CachedHashObjectWrapper<T>, MutableInt> listCount = new HashMap<>();
        for (T item : list) {
            listCount.computeIfAbsent(new CachedHashObjectWrapper<>(item), (k) -> new MutableInt()).increment();
        }
        return listCount;
    }

    /**
     * Replaces all occurrences of items from {unioned} that are not in {with} with null.
     */
    public static <T> @NotNull List<T> nullUnionList(@NotNull List<T> unioned, @NotNull List<T> with) {
        @NotNull Map<CachedHashObjectWrapper<T>, MutableInt> withCount = toCachedMapCount(with);
        return nullUnionList(unioned, withCount);
    }

    public static <T> @NotNull List<T> nullUnionList(@NotNull List<T> unioned,
                                                     @NotNull Map<CachedHashObjectWrapper<T>, MutableInt> with) {
        List<T> result = new ArrayList<>();
        for (T item: unioned) {
            MutableInt x = with.get(new CachedHashObjectWrapper<>(item));
            if (x == null || x.intValue() <= 0) {
                result.add(null);
            } else {
                result.add(item);
                x.decrement();
            }
        }
        return result;
    }

    public static <E extends Entity> @Nullable E getSingleNearbyEntityOfType(Class<E> classOfE, @NotNull Location location, double dx, double dy, double dz) {
        return getSingleNearbyEntityOfType(classOfE, location, dx, dy, dz, e -> true);
    }

    public static <E extends Entity> @Nullable E getSingleNearbyEntityOfType(Class<E> classOfE, @NotNull Location location, double dx, double dy, double dz, @NotNull Predicate<E> predicate) {
        Collection<E> entities = getNearbyEntitiesOfType(classOfE, location, dx, dy, dz, predicate);
        return entities.stream().findAny().orElse(null);
    }

    public static <E extends Entity> Collection<E> getNearbyEntitiesOfType(Class<E> classOfE, @NotNull Location location, double dx, double dy, double dz) {
        return getNearbyEntitiesOfType(classOfE, location, dx, dy, dz, e -> true);
    }

    public static <E extends Entity> Collection<E> getNearbyEntitiesOfType(Class<E> classOfE, @NotNull Location location, double dx, double dy, double dz, @NotNull Predicate<E> predicate) {
        //noinspection ConstantConditions
        Preconditions.checkNotNull(location.getWorld(), "Cannot use a location with a null world");
        return getNearbyEntitiesOfType(classOfE, location.getWorld(), location, dx, dy, dz, predicate);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Entity> Collection<E> getNearbyEntitiesOfType(Class<E> classOfE, @NotNull World world, @NotNull Location location, double dx, double dy, double dz, @NotNull Predicate<E> predicate) {
        List<E> nearby = Lists.newArrayList();
        for (Entity nearbyEntity : world.getNearbyEntities(location, dx, dy, dz)) {
            if (classOfE.isAssignableFrom(nearbyEntity.getClass()) && predicate.test((E) nearbyEntity)) {
                nearby.add((E) nearbyEntity);
            }
        }
        return nearby;
    }
}
