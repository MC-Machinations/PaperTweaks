/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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

import me.machinemaker.vanillatweaks.pdc.types.BooleanDataType;
import me.machinemaker.vanillatweaks.pdc.types.itemstack.ItemStackArrayDataType;
import me.machinemaker.vanillatweaks.pdc.types.LocationDataType;
import me.machinemaker.vanillatweaks.pdc.types.UUIDDataType;
import me.machinemaker.vanillatweaks.pdc.types.itemstack.ItemStackDataType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public final class DataTypes {

    private DataTypes() {
    }

    public static final PersistentDataType<Byte, Boolean> BOOLEAN = new BooleanDataType();
    public static final PersistentDataType<byte[], UUID> UUID = new UUIDDataType();
    public static final PersistentDataType<byte[], Location> LOCATION = new LocationDataType();
    public static final PersistentDataType<byte[], ItemStack> ITEMSTACK = new ItemStackDataType();

    public static final PersistentDataType<String, ItemStack[]> ITEMSTACK_ARRAY = new ItemStackArrayDataType();
}
