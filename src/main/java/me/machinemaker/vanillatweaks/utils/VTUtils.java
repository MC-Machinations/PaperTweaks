package me.machinemaker.vanillatweaks.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class VTUtils {

    public static ItemStack getSkull(String name, String uuid, String texture, int count) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, count);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setDisplayName(name);
        GameProfile profile = new GameProfile(UUID.fromString(uuid), null);
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
                result.add(item);
            } else {
                result.add(null);
                x.decrement();
            }
        }
        return result;
    }
}
