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
package me.machinemaker.vanillatweaks.tags;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public final class MaterialTag extends AbstractTag<Material> {

    public MaterialTag(NamespacedKey key, @NotNull Collection<Material> values) {
        super(key, values);
    }

    public static Builder builder(NamespacedKey key) {
        return new Builder(key);
    }

    public boolean isTagged(Item item) {
        return this.isTagged(item.getItemStack());
    }

    public boolean isTagged(ItemStack stack) {
        return this.isTagged(stack.getType());
    }

    public boolean isTagged(Block block) {
        return this.isTagged(block.getType());
    }

    public static final class Builder extends AbstractTag.Builder<Material, MaterialTag, Builder> {

        Builder(NamespacedKey key) {
            super(key);
        }

        @Override
        public String nameOf(Material value) {
            return value.name();
        }

        @Override
        public Collection<Material> allValues() {
            //noinspection deprecation // for tests
            return Set.copyOf(Arrays.stream(Material.values()).filter(m -> !m.isLegacy()).toList());
        }

        @Override
        public MaterialTag build() {
            return new MaterialTag(this.key, this.tagged);
        }
    }
}
