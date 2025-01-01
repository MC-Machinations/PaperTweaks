/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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

import java.net.URI;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.modules.ModuleRecipe;
import me.machinemaker.papertweaks.utils.Keys;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.resource.ResourcePackInfo.resourcePackInfo;
import static net.kyori.adventure.resource.ResourcePackRequest.resourcePackRequest;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.Style.style;

@ModuleInfo(name = "RotationWrenches", configPath = "items.rotation-wrenches", description = "A wrench to rotate redstone components and/or terracotta")
public class RotationWrenches extends ModuleBase {

    private static final UUID RESOURCE_PACK_UUID = UUID.fromString("904f0c77-69fd-380e-b1d7-5241aa6f5637");
    private static final URI RESOURCE_PACK_URL = URI.create("https://potrebic.box.com/shared/static/uw4fvii2o8qsjuz6xuant1safwjdnrez.zip");
    private static final String RESOURCE_PACK_HASH = "1ACF79C491B3CB9EEE50816AD0CC1FC45AABA147";
    private static final Component PACK_PROMPT = text("This resource pack adds a texture for the Redstone Wrench");

    static final ResourcePackRequest PACK_REQUEST = resourcePackRequest()
        .prompt(PACK_PROMPT)
        .required(true)
        .packs(resourcePackInfo(RESOURCE_PACK_UUID, RESOURCE_PACK_URL, RESOURCE_PACK_HASH))
        .build();

    static final int RESOURCE_PACK_MODEL_DATA = 4321;
    static final NamespacedKey WRENCH_RECIPE_KEY = Keys.legacyKey("redstone_wrench");
    static final ShapedRecipe WRENCH_RECIPE;
    private static final String ITEM_NAME = "Redstone Wrench";
    private static final NamespacedKey WRENCH_PDC_KEY = Keys.key("redstone_wrench");

    static {
        final ItemStack wrench = new ItemStack(Material.CARROT_ON_A_STICK, 1);
        wrench.editMeta(meta -> {
            meta.displayName(text(ITEM_NAME, style(TextDecoration.ITALIC.withState(false))));
            meta.setUnbreakable(true);
            meta.setCustomModelData(RotationWrenches.RESOURCE_PACK_MODEL_DATA);
            meta.getPersistentDataContainer().set(RotationWrenches.WRENCH_PDC_KEY, PersistentDataType.BOOLEAN, true);
        });
        WRENCH_RECIPE = new ShapedRecipe(RotationWrenches.WRENCH_RECIPE_KEY, wrench)
            .shape(
                " # ",
                " ##",
                "$  "
            ).setIngredient('#', Material.GOLD_INGOT)
            .setIngredient('$', Material.IRON_INGOT);
    }

    static boolean isWrench(final ItemStack stack) {
        final @Nullable ItemMeta meta = stack.getItemMeta();
        if (meta == null) return false;
        if (meta.getPersistentDataContainer().has(WRENCH_PDC_KEY)) {
            return true;
        }
        // legacy migration
        if (stack.getType() == Material.CARROT_ON_A_STICK && ITEM_NAME.equals(PlainTextComponentSerializer.plainText().serializeOrNull(meta.displayName())) && meta.hasCustomModelData() && meta.getCustomModelData() == RESOURCE_PACK_MODEL_DATA) {
            meta.getPersistentDataContainer().set(WRENCH_PDC_KEY, PersistentDataType.BOOLEAN, true);
            stack.setItemMeta(meta);
            return true;
        }
        return false;
    }

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }

    @Override
    protected Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(PlayerListener.class, WrenchListener.class);
    }

    @Override
    protected Collection<ModuleRecipe<?>> recipes() {
        return Set.of(new ModuleRecipe<>(WRENCH_RECIPE));
    }
}
