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
package me.machinemaker.vanillatweaks.utils.runnables;

import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public abstract class ItemDropFinder extends BukkitRunnable {

    private final Item item;
    private final long maxRuns;
    private long counter;

    protected ItemDropFinder(@NotNull Item item, long maxRuns) {
        this.item = item;
        this.maxRuns = maxRuns;
    }

    @Override
    public final void run() {
        if (this.item.isDead()) {
            this.cancel();
            return;
        }

        if (this.counter >= maxRuns) {
            this.cancel();
            return;
        }

        if (this.failCheck(this.item)) {
            this.cancel();
            return;
        }

        if (this.successCheck(this.item)) {
            this.onSuccess(this.item);
            this.cancel();
            return;
        }

        counter++;
    }

    public boolean failCheck(@NotNull Item item) {
        return false;
    }

    public abstract boolean successCheck(@NotNull Item item);

    public void onSuccess(@NotNull Item item) {}
}
