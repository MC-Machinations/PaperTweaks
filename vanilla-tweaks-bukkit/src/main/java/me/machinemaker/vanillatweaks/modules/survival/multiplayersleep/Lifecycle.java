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
package me.machinemaker.vanillatweaks.modules.survival.multiplayersleep;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

class Lifecycle extends ModuleLifecycle {

    static final Map<UUID, BossBar> BOSS_BARS = Maps.newHashMap();

    private final BukkitAudiences audiences;
    private final Config config;
    private final PlayerListener listener;

    @Inject
    Lifecycle(JavaPlugin plugin, Set<ModuleCommand> commands, Set<ModuleListener> listeners, Set<ModuleConfig> configs, BukkitAudiences audiences, Config config, PlayerListener listener) {
        super(plugin, commands, listeners, configs);
        this.audiences = audiences;
        this.config = config;
        this.listener = listener;
    }

    @Override
    public void onEnable() {
        this.validateWorldList(true);
    }

    @Override
    public void onReload() {
        this.validateWorldList(false);
        this.resetSleepContexts(true);
    }

    @Override
    public void onDisable() {
        resetSleepContexts(false);
    }

    private void validateWorldList(boolean firstLoad) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            BOSS_BARS.values().forEach(bossBar -> {
                audiences.player(player).hideBossBar(bossBar);
            });
        });
        BOSS_BARS.clear();
        this.config.worlds(firstLoad).forEach(world -> {
            BOSS_BARS.put(world.getUID(), BossBar.bossBar(translatable("modules.multiplayer-sleep.display.boss-bar.title", text(0), text(0)), 0.0f, this.config.bossBarColor, BossBar.Overlay.PROGRESS));
        });

    }

    private void resetSleepContexts(boolean kickOut) {
        MultiplayerSleep.SLEEP_CONTEXT_MAP.forEach((uuid, sleepContext) -> {
            sleepContext.reset(kickOut);
        });
        MultiplayerSleep.SLEEP_CONTEXT_MAP.clear();
    }
}
