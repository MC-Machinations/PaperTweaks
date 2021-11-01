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
package me.machinemaker.vanillatweaks.modules.hermitcraft.tag;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.time.Duration;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class PlayerListener implements ModuleListener {

    private final TagManager tagManager;
    private final Config config;
    private final BukkitAudiences audiences;

    @Inject
    PlayerListener(TagManager tagManager, Config config, BukkitAudiences audiences) {
        this.tagManager = tagManager;
        this.config = config;
        this.audiences = audiences;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.THORNS && event.getDamager() instanceof Player damager && event.getEntity() instanceof Player damagee) {
            if (!damager.getInventory().getItemInMainHand().isSimilar(Tag.TAG_ITEM)) return;
            if (!Tag.IT.has(damager)) return;
            Audience damagerAudience = this.audiences.player(damager);
            if (Tag.COOLDOWN.getFromOrDefault(damager, Long.MIN_VALUE) > System.currentTimeMillis()) {
                Duration timeLeft = Duration.ofSeconds((Tag.COOLDOWN.getFrom(damager) - System.currentTimeMillis()) / 1000);
                damagerAudience.sendMessage(translatable("modules.tag.tag.fail.cooldown.notice", RED));
                damagerAudience.sendMessage(translatable("modules.tag.tag.fail.cooldown.time-left", YELLOW, text(String.format("%d:%02d:%02d", timeLeft.toHours(), timeLeft.toMinutesPart(), timeLeft.toSecondsPart()))));
                return;
            }
            if (this.tagManager.setAsIt(damager, damagee)) {
                this.tagManager.removeAsIt(damager);
                if (this.config.showMessages) {
                    this.audiences.players().sendMessage(translatable("modules.tag.tag.success", YELLOW, text(damager.getName()), text(damagee.getName())));
                }
            }
        }
    }

}
