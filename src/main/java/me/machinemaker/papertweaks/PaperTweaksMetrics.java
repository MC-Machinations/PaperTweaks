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
package me.machinemaker.papertweaks;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
public class PaperTweaksMetrics {

    private static final int PLUGIN_ID = 8141;

    private Metrics metrics;

    @Inject
    PaperTweaksMetrics(final PaperTweaksConfig config, final JavaPlugin plugin) {
        if (config.metricsEnabled) {
            this.metrics = new Metrics(plugin, PLUGIN_ID);
        }
    }

    public boolean isRunning() {
        return this.metrics != null;
    }

}
