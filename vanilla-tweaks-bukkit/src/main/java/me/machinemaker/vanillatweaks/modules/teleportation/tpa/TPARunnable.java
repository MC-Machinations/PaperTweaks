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
package me.machinemaker.vanillatweaks.modules.teleportation.tpa;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.machinemaker.vanillatweaks.utils.runnables.TimerRunnable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@Singleton
class TPARunnable extends TimerRunnable {

    private final TPAManager manager;

    @Inject
    TPARunnable(Plugin plugin, TPAManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @Override
    public void run() {
        var iter = this.manager.requestsBySender.entrySet().iterator();
        while (iter.hasNext()) {
            var entry = iter.next();
            if (System.currentTimeMillis() > entry.getValue().cancelAfter()) {
                Bukkit.getScheduler().runTask(this.plugin, () -> this.manager.cancelRequest(entry.getValue()));
                iter.remove();
            }
        }
    }
}
