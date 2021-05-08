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
package me.machinemaker.vanillatweaks.pdc.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LocationDataType implements PersistentDataType<byte[], Location> {
    @NotNull
    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @NotNull
    @Override
    public Class<Location> getComplexType() {
        return Location.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull Location complex, @NotNull PersistentDataAdapterContext context) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(complex.getBlockX());
            dataOutput.writeInt(complex.getBlockY());
            dataOutput.writeInt(complex.getBlockZ());
            if (complex.getWorld() != null) {
                dataOutput.writeBoolean(true);
                dataOutput.writeUTF(complex.getWorld().getName());
            } else {
                dataOutput.writeBoolean(false);
            }
            dataOutput.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save location", e);
        }
    }

    @NotNull
    @Override
    public Location fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(primitive);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            int x = dataInput.readInt();
            int y = dataInput.readInt();
            int z = dataInput.readInt();
            boolean hasWorld = dataInput.readBoolean();
            World world = null;
            if (hasWorld) {
                world = Bukkit.getWorld(dataInput.readUTF());
            }
            dataInput.close();
            return new Location(world, x, y, z);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Unable to read location");
    }
}
