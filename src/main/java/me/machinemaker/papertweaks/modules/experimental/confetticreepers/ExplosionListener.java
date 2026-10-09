/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2026 Machine_Maker
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
package me.machinemaker.papertweaks.modules.experimental.confetticreepers;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import me.machinemaker.papertweaks.modules.ModuleListener;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public class ExplosionListener implements ModuleListener {

    private final Config config;

    @Inject
    public ExplosionListener(final Config config) {
        this.config = config;
    }

    private static FireworkEffect createFireworkEffect() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final int count = random.nextInt(2, 5);
        final List<Color> colors = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            colors.add(Color.fromRGB(random.nextInt(0x1000000)));
        }
        return FireworkEffect.builder()
            .flicker(true)
            .trail(false)
            .with(FireworkEffect.Type.CREEPER)
            .withColor(colors)
            .withFade(Color.fromRGB(random.nextInt(0x1000000)))
            .build();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosionPrime(final ExplosionPrimeEvent event) {
        if (event.getEntityType() != EntityType.CREEPER) return;
        if (this.config.allowChargedCreepers) {
            Creeper creeper = (Creeper) event.getEntity();
            if (creeper.isPowered()) return;
        }
        if (ThreadLocalRandom.current().nextDouble() < this.config.chance) {
            event.setCancelled(true);
            final Entity creeper = event.getEntity();
            final Location location = creeper.getLocation();
            creeper.remove();

            final Firework firework = location.getWorld().spawn(location, Firework.class, fw -> {
                final FireworkMeta fireworkMeta = fw.getFireworkMeta();
                fireworkMeta.setPower(0);
                fireworkMeta.addEffect(createFireworkEffect());
                fw.setFireworkMeta(fireworkMeta);
            });
            firework.detonate();

            location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.HOSTILE, 1.0F, 1.0F);
            location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, SoundCategory.HOSTILE, 1.0F, 1.0F);
        }
    }
}
