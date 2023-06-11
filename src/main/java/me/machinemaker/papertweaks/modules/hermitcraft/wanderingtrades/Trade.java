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
package me.machinemaker.papertweaks.modules.hermitcraft.wanderingtrades;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.machinemaker.papertweaks.utils.PTUtils;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

class Trade {

    private final int maxUses;
    private final Material secondaryCost;
    private final ItemStack skull;

    @JsonCreator
    Trade(int maxUses, @NotNull Material secondaryCost, int headCount, @NotNull String name, @NotNull UUID uuid, @NotNull String texture) {
        this.maxUses = maxUses;
        this.secondaryCost = secondaryCost;
        this.skull = PTUtils.getSkull(GsonComponentSerializer.gson().deserialize(name), uuid, texture, headCount);
    }

    public boolean isBlockTrade() {
        return this.secondaryCost != Material.AIR;
    }

    public @NotNull MerchantRecipe createTrade() {
        MerchantRecipe recipe = new MerchantRecipe(this.skull.clone(), this.maxUses);
        recipe.addIngredient(new ItemStack(Material.EMERALD, 1));
        if (secondaryCost != Material.AIR) {
            recipe.addIngredient(new ItemStack(this.secondaryCost, 1));
        }
        return recipe;
    }
}
