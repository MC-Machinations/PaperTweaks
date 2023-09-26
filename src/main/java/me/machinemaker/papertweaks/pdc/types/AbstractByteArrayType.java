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
package me.machinemaker.papertweaks.pdc.types;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class AbstractByteArrayType<Z extends ConfigurationSerializable> implements PersistentDataType<byte[], Z> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull Z complex, @NotNull PersistentDataAdapterContext context) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            write(complex, dataOutput);

            dataOutput.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write object", e);
        }
    }

    protected abstract void write(@NotNull Z object, @NotNull BukkitObjectOutputStream dataOutput) throws IOException;

    @Override
    public @NotNull Z fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(primitive);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);



            dataInput.close();
            return read(dataInput);
        } catch (ClassNotFoundException | IOException e) {
            throw new IllegalStateException("Unable to read object", e);
        }
    }

    protected abstract Z read(@NotNull BukkitObjectInputStream dataInput) throws ClassNotFoundException, IOException;
}
