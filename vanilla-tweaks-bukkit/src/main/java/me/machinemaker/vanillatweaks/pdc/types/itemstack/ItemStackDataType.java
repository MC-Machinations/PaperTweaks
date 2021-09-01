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
