/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules.hermitcraft.tag;

import com.google.inject.Inject;
import java.time.Duration;
import me.machinemaker.papertweaks.modules.ModuleListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

class PlayerListener implements ModuleListener {

    private final TagManager tagManager;
    private final Config config;

    @Inject
    PlayerListener(final TagManager tagManager, final Config config) {
        this.tagManager = tagManager;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(final EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.THORNS && event.getDamager() instanceof final Player damager && event.getEntity() instanceof final Player damagee) {
            if (!damager.getInventory().getItemInMainHand().isSimilar(Tag.TAG_ITEM)) return;
            if (!Tag.IT.has(damager)) return;
            if (Tag.COOLDOWN.getFromOrDefault(damager, Long.MIN_VALUE) > System.currentTimeMillis()) {
                final Duration timeLeft = Duration.ofSeconds((Tag.COOLDOWN.getFrom(damager) - System.currentTimeMillis()) / 1000);
                damager.sendMessage(translatable("modules.tag.tag.fail.cooldown.notice", RED));
                damager.sendMessage(translatable("modules.tag.tag.fail.cooldown.time-left", YELLOW, text(String.format("%d:%02d:%02d", timeLeft.toHours(), timeLeft.toMinutesPart(), timeLeft.toSecondsPart()))));
                return;
            }
            if (this.tagManager.setAsIt(damager, damagee)) {
                this.tagManager.removeAsIt(damager);
                if (this.config.showMessages) {
                    Bukkit.getServer().sendMessage(translatable("modules.tag.tag.success", YELLOW, text(damager.getName()), text(damagee.getName())));
                }
            }
        }
    }

}
