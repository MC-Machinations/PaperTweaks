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
package me.machinemaker.papertweaks.modules.survival.multiplayersleep;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import java.util.function.Function;
import me.machinemaker.papertweaks.menus.parts.enums.PreviewableMenuEnum;
import me.machinemaker.papertweaks.settings.ModuleSettings;
import me.machinemaker.papertweaks.settings.SettingKey;
import me.machinemaker.papertweaks.settings.types.PlayerSetting;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Settings extends ModuleSettings<Player, PlayerSetting<?>> {

    public static final SettingKey<DisplaySetting> DISPLAY = new SettingKey<>("mps.display_setting");

    @Inject
    private static Config config;
    @Inject
    private static JavaPlugin plugin;

    @Inject
    Settings(final Config config) {
        this.register(PlayerSetting.ofEnum(DISPLAY, DisplaySetting.class, () -> config.defaultDisplaySetting));
        // register(GameRuleSetting.ofInt(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 0, 100));
    }

    static Component bossBarName(final long sleepingCount, final long totalCount) {
        return translatable("modules.multiplayer-sleep.display.boss-bar.title", text(sleepingCount), text(totalCount));
    }

    enum DisplaySetting implements PreviewableMenuEnum<DisplaySetting> {

        HIDDEN("Hidden") {
            @Override
            void notify(final Player player, final SleepContext context, final boolean isBedLeave) { /*pass*/ }

            @Override
            void notifyFinal(final Player player, final SleepContext context) { /*pass*/ }

            @Override
            public Component build(final DisplaySetting selected, final String labelKey, final String commandPrefix, final String optionKey) {
                return this.buildWithoutPreview(selected, labelKey, commandPrefix, optionKey);
            }

        },
        BOSS_BAR("Boss Bar") {
            @Override
            void notify(final Player player, final SleepContext context, final boolean isBedLeave) {
                BossBar bossBar = Lifecycle.BOSS_BARS.get(player.getWorld().getUID());
                if (context.sleepingPlayers().isEmpty()) {
                    if (bossBar != null) {
                        player.hideBossBar(bossBar);
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
                player.showBossBar(bossBar);
            }

            @Override
            void notifyFinal(final Player player, final SleepContext context) {
                final BossBar bossBar = Lifecycle.BOSS_BARS.get(player.getWorld().getUID());
                if (bossBar != null) {
                    bossBar.name(bossBarName(context.totalPlayerCount(), context.totalPlayerCount()));
                    bossBar.color(config.bossBarColor);
                    bossBar.progress(1f);
                    player.showBossBar(bossBar);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.hideBossBar(bossBar), 60L);
                }
            }

            @Override
            public void preview(final Player player) {
                final BossBar bossBar = BOSS_BAR_PREVIEW.apply(config);
                player.showBossBar(bossBar);
                Bukkit.getScheduler().runTaskLater(plugin, () -> player.hideBossBar(bossBar), 100L);
            }
        },
        ACTION_BAR("Action Bar") {
            @Override
            void notify(final Player player, final SleepContext context, final boolean isBedLeave) {
                if (isBedLeave) return; // skip because runnable handles the task
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
            void notifyFinal(final Player player, final SleepContext context) {
                this.sendNotification(player, context.sleepingCount(), context.totalPlayerCount());
            }

            @Override
            public void preview(final Player player) {
                this.sendNotification(player, 10, 15);
            }

            private void sendNotification(final Player player, final long sleepingCount, final long totalCount) {
                player.sendActionBar(translatable("modules.multiplayer-sleep.display.action-bar.player-sleeping", YELLOW, text(sleepingCount), text(totalCount)));
            }
        },
        CHAT("Chat") {
            @Override
            void notify(final Player player, final SleepContext context, final boolean isBedLeave) {
                if (isBedLeave) return;
                this.notify(player, Iterables.getLast(context.sleepingPlayers()).displayName(), context.sleepingCount(), context.totalPlayerCount());
            }

            private void notify(final Player player, final Component playerName, final long sleepingCount, final long totalCount) {
                player.sendMessage(translatable("modules.multiplayer-sleep.display.chat.player-sleeping", GOLD, playerName.color(YELLOW), text(sleepingCount, YELLOW), text(totalCount, YELLOW)));
            }

            @Override
            void notifyFinal(final Player player, final SleepContext context) {
                player.sendMessage(translatable("modules.multiplayer-sleep.display.chat.last-player-sleeping", GOLD, Iterables.getLast(context.sleepingPlayers()).displayName().color(YELLOW)));
            }

            @Override
            public void preview(final Player player) {
                this.notify(player, text("Machine_Maker"), 10, 15);
            }
        };

        private static final Function<Config, BossBar> BOSS_BAR_PREVIEW = (config) -> BossBar.bossBar(bossBarName(5, 10), 0.5f, config.bossBarColor, BossBar.Overlay.PROGRESS);

        private final String label;

        DisplaySetting(final String label) {
            this.label = label;
        }

        @Override
        public Component label() {
            return text(this.label);
        }

        abstract void notify(Player player, SleepContext context, boolean isBedLeave);

        abstract void notifyFinal(Player player, SleepContext context);

        @Override
        public String previewCommandPrefix() {
            return "/multiplayersleep config preview_display";
        }
    }
}
