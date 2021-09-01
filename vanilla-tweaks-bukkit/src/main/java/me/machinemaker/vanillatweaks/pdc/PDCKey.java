package me.machinemaker.vanillatweaks.pdc;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record PDCKey<Z>(@NotNull NamespacedKey key, @NotNull PersistentDataType<?, Z> dataType) {

    public static @NotNull PDCKey<Boolean> forBool(@NotNull NamespacedKey key) {
        return new PDCKey<>(key, DataTypes.BOOLEAN);
    }

    public static @NotNull PDCKey<UUID> forUUID(@NotNull NamespacedKey key) {
        return new PDCKey<>(key, DataTypes.UUID);
    }

    public static @NotNull PDCKey<ItemStack> forItemStack(@NotNull NamespacedKey key) {
        return new PDCKey<>(key, DataTypes.ITEMSTACK);
    }

    public boolean has(@NotNull PersistentDataHolder holder) {
        return holder.getPersistentDataContainer().has(this.key, this.dataType);
    }

    public @Nullable Z get(@NotNull PersistentDataHolder holder) {
        return holder.getPersistentDataContainer().get(this.key, this.dataType);
    }

    public void set(@NotNull PersistentDataHolder holder, Z object) {
        holder.getPersistentDataContainer().set(this.key, this.dataType, object);
    }

    public @Nullable Z get(@NotNull ItemStack stack) {
        if (stack.getItemMeta() != null) {
            return get(stack.getItemMeta());
        }
        return null;
    }

    public boolean has(@NotNull ItemStack stack) {
        if (stack.getItemMeta() != null) {
            return has(stack.getItemMeta());
        }
        return false;
    }

    @Contract("_, null -> null; _, !null -> !null")
    public Z getOrDefault(@NotNull PersistentDataHolder holder, Z defaultValue) {
        return holder.getPersistentDataContainer().getOrDefault(this.key, this.dataType, defaultValue);
    }
}
