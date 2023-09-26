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
import java.util.Set;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.modules.ModuleRecipe;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kyori.adventure.text.Component.text;

class Lifecycle extends ModuleLifecycle {

    static final Component PACK_PROMPT = text("This resource pack adds a texture for the Redstone Wrench");

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
                player.setResourcePack(RotationWrenches.RESOURCE_PACK_URL, RotationWrenches.RESOURCE_PACK_HASH, PACK_PROMPT);
            });
        }

    }

    @Override
    public void onReload() {
        if (this.config.suggestResourcePack) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setResourcePack(RotationWrenches.RESOURCE_PACK_URL, RotationWrenches.RESOURCE_PACK_HASH, PACK_PROMPT);
            });
        }
    }
}
