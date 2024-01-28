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
package me.machinemaker.papertweaks.utils.runnables;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class TeleportRunnable extends BukkitRunnable {

    private static final double MOVEMENT_THRESHOLD = 0.01;

    protected final Player player;
    protected final Location originalLoc;
    protected final Location teleportLoc;
    private long tickDelay;

    protected TeleportRunnable(final Player player, final Location teleportLoc, final long tickDelay) {
        Preconditions.checkArgument(tickDelay > 0, "tickDelay must be positive");
        this.player = player;
        this.originalLoc = player.getLocation();
        this.teleportLoc = teleportLoc;
        this.tickDelay = tickDelay;
    }

    @Override
    public final void run() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.onEnd();
            this.cancel();
            return;
        }
        if (this.tickDelay <= 0) {
            this.onTeleport();
            if (this.teleportLoc.getChunk().isLoaded()) {
                this.player.teleport(this.teleportLoc);
            } else {
                this.player.teleportAsync(this.teleportLoc);
            }
            this.onEnd();
            this.cancel();
            return;
        }
        if (this.originalLoc.distanceSquared(this.player.getLocation()) >= MOVEMENT_THRESHOLD) {
            this.onMove();
            this.onEnd();
            this.cancel();
            return;
        }

        this.tickDelay--;
    }

    public void onTeleport() {
    }

    public void onMove() {
    }

    public void onEnd() {
    }
}
