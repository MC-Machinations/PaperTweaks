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
package me.machinemaker.vanillatweaks.modules.survival.coordinateshud;

import com.google.inject.Inject;
import java.util.Set;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.modules.ModuleRecipe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

class Lifecycle extends ModuleLifecycle {

    private final Config config;
    private final HUDRunnable hudRunnable;
    private BukkitTask task;

    @Inject
    public Lifecycle(final JavaPlugin plugin, final Set<ModuleCommand> commands, final Set<ModuleListener> listeners, final Set<ModuleConfig> configs, final Config config, final HUDRunnable hudRunnable, final Set<ModuleRecipe<?>> moduleRecipes) {
        super(plugin, commands, listeners, configs, moduleRecipes);
        this.config = config;
        this.hudRunnable = hudRunnable;
    }

    @Override
    public void onEnable() {
        this.startTask();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (!HUDRunnable.COORDINATES_HUD_KEY.has(player)) {
                HUDRunnable.COORDINATES_HUD_KEY.setTo(player, this.config.enabledByDefault);
            }
            if (Boolean.TRUE.equals(HUDRunnable.COORDINATES_HUD_KEY.getFrom(player))) {
                this.hudRunnable.addPlayer(player);
            }
        }
    }

    @Override
    public void onReload() {
        this.startTask();
    }

    private void startTask() {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(this.getPlugin(), this.hudRunnable, 1L, this.config.ticks);
    }

    @Override
    public void onDisable(final boolean isShutdown) {
        if (this.task != null) {
            this.task.cancel();
        }
        this.hudRunnable.clearPlayers();
    }

}
