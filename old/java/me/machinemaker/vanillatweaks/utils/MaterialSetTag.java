package me.machinemaker.vanillatweaks.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MaterialSetTag implements Tag<Material> {

    private final NamespacedKey key;
    private final Set<Material> materials;

    public MaterialSetTag(NamespacedKey key, Material... materials) {
        this.key = key;
        this.materials = Sets.newEnumSet(Lists.newArrayList(materials), Material.class);
    }

    @Override
    public boolean isTagged(@NotNull Material item) {
        return this.materials.contains(item);
    }

    @Override
    public @NotNull Set<Material> getValues() {
        return this.materials;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return this.key;
    }
}
