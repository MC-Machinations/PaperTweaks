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
package me.machinemaker.vanillatweaks.modules.survival.durabilityping;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Range;
import com.google.common.util.concurrent.Callables;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.tags.Tags;
import me.machinemaker.vanillatweaks.utils.Keys;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

class PlayerListener implements ModuleListener {

    private static final Object INSTANCE = new Object();

    private final DurabilityPing durabilityPing;
    private final Config config;
    private final BukkitAudiences audiences;
    final Cache<UUID, Settings.Instance> settingsCache = CacheBuilder.newBuilder().expireAfterAccess(20, TimeUnit.MINUTES).build();
    @MonotonicNonNull Cache<UUID, Object> cooldownCache;

    @Inject
    PlayerListener(DurabilityPing durabilityPing, Config config, BukkitAudiences audiences) {
        this.durabilityPing = durabilityPing;
        this.config = config;
        this.audiences = audiences;
        this.cooldownCache = CacheBuilder.newBuilder().expireAfterWrite(config.notificationCooldown, TimeUnit.SECONDS).build();
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().getPersistentDataContainer().has(durabilityPing.pingKey, PersistentDataType.INTEGER) && config.enabledByDefault) {
            durabilityPing.setToPing(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.cooldownCache.invalidate(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDurabilityChange(PlayerItemDamageEvent event) throws ExecutionException {
        if (
                durabilityPing.shouldPing(event.getPlayer())
                        && this.cooldownCache.getIfPresent(event.getPlayer().getUniqueId()) == null
                        && event.getItem().hasItemMeta()
                        && event.getItem().getItemMeta() instanceof Damageable damageable
                        && event.getPlayer().hasPermission("vanillatweaks.durabilityping")
                        && Range.openClosed(1, config.usesLeft + 1).contains(event.getItem().getType().getMaxDurability() - damageable.getDamage())
        ) {
            Settings.Instance playerSettings = settingsCache.get(event.getPlayer().getUniqueId(), Callables.returning(Settings.from(event.getPlayer())));
            Material type = event.getItem().getType();
            if ((!playerSettings.handPing() && Tags.DAMAGEABLE_TOOLS.isTagged(type)) || (!playerSettings.armorPing() && Tags.DAMAGEABLE_ARMOR.isTagged(type))) {
                return;
            }
            Audience audience = audiences.player(event.getPlayer());
            if (playerSettings.sound()) {
                audience.playSound(DurabilityPing.SOUND, Sound.Emitter.self());
            }
            playerSettings.displaySetting().sendMessage(audience, createNotification(type, damageable.getDamage()));
            this.cooldownCache.put(event.getPlayer().getUniqueId(), INSTANCE);
        }
    }

    Component createNotification(Material type, int durability) {
        return translatable(
                "modules.durability-ping.notification.tool",
                NamedTextColor.RED,
                translatable(Keys.itemTranslationKey(type), NamedTextColor.GOLD),
                text(type.getMaxDurability() - durability - 1, NamedTextColor.GOLD),
                text(type.getMaxDurability(), NamedTextColor.GOLD)
        );
    }
}
