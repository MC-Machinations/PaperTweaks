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
package me.machinemaker.papertweaks.modules.items.wrenches;

import com.google.inject.Inject;
import java.math.BigInteger;
import java.util.Set;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.modules.ModuleRecipe;
import me.machinemaker.papertweaks.utils.Keys;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.Style.style;

class Lifecycle extends ModuleLifecycle {

    static final String RESOURCE_PACK_URL = "https://potrebic.box.com/shared/static/uw4fvii2o8qsjuz6xuant1safwjdnrez.zip";
    static final byte[] RESOURCE_PACK_HASH = new BigInteger("1ACF79C491B3CB9EEE50816AD0CC1FC45AABA147", 16).toByteArray();

    static final ItemStack WRENCH = new ItemStack(Material.CARROT_ON_A_STICK, 1);
    static final NamespacedKey WRENCH_RECIPE_KEY = Keys.key("redstone_wrench");
    static final ShapedRecipe WRENCH_RECIPE = new ShapedRecipe(WRENCH_RECIPE_KEY, WRENCH)
        .shape(
            " # ",
            " ##",
            "$  "
        ).setIngredient('#', Material.GOLD_INGOT)
        .setIngredient('$', Material.IRON_INGOT);

    static {
        final ItemMeta meta = requireNonNull(WRENCH.getItemMeta());
        meta.displayName(text("Redstone Wrench", style(TextDecoration.ITALIC.withState(false))));
        meta.setUnbreakable(true);
        meta.setCustomModelData(4321);
        WRENCH.setItemMeta(meta);
    }

    private final Config config;

    @Inject
    Lifecycle(final JavaPlugin plugin, final Set<ModuleCommand> commands, final Set<ModuleListener> listeners, final Set<ModuleConfig> configs, final Config config, final Set<ModuleRecipe<?>> moduleRecipes) {
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
