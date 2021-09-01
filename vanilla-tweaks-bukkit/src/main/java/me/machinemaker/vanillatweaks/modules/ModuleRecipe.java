package me.machinemaker.vanillatweaks.modules;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public record ModuleRecipe<R extends Recipe & Keyed>(@NotNull R recipe) {

    @SuppressWarnings("unchecked")
    public Class<R> recipeType() {
        return (Class<R>) this.recipe.getClass();
    }

    public NamespacedKey key() {
        return this.recipe.getKey();
    }
}
