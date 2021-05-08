/*
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
package me.machinemaker.vanillatweaks.modules.survival.durabilityping;

import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.concurrent.TimeUnit;

class Lifecycle extends ModuleLifecycle {

    private final DurabilityPing durabilityPing;
    private final Config config;
    private final PlayerListener listener;

    @Inject
    Lifecycle(JavaPlugin plugin, Set<ModuleCommand> commands, Set<ModuleListener> listeners, Set<ModuleConfig> configs, DurabilityPing durabilityPing, Config config, PlayerListener listener) {
        super(plugin, commands, listeners, configs);
        this.durabilityPing = durabilityPing;
        this.config = config;
        this.listener = listener;
    }

    @Override
    public void onEnable() {
        refreshCaches();
        if (this.config.enabledByDefault) {
            Bukkit.getOnlinePlayers().forEach(this.durabilityPing::setToPing);
        }
    }

    @Override
    public void onReload() {
        refreshCaches();
        if (this.config.enabledByDefault) {
            Bukkit.getOnlinePlayers().forEach(this.durabilityPing::setToPing);
        }
    }

    @Override
    public void onDisable() {
        this.listener.cooldownCache.invalidateAll();
        this.listener.settingsCache.invalidateAll();
    }

    private void refreshCaches() {
        this.listener.cooldownCache = CacheBuilder.newBuilder().expireAfterWrite(config.notificationCooldown, TimeUnit.SECONDS).build();
        this.listener.settingsCache.invalidateAll();
    }
}
