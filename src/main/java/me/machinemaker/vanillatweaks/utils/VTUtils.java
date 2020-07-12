package me.machinemaker.vanillatweaks.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collection;
import java.util.UUID;

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
}
