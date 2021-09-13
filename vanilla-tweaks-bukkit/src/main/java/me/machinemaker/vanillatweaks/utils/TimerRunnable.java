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
package me.machinemaker.vanillatweaks.utils;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class TimerRunnable implements Runnable {

    protected final Plugin plugin;
    private BukkitTask currentTask;

    @Inject
    protected TimerRunnable(Plugin plugin) {
        this.plugin = plugin;
    }

    public synchronized BukkitTask runTaskTimer(long delay, long period) throws IllegalStateException {
        checkNotYetScheduled();
        this.currentTask = Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period);
        return this.currentTask;
    }

    public synchronized BukkitTask runTaskTimerAsynchronously(long delay, long period) throws IllegalStateException {
        checkNotYetScheduled();
        this.currentTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period);
        return this.currentTask;
    }

    public synchronized void cancel() throws IllegalStateException{
        Bukkit.getScheduler().cancelTask(getTaskId());
    }

    public synchronized int getTaskId() throws IllegalStateException {
        checkScheduled();
        return this.currentTask.getTaskId();
    }

    private void checkScheduled() {
        if (this.currentTask == null || this.currentTask.isCancelled()) {
            throw new IllegalStateException("Not scheduled yet");
        }
    }

    private void checkNotYetScheduled() {
        if (this.currentTask != null && !this.currentTask.isCancelled()) {
            throw new IllegalStateException("Already scheduled as " + this.currentTask.getTaskId());
        }
    }
}
