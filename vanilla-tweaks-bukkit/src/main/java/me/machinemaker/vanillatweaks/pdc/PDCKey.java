/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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
package me.machinemaker.vanillatweaks.pdc;

import me.machinemaker.vanillatweaks.pdc.types.EnumDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record PDCKey<Z>(@NotNull NamespacedKey key, @NotNull PersistentDataType<?, Z> dataType) {

    public static @NotNull PDCKey<Boolean> bool(@NotNull NamespacedKey key) {
        return new PDCKey<>(key, DataTypes.BOOLEAN);
    }

    public static @NotNull PDCKey<Long> forLong(@NotNull NamespacedKey key) {
        return new PDCKey<>(key, PersistentDataType.LONG);
    }

    public static @NotNull PDCKey<String> string(@NotNull NamespacedKey key) {
        return new PDCKey<>(key, PersistentDataType.STRING);
    }

    public static @NotNull PDCKey<UUID> uuid(@NotNull NamespacedKey key) {
        return new PDCKey<>(key, DataTypes.UUID);
    }

    public static @NotNull PDCKey<ItemStack> itemStack(@NotNull NamespacedKey key) {
        return new PDCKey<>(key, DataTypes.ITEMSTACK);
    }

    public static <E extends Enum<E>> @NotNull PDCKey<E> enums(@NotNull NamespacedKey key, Class<E> classOfE) {
        return new PDCKey<>(key, EnumDataType.of(classOfE));
    }

    @Contract(pure = true)
    public boolean has(@NotNull PersistentDataHolder holder) {
        return holder.getPersistentDataContainer().has(this.key, this.dataType);
    }

    @Contract(pure = true)
    public @Nullable Z getFrom(@NotNull PersistentDataHolder holder) {
        return holder.getPersistentDataContainer().get(this.key, this.dataType);
    }

    public void setTo(@NotNull PersistentDataHolder holder, Z object) {
        holder.getPersistentDataContainer().set(this.key, this.dataType, object);
    }

    @Contract(pure = true)
    public @Nullable Z getFrom(@NotNull ItemStack stack) {
        if (stack.getItemMeta() != null) {
            return getFrom(stack.getItemMeta());
        }
        return null;
    }

    @Contract(pure = true)
    public boolean has(@NotNull ItemStack stack) {
        if (stack.getItemMeta() != null) {
            return has(stack.getItemMeta());
        }
        return false;
    }

    public void remove(@NotNull PersistentDataHolder holder) {
        holder.getPersistentDataContainer().remove(this.key);
    }

    @Contract(value = "_, null -> null; _, !null -> !null", pure = true)
    public Z getFromOrDefault(@NotNull PersistentDataHolder holder, Z defaultValue) {
        return holder.getPersistentDataContainer().getOrDefault(this.key, this.dataType, defaultValue);
    }
}
