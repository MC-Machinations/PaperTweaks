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
package me.machinemaker.vanillatweaks.modules.hermitcraft.thundershrine;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.machinemaker.vanillatweaks.utils.Entities;
import me.machinemaker.vanillatweaks.utils.runnables.TimerRunnable;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@Singleton
class ShrineRunnable extends TimerRunnable {

    private final BukkitAudiences audiences;

    @Inject
    ShrineRunnable(Plugin plugin, BukkitAudiences audiences) {
        super(plugin);
        this.audiences = audiences;
    }

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            for (AreaEffectCloud cloud : Entities.getEntitiesOfType(AreaEffectCloud.class, world, ThunderShrine.SHRINE::has)) {
                Item item = Entities.getSingleNearbyEntityOfType(Item.class, cloud.getLocation(), 0.5, 0.5, 0.5, i -> i.getItemStack().getType() == Material.NETHER_STAR && i.getItemStack().getAmount() == 1);
                if (item != null) {
                    item.remove();
                    world.spawnParticle(Particle.FLAME, cloud.getLocation(), 100, 0, 0, 0, 0.5);
                    world.playSound(cloud.getLocation(), Sound.ITEM_FIRECHARGE_USE, SoundCategory.MASTER, 1.0f, 1.0f);
                    world.strikeLightning(cloud.getLocation());
                    world.setClearWeatherDuration(0);
                    world.setWeatherDuration(6000);
                    world.setThunderDuration(6000);
                    world.setStorm(true);
                    world.setThundering(true);
                    for (Player player : Entities.getNearbyEntitiesOfType(Player.class, cloud.getLocation(), 5, 5, 5)) {
                        this.audiences.player(player).sendMessage(translatable("modules.thunder-shrine.ritual.success", RED));
                    }
                } else {
                    world.spawnParticle(Particle.ENCHANTMENT_TABLE, cloud.getLocation(), 1, 0.1, 0.1, 0.1, 1);
                }
            }
        }
    }
}
