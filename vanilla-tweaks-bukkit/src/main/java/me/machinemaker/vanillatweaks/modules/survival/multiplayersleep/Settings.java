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

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.utils.Keys;
import me.machinemaker.vanillatweaks.menus.parts.enums.PreviewableMenuEnum;
import me.machinemaker.vanillatweaks.settings.ModuleSettings;
import me.machinemaker.vanillatweaks.settings.types.PlayerSetting;
import me.machinemaker.vanillatweaks.settings.SettingWrapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Settings extends ModuleSettings<PlayerSetting<?>> {

    public static final SettingWrapper.PDC<DisplaySetting> DISPLAY = SettingWrapper.pdc(Keys.key("mps.display_setting"));

    @Inject private static Config config;
    @Inject private static JavaPlugin plugin;
    @Inject private static BukkitAudiences audiences;

    @Inject
    Settings(Config config) {
        register(PlayerSetting.ofEnum(DISPLAY, DisplaySetting.class, () -> config.defaultDisplaySetting));
    }

    enum DisplaySetting implements PreviewableMenuEnum<DisplaySetting> {

        HIDDEN("Display: Hidden") {
            @Override
            void notify(Player player, SleepContext context, boolean isBedLeave) { /*pass*/ }

            @Override
            void notifyFinal(Player player, SleepContext context) { /*pass*/ }

            @Override
            public @NotNull Component build(@NotNull DisplaySetting selected, @NotNull String commandPrefix) {
                return super.buildWithoutPreview(selected, commandPrefix);
            }

        },
        BOSS_BAR("Display: Boss Bar") {
            @Override
            void notify(Player player, SleepContext context, boolean isBedLeave) {
                Audience audience = audiences.player(player);
                BossBar bossBar = Lifecycle.BOSS_BARS.get(player.getWorld().getUID());
                if (context.sleepingPlayers().isEmpty()) {
                    if (bossBar != null) {
                        audience.hideBossBar(bossBar);
                    }
                    return;
                } else if (bossBar == null) {
                    bossBar = BossBar.bossBar(
                            bossBarName(context.sleepingCount(), context.totalPlayerCount()),
                            context.sleepingCount() / (float) context.totalPlayerCount(),
                            config.bossBarColor,
                            BossBar.Overlay.PROGRESS
                    );
                    Lifecycle.BOSS_BARS.put(player.getWorld().getUID(), bossBar);
                } else {
                    bossBar.name(bossBarName(context.sleepingCount(), context.totalPlayerCount()));
                    bossBar.progress(context.sleepingCount() / (float) context.totalPlayerCount());
                    bossBar.color(config.bossBarColor);
                }
                audience.showBossBar(bossBar);
            }

            @Override
            void notifyFinal(Player player, SleepContext context) {
                BossBar bossBar = Lifecycle.BOSS_BARS.get(player.getWorld().getUID());
                if (bossBar != null) {
                    bossBar.name(bossBarName(context.totalPlayerCount(), context.totalPlayerCount()));
                    bossBar.color(config.bossBarColor);
                    bossBar.progress(1f);
                    Audience audience = audiences.player(player);
                    audience.showBossBar(bossBar);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> audience.hideBossBar(bossBar), 60L);
                }
            }

            @Override
            public void preview(Player player) {
                Audience audience = audiences.player(player);
                BossBar bossBar = BOSS_BAR_PREVIEW.apply(config);
                audience.showBossBar(bossBar);
                Bukkit.getScheduler().runTaskLater(plugin, () -> audience.hideBossBar(bossBar), 100L);
            }
        },
        ACTION_BAR("Display: Action Bar") {
            @Override
            void notify(Player player, SleepContext context, boolean isBedLeave) {
                if (isBedLeave) return;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (context.shouldSkip() || context.sleepingPlayers().isEmpty()) {
                            this.cancel();
                            return;
                        }
                        sendNotification(player, context.sleepingCount(), context.totalPlayerCount());
                    }
                }.runTaskTimerAsynchronously(plugin, 1L, 10L);
            }

            @Override
            void notifyFinal(Player player, SleepContext context) {
                sendNotification(player, context.sleepingCount(), context.totalPlayerCount());
            }

            @Override
            public void preview(Player player) {
                sendNotification(player, 10, 15);
            }

            private void sendNotification(Player player, long sleepingCount, long totalCount) {
                audiences.player(player).sendActionBar(translatable("modules.multiplayer-sleep.display.action-bar.player-sleeping", YELLOW, text(sleepingCount), text(totalCount)));
            }
        },
        CHAT("Display: Chat") {
            @Override
            void notify(Player player, SleepContext context, boolean isBedLeave) {
                if (isBedLeave) return;
                notify(player, Iterables.getLast(context.sleepingPlayers()).getDisplayName(), context.sleepingCount(), context.totalPlayerCount());
            }

            private void notify(Player player, String playerName, long sleepingCount, long totalCount) {
                audiences.player(player).sendMessage(translatable("modules.multiplayer-sleep.display.chat.player-sleeping", GOLD, text(playerName, YELLOW), text(sleepingCount, YELLOW), text(totalCount, YELLOW)));
            }

            @Override
            void notifyFinal(Player player, SleepContext context) {
                audiences.player(player).sendMessage(translatable("modules.multiplayer-sleep.display.chat.last-player-sleeping", GOLD, text(Iterables.getLast(context.sleepingPlayers()).getDisplayName(), YELLOW)));
            }

            @Override
            public void preview(Player player) {
                notify(player, "Machine_Maker", 10, 15);
            }
        };

        private static final Function<Config, BossBar> BOSS_BAR_PREVIEW = (config) -> BossBar.bossBar(bossBarName(5, 10), 0.5f, config.bossBarColor, BossBar.Overlay.PROGRESS);

        private final String label;

        DisplaySetting(String label) {
            this.label = label;
        }

        @Override
        public @NotNull Component label() {
            return text(this.label);
        }

        abstract void notify(Player player, SleepContext context, boolean isBedLeave);

        abstract void notifyFinal(Player player, SleepContext context);

        @Override
        public @NotNull String previewCommandPrefix() {
            return "/multiplayersleep config preview_display";
        }
    }

    static Component bossBarName(long sleepingCount, long totalCount) {
        return translatable("modules.multiplayer-sleep.display.boss-bar.title", text(sleepingCount), text(totalCount));
    }
}
