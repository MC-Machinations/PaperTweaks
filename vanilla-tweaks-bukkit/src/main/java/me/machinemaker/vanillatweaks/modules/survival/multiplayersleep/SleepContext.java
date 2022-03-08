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
package me.machinemaker.vanillatweaks.modules.survival.multiplayersleep;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.format.*;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.translatable;

final class SleepContext {

    @Inject private static Config config;
    @Inject private static JavaPlugin plugin;
    @Inject private static BukkitAudiences audiences;

    private final World world;
    private final List<Player> sleepingPlayers = Lists.newArrayList();
    private final Map<Player, BukkitTask> sleepingTasks = Maps.newHashMap();

    private SleepContext(@NotNull World world) {
        this.world = world;
    }

    static SleepContext from(@Nullable World world) {
        if (world == null) {
            return null;
        }
        return new SleepContext(world);
    }

    public @NotNull World world() {
        return world;
    }

    public long sleepingCount() {
        return this.sleepingPlayers.size() + this.world.getPlayers().stream().filter(Player::isSleepingIgnored).count();
    }

    public double requiredPercent() {
        return getSleepingPercentage(this.world);
    }

    public List<Player> sleepingPlayers() {
        return this.sleepingPlayers;
    }

    public Set<Player> almostSleepingPlayers() {
        return this.sleepingTasks.keySet();
    }

    public void startSleeping(Player player) {
        if (player.isSleepingIgnored()) {
            return;
        }
        if (this.sleepingPlayers.remove(player)) {
            plugin.getLogger().log(Level.WARNING, "{0} was already recorded as fully asleep", player.getDisplayName());
        }
        if (this.sleepingTasks.containsKey(player)) {
            this.sleepingTasks.remove(player).cancel();
            plugin.getLogger().log(Level.WARNING, "{0} already had a scheduled sleep task", player.getDisplayName());
        }
        this.sleepingTasks.put(player, new PlayerBedCheckRunnable(player, this::addSleepingPlayer).runTaskTimer(plugin, 99L, 1L));
    }

    public void addSleepingPlayer(Player player) {
        Preconditions.checkArgument(player.getSleepTicks() >= 100, player.getDisplayName() + " is not deeply sleeping");
        if (player.isSleepingIgnored()) {
            this.sleepingPlayers.remove(player);
            return;
        }
        if (this.sleepingPlayers.remove(player)) {
            plugin.getLogger().log(Level.WARNING, "{0} already is in the list of sleeping players", player.getDisplayName());
        }
        this.sleepingTasks.remove(player).cancel();
        this.sleepingPlayers.add(player);
        recalculate(false);
    }

    public void removePlayer(Player player) {
        this.sleepingPlayers.remove(player);
        if (this.sleepingTasks.containsKey(player)) {
            BukkitTask task = this.sleepingTasks.remove(player);
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
        recalculate(true);
    }

    public long totalPlayerCount() {
        return this.world.getPlayers().size();
    }

    public void reset(boolean kickOut) {
        this.sleepingPlayers.forEach(player -> {
            if (kickOut) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> player.wakeup(false), 1L);
                audiences.player(player).sendMessage(translatable("modules.multiplayer-sleep.reload.kick-out-of-bed", NamedTextColor.RED));
            }
        });
        this.sleepingPlayers.clear();
        this.sleepingTasks.forEach((player, bukkitTask) -> {
            if (kickOut) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> player.wakeup(false), 1L);
                audiences.player(player).sendMessage(translatable("modules.multiplayer-sleep.reload.kick-out-of-bed", NamedTextColor.RED));
            }
            if (!bukkitTask.isCancelled()) {
                bukkitTask.cancel();
            }
        });
        this.sleepingTasks.clear();
        recalculate(true);
    }

    public boolean shouldSkip() {
        if (this.sleepingPlayers.isEmpty() || this.totalPlayerCount() == 0) {
            return false;
        }
        return (double) this.sleepingPlayers.size() / (double) totalPlayerCount() >= this.requiredPercent();
    }

    private void recalculate(boolean isRemoval) {
        if (shouldSkip()) {
            this.world.getPlayers().forEach(player -> {
                Settings.DISPLAY.getOrDefault(player).notifyFinal(player, this);
            });
            if (this.world.hasStorm() || config.alwaysResetWeatherCycle) {
                this.world.setWeatherDuration(0);
                this.world.setStorm(false);
                this.world.setThunderDuration(0);
                this.world.setThundering(false);
            }
            this.reset(false);
        } else {
            this.world.getPlayers().forEach(player -> {
                Settings.DISPLAY.getOrDefault(player).notify(player, this, isRemoval);
            });
        }
    }

    static double getSleepingPercentage(World world) {
        return Math.max(requireNonNull(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)), 100) / 100D;
    }
}
