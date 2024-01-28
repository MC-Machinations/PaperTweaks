/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.modules.experimental.xpmanagement;

import java.util.Collection;
import java.util.Set;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.modules.ModuleRecipe;
import me.machinemaker.papertweaks.utils.Keys;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

@ModuleInfo(name = "XPManagement", configPath = "experimental.xp-management", description = "Store XP in bottles, smelt in furnace to retrieve XP")
public class XPManagement extends ModuleBase {

    private static final NamespacedKey XP_RECIPE_KEY = Keys.legacyKey("xp_management_recipe");
    private static final FurnaceRecipe XP_RECIPE = new FurnaceRecipe(XP_RECIPE_KEY, new ItemStack(Material.GLASS_BOTTLE), Material.EXPERIENCE_BOTTLE, 12, 1);

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.Empty.class;
    }

    @Override
    protected Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(PlayerListener.class);
    }

    @Override
    protected Collection<ModuleRecipe<?>> recipes() {
        return Set.of(new ModuleRecipe<>(XP_RECIPE));
    }
}
