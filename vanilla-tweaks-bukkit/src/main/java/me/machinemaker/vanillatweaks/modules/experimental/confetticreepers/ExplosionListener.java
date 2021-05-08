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
package me.machinemaker.vanillatweaks.modules.experimental.confetticreepers;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.concurrent.ThreadLocalRandom;

public class ExplosionListener implements ModuleListener {

    private static final FireworkEffect fireworkEffect = FireworkEffect.builder()
            .flicker(false)
            .trail(false)
            .with(FireworkEffect.Type.BURST)
            .withColor(
                    Color.fromRGB(11743532),
                    Color.fromRGB(15435844),
                    Color.fromRGB(14602026),
                    Color.fromRGB(4312372),
                    Color.fromRGB(6719955),
                    Color.fromRGB(8073150),
                    Color.fromRGB(14188952)
            ).build();

    private final Config config;

    @Inject
    public ExplosionListener(Config config) {
        this.config = config;
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (event.getEntityType() != EntityType.CREEPER) return;
        if (ThreadLocalRandom.current().nextDouble() < config.chance) {
            event.setFire(false);
            event.setRadius(0);
            event.getEntity().getWorld().spawn(event.getEntity().getLocation(), Firework.class, firework -> {
                FireworkMeta fireworkMeta = firework.getFireworkMeta();
                fireworkMeta.setPower(0);
                fireworkMeta.addEffect(fireworkEffect);
                firework.setFireworkMeta(fireworkMeta);
                firework.detonate();
            });
        }
    }
}
