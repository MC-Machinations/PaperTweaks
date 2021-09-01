package me.machinemaker.vanillatweaks.pdc.types;

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
