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
package me.machinemaker.papertweaks.modules.survival.durabilityping;

import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.modules.ModuleRecipe;
import org.bukkit.plugin.java.JavaPlugin;

class Lifecycle extends ModuleLifecycle {

    private final Config config;
    private final PlayerListener listener;

    @Inject
    Lifecycle(final JavaPlugin plugin, final Set<ModuleCommand> commands, final Set<ModuleListener> listeners, final Set<ModuleConfig> configs, final Config config, final PlayerListener listener, final Set<ModuleRecipe<?>> moduleRecipes) {
        super(plugin, commands, listeners, configs, moduleRecipes);
        this.config = config;
        this.listener = listener;
    }

    @Override
    public void onEnable() {
        this.refreshCaches();
    }

    @Override
    public void onReload() {
        this.refreshCaches();
    }

    @Override
    public void onDisable(final boolean isShutdown) {
        this.listener.cooldownCache.invalidateAll();
        this.listener.settingsCache.invalidateAll();
    }

    private void refreshCaches() {
        this.listener.cooldownCache = CacheBuilder.newBuilder().expireAfterWrite(this.config.notificationCooldown, TimeUnit.SECONDS).build();
        this.listener.settingsCache.invalidateAll();
    }
}
