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
package me.machinemaker.vanillatweaks.pdc.types.itemstack;

import me.machinemaker.vanillatweaks.pdc.types.AbstractByteArrayType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ItemStackDataType extends AbstractByteArrayType<ItemStack> {

    @Override
    protected void write(@NotNull ItemStack object, @NotNull BukkitObjectOutputStream dataOutput) throws IOException {
        dataOutput.writeObject(object);
    }

    @Override
    protected ItemStack read(@NotNull BukkitObjectInputStream dataInput) throws ClassNotFoundException, IOException {
        return (ItemStack) dataInput.readObject();
    }

    @Override
    public @NotNull Class<ItemStack> getComplexType() {
        return ItemStack.class;
    }
}
