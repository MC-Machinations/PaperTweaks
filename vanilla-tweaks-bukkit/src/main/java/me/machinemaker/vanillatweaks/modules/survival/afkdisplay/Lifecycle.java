/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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
package me.machinemaker.vanillatweaks.modules.survival.afkdisplay;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.modules.ModuleRecipe;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;

class Lifecycle extends ModuleLifecycle {

    private final AFKRunnable afkRunnable;
    private BukkitTask task;

    @Inject
    protected Lifecycle(JavaPlugin plugin, Set<ModuleCommand> commands, Set<ModuleListener> listeners, Set<ModuleConfig> configs, AFKRunnable afkRunnable, Set<ModuleRecipe<?>> moduleRecipes) {
        super(plugin, commands, listeners, configs, moduleRecipes);
        this.afkRunnable = afkRunnable;
    }

    @Override
    public void onEnable() {
        Bukkit.getOnlinePlayers().forEach(this.afkRunnable::addPlayer);
        startTask();
    }

    @Override
    public void onReload() {
        startTask();
    }

    @Override
    public void onDisable(boolean isShutdown) {
        if (this.task != null) {
            this.task.cancel();
        }
        this.afkRunnable.clear();
    }

    private void startTask() {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(this.getPlugin(), this.afkRunnable, 1L, 20L);
    }
}
