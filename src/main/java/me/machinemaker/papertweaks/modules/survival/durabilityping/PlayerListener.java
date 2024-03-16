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
package me.machinemaker.papertweaks.modules.survival.durabilityping;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Range;
import com.google.inject.Inject;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.settings.ModuleSettings;
import me.machinemaker.papertweaks.tags.Tags;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.Damageable;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

class PlayerListener implements ModuleListener {

    private static final Object INSTANCE = new Object();
    final Cache<UUID, CachedSettings> settingsCache = CacheBuilder.newBuilder().expireAfterAccess(20, TimeUnit.MINUTES).build();
    private final Config config;
    private final Settings settings;
    @MonotonicNonNull Cache<UUID, Object> cooldownCache;

    @Inject
    PlayerListener(final Config config, final Settings settings) {
        this.config = config;
        this.cooldownCache = CacheBuilder.newBuilder().expireAfterWrite(config.notificationCooldown, TimeUnit.SECONDS).build();
        this.settings = settings;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(final PlayerQuitEvent event) {
        this.cooldownCache.invalidate(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDurabilityChange(final PlayerItemDamageEvent event) throws ExecutionException {
        if (this.cooldownCache.getIfPresent(event.getPlayer().getUniqueId()) == null
            && event.getItem().hasItemMeta()
            && event.getItem().getItemMeta() instanceof final Damageable damageable
            && event.getPlayer().hasPermission("vanillatweaks.durabilityping.notification")
            && Range.openClosed(1, this.config.usesLeft + 1).contains(event.getItem().getType().getMaxDurability() - damageable.getDamage())
        ) {
            final CachedSettings playerSettings = this.getCachedSettings(event.getPlayer());
            final Material type = event.getItem().getType();
            if ((!playerSettings.handPing() && Tags.DAMAGEABLE_TOOLS.isTagged(type)) || (!playerSettings.armorPing() && Tags.DAMAGEABLE_ARMOR.isTagged(type))) {
                return;
            }
            final Player player = event.getPlayer();
            if (playerSettings.sound()) {
                player.playSound(DurabilityPing.SOUND, Sound.Emitter.self());
            }
            playerSettings.displaySetting().sendMessage(player, this.createNotification(type, damageable.getDamage()));
            this.cooldownCache.put(event.getPlayer().getUniqueId(), INSTANCE);
        }
    }

    Component createNotification(final Material type, final int durability) {
        return translatable(
            "modules.durability-ping.notification.tool",
            RED,
            translatable(type, GOLD),
            text(type.getMaxDurability() - durability - 1, GOLD),
            text(type.getMaxDurability(), GOLD)
        );
    }

    private CachedSettings getCachedSettings(final Player player) throws ExecutionException {
        return this.settingsCache.get(player.getUniqueId(), () -> {
            final ModuleSettings.SettingGetter getter = this.settings.createGetter(player);
            return new CachedSettings(getter.getOrDefault(Settings.HAND_PING), getter.getOrDefault(Settings.ARMOR_PING), getter.getOrDefault(Settings.SOUND), getter.getOrDefault(Settings.DISPLAY));
        });
    }

    record CachedSettings(boolean handPing, boolean armorPing, boolean sound, Settings.DisplaySetting displaySetting) {}
}
