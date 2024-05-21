/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import me.machinemaker.mirror.paper.PaperMirror;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;

@DefaultQualifier(NonNull.class)
public final class PTUtils {

    private static final Class<?> CRAFT_PLAYER_CLASS = PaperMirror.getCraftBukkitClass("entity.CraftPlayer");
    private static final Class<?> NMS_PLAYER_CLASS = PaperMirror.findMinecraftClass("world.entity.player.Player");

    private static final Gson GSON = new Gson();

    private PTUtils() {
    }

    public static Component sanitizeName(final String name) {
        final JsonElement tree = GsonComponentSerializer.gson().serializer().fromJson(name, JsonElement.class);
        if (tree instanceof final JsonObject object && object.has("text")) {
            final String text = object.getAsJsonPrimitive("text").getAsString();
            if (text.contains("§")) {
                if (object.size() == 1) {
                    final TextComponent deserialized = LegacyComponentSerializer.legacySection().deserialize(text);
                    if (text.contains("§r")) {
                        return deserialized.decoration(ITALIC, false);
                    }
                    return deserialized;
                } else {
                    throw new IllegalStateException("This is a bug. Report to PaperTweaks Discord or GitHub: %s".formatted(name));
                }
            }
        }
        return GsonComponentSerializer.gson().deserializeFromTree(tree);
    }

    public static ItemStack getSkull(final Component name, final String texture) {
        return getSkull(name, null, texture, 1);
    }

    public static ItemStack getSkull(final Component name, final @Nullable UUID uuid, final String texture, final int count) {
        return getSkull(name, makeValidGameProfileName(name), uuid, texture, count);
    }

    public static ItemStack getSkull(final String stringName, final UUID uuid, final String texture) {
        final String gameProfileName = makeValidGameProfileName(stringName);
        final @Nullable Component name;
        if (!gameProfileName.equals(stringName)) {
            name = translatable("block.minecraft.player_head.named", text(stringName));
        } else {
            name = null; // name is valid in the gameprofile
        }
        return getSkull(name, gameProfileName, uuid, texture, 1);
    }

    private static ItemStack getSkull(final @Nullable Component customName, final String gameProfileName, final @Nullable UUID uuid, final String texture, final int count) {
        final ItemStack skull = new ItemStack(Material.PLAYER_HEAD, count);
        final @Nullable SkullMeta meta = (SkullMeta) Objects.requireNonNull(skull.getItemMeta());
        final PlayerProfile profile = Bukkit.createProfile(uuid == null ? UUID.randomUUID() : uuid, gameProfileName);
        profile.setProperty(new ProfileProperty("textures", texture));
        meta.setPlayerProfile(profile);
        if (customName != null) {
            meta.itemName(customName);
        }
        skull.setItemMeta(meta);
        return skull;
    }

    public static void sanitizeTextures(final PlayerProfile profile) {
        final @Nullable ProfileProperty textures = profile.getProperties().stream().filter(property -> property.getName().equals("textures")).findFirst().orElse(null);
        profile.removeProperty("textures");
        if (textures != null) {
            final JsonObject object = GSON.fromJson(new String(Base64.getDecoder().decode(textures.getValue()), StandardCharsets.UTF_8), JsonObject.class);
            object.remove("timestamp");
            profile.setProperty(new ProfileProperty("textures", Base64.getEncoder().encodeToString(GSON.toJson(object).getBytes(StandardCharsets.UTF_8))));
        }
    }

    public static String makeValidGameProfileName(final Component input) {
        return makeValidGameProfileName(plainText().serialize(input));
    }

    public static String makeValidGameProfileName(final String input) {
        return input.replace(' ', '_').substring(0, Math.min(16, input.length()));
    }


    public static Location toBlockLoc(final Location location) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Location toCenter(final Location location, final boolean includeY) {
        final Location center = location.clone();
        center.setX(location.getBlockX() + 0.5);
        if (includeY) {
            center.setY(location.getBlockY() + 0.5);
        }
        center.setZ(location.getBlockZ() + 0.5);
        return center;
    }

    public static <T> T random(final Collection<T> coll) {
        int num = (int) (Math.random() * coll.size());
        for (final T t : coll) if (--num < 0) return t;
        throw new AssertionError();
    }

    public static <T> Map<CachedHashObjectWrapper<T>, MutableInt> toCachedMapCount(final List<T> list) {
        final Map<CachedHashObjectWrapper<T>, MutableInt> listCount = new HashMap<>();
        for (final T item : list) {
            listCount.computeIfAbsent(new CachedHashObjectWrapper<>(item), (k) -> new MutableInt()).increment();
        }
        return listCount;
    }

    /**
     * Replaces all occurrences of items from {unioned} that are not in {with} with null.
     */
    public static <T> List<@Nullable T> nullUnionList(final List<T> unioned, final List<T> with) {
        final Map<CachedHashObjectWrapper<T>, MutableInt> withCount = toCachedMapCount(with);
        return nullUnionList(unioned, withCount);
    }

    public static <T> List<@Nullable T> nullUnionList(final List<T> unioned,
                                            final Map<CachedHashObjectWrapper<T>, MutableInt> with) {
        final List<@Nullable T> result = new ArrayList<>();
        for (final T item : unioned) {
            final MutableInt x = with.get(new CachedHashObjectWrapper<>(item));
            if (x == null || x.intValue() <= 0) {
                result.add(null);
            } else {
                result.add(item);
                x.decrement();
            }
        }
        return result;
    }

    public static void runIfHasPermission(final String permission, final Consumer<CommandSender> consumer) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                consumer.accept(player);
            }
        }
        consumer.accept(Bukkit.getConsoleSender());
    }
}
