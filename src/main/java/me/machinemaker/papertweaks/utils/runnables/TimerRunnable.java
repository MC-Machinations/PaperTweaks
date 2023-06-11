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
package me.machinemaker.papertweaks.utils.runnables;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class TimerRunnable implements Runnable {

    protected final Plugin plugin;
    private @Nullable BukkitTask currentTask;

    @Inject
    protected TimerRunnable(final Plugin plugin) {
        this.plugin = plugin;
    }

    private static void checkScheduled(final @Nullable BukkitTask task) {
        if (task == null || task.isCancelled()) {
            throw new IllegalStateException("Not scheduled yet");
        }
    }

    private static void checkNotYetScheduled(final @Nullable BukkitTask task) {
        if (task != null && !task.isCancelled()) {
            throw new IllegalStateException("Already scheduled as " + task.getTaskId());
        }
    }

    public synchronized BukkitTask runTaskTimer(final long delay, final long period) throws IllegalStateException {
        checkNotYetScheduled(this.currentTask);
        this.currentTask = Bukkit.getScheduler().runTaskTimer(this.plugin, this, delay, period);
        return this.currentTask;
    }

    public synchronized BukkitTask runTaskTimerAsynchronously(final long delay, final long period) throws IllegalStateException {
        checkNotYetScheduled(this.currentTask);
        this.start();
        this.currentTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this, delay, period);
        return this.currentTask;
    }

    protected void start() {
    }

    public synchronized void cancel() {
        try {
            Bukkit.getScheduler().cancelTask(this.getTaskId());
        } catch (final IllegalStateException ignored) {
        }
    }

    public synchronized int getTaskId() throws IllegalStateException {
        checkScheduled(this.currentTask);
        return this.currentTask.getTaskId();
    }
}
