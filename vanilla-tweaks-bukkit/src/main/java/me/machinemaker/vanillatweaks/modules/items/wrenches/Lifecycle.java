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
package me.machinemaker.vanillatweaks.modules.items.wrenches;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.modules.ModuleRecipe;
import me.machinemaker.vanillatweaks.utils.Keys;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigInteger;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.text;

class Lifecycle extends ModuleLifecycle {

    static final String RESOURCE_PACK_URL = "https://potrebic.box.com/shared/static/uw4fvii2o8qsjuz6xuant1safwjdnrez.zip";
    static final byte[] RESOURCE_PACK_HASH = new BigInteger("1ACF79C491B3CB9EEE50816AD0CC1FC45AABA147", 16).toByteArray();

    static final ItemStack WRENCH = new ItemStack(Material.CARROT_ON_A_STICK, 1);
    static {
        ItemMeta meta = requireNonNull(WRENCH.getItemMeta());
        VTUtils.loadMeta(meta, text("Redstone Wrench", Style.style().decoration(TextDecoration.ITALIC, false).build()));
        meta.setUnbreakable(true);
        meta.setCustomModelData(4321);
        WRENCH.setItemMeta(meta);
    }

    static final NamespacedKey WRENCH_RECIPE_KEY = Keys.key("redstone_wrench");
    static final ShapedRecipe WRENCH_RECIPE = new ShapedRecipe(WRENCH_RECIPE_KEY, WRENCH)
            .shape(
                    " # ",
                    " ##",
                    "$  "
            ).setIngredient('#', Material.GOLD_INGOT)
            .setIngredient('$', Material.IRON_INGOT);

    private final Config config;

    @Inject
    Lifecycle(JavaPlugin plugin, Set<ModuleCommand> commands, Set<ModuleListener> listeners, Set<ModuleConfig> configs, Config config, Set<ModuleRecipe<?>> moduleRecipes) {
        super(plugin, commands, listeners, configs, moduleRecipes);
        this.config = config;
    }

    @Override
    public void onEnable() {
        if (this.config.suggestResourcePack) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setResourcePack(RESOURCE_PACK_URL, RESOURCE_PACK_HASH);
            });
        }

    }

    @Override
    public void onReload() {
        if (this.config.suggestResourcePack) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setResourcePack(RESOURCE_PACK_URL, RESOURCE_PACK_HASH);
            });
        }
    }
}
