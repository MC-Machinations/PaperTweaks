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
package me.machinemaker.vanillatweaks.tags.types;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaterialTag extends BaseTag<Material, MaterialTag> {

    @SuppressWarnings("deprecation")
    public MaterialTag(@NotNull NamespacedKey key) {
        super(Material.class, key, Collections.emptySet(), ((Predicate<Material>) Material::isLegacy).negate());
    }

    public boolean isTagged(@NotNull Item item) {
        return isTagged(item.getItemStack());
    }

    public boolean isTagged(@NotNull ItemStack stack) {
        return isTagged(stack.getType());
    }

    @Override
    protected MaterialTag getThis() {
        return this;
    }

    @Override
    protected @NotNull Set<Material> getAllPossibleValues() {
        return Stream.of(Material.values()).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    protected @NotNull String getName(@NotNull Material value) {
        return value.name();
    }
}
