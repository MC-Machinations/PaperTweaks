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
package me.machinemaker.vanillatweaks.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerSkull {

    private final ItemStack skull;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PlayerSkull(@Nullable String name, @Nullable String gameProfileName, @Nullable UUID uuid, @Nullable String texture, @Nullable Integer count) {
        this.skull = VTUtils.getSkull(GsonComponentSerializer.gson().deserializeOrNull(name), gameProfileName, uuid, texture, count != null ? count : 1);
    }

    public @NotNull ItemStack cloneWithAmount(int amount) {
        ItemStack clone = this.skull.clone();
        clone.setAmount(amount);
        return clone;
    }

    public @NotNull ItemStack cloneSingle() {
        return this.cloneWithAmount(1);
    }

    public @NotNull ItemStack cloneOriginal() {
        return this.skull.clone();
    }
}
