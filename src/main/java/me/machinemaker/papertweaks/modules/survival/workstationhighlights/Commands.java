/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.workstationhighlights;

import cloud.commandframework.Command;
import com.google.inject.Inject;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.utils.Entities;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Villager;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;

@ModuleCommand.Info(value = "find-workstation", aliases = {"fworkstation", "fwork", "findwork"}, descriptionKey = "modules.workstation-highlights.commands.root", miniMessage = true, infoOnRoot = false)
class Commands extends ModuleCommand {

    private final MessageService messageService;

    @Inject
    Commands(final MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        this.register(
            builder
                .permission(this.modulePermission("vanillatweaks.workstationhighlights.findworkstation"))
                .handler(this.sync((context, player) -> {
                    final @Nullable Villager villager = Entities.getSingleNearbyEntityOfType(Villager.class, player.getLocation(), 3, 3, 3);
                    if (villager == null) {
                        this.messageService.noVillagerNearby(context.getSender());
                        return;
                    }
                    final @Nullable Location work = villager.getMemory(MemoryKey.JOB_SITE);
                    if (work == null || work.getWorld() == null) {
                        this.messageService.noWorkstationFound(context.getSender());
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
                    this.messageService.workstationLocatedAt(context.getSender(), work.getBlockX(), work.getBlockY(), work.getBlockZ());
                }))
        );
    }
}
