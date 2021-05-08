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
package me.machinemaker.vanillatweaks.modules.survival.workstationhighlights;

import cloud.commandframework.minecraft.extras.RichDescription;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.MappedTranslatableComponent.mapped;
import static net.kyori.adventure.text.MappedTranslatableComponent.mappedBuilder;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ModuleCommand {

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        var builder = manager
                .commandBuilder("find-workstation", RichDescription.of(mapped("modules.workstation-highlights.commands.root")), "fworkstation", "fwork")
                .senderType(PlayerCommandDispatcher.class);

        manager.command(
                builder.permission(ModulePermission.of(lifecycle, "vanillatweaks.workstationhighlights.findworkstation"))
                        .handler(commandContext -> {
                            manager.taskRecipe().begin(commandContext).synchronous(context -> {
                                Player player = PlayerCommandDispatcher.from(context);
                                Villager villager = VTUtils.getSingleNearbyEntityOfType(Villager.class, player.getLocation(), 3, 3, 3);
                                if (villager == null) {
                                    context.getSender().sendMessage(mapped("modules.workstation-highlights.no-villager-nearby", RED));
                                    return;
                                }
                                Location work = villager.getMemory(MemoryKey.JOB_SITE);
                                if (work == null || work.getWorld() == null) {
                                    context.getSender().sendMessage(mapped("modules.workstation-highlights.none-found", YELLOW));
                                    return;
                                }
                                villager.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0));
                                work.getWorld().spawn(work.add(0.5, 1, 0.5), AreaEffectCloud.class, (cloud) -> {
                                    cloud.setParticle(Particle.HEART);
                                    cloud.setReapplicationDelay(10);
                                    cloud.setRadius(0.5f);
                                    cloud.setRadiusPerTick(0f);
                                    cloud.setRadiusOnUse(0f);
                                    cloud.setDuration(200);
                                });
                                context.getSender().sendMessage(mappedBuilder("modules.workstation-highlights.located-at", YELLOW)
                                        .arg("x", text(work.getBlockX(), WHITE))
                                        .arg("y", text(work.getBlockY(), WHITE))
                                        .arg("z", text(work.getBlockZ(), WHITE))
                                );
                            }).execute();
                        })
        );
    }
}
