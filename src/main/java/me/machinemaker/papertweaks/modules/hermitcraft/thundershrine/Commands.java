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
package me.machinemaker.papertweaks.modules.hermitcraft.thundershrine;

import cloud.commandframework.Command;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.utils.Entities;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

@ModuleCommand.Info(value = "thundershrine", aliases = {"tshrine"}, i18n = "thunder-shrine", perm = "thundershrine")
class Commands extends ConfiguredModuleCommand {

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        this.manager.command(this.literal(builder, "create")
            .handler(this.sync((context, player) -> {
                final @Nullable ArmorStand stand = Entities.getSingleNearbyEntityOfType(ArmorStand.class, player.getLocation(), 3, 3, 3);
                if (stand == null) {
                    context.getSender().sendMessage(translatable("modules.thunder-shrine.commands.create.fail.no-stands", RED));
                } else {
                    stand.getWorld().spawnParticle(Particle.TOTEM, stand.getLocation(), 100, 0, 0, 0, 0.5);
                    stand.getWorld().playSound(stand.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.MASTER, 1.0f, 0.75f);
                    stand.getWorld().spawn(stand.getLocation(), AreaEffectCloud.class, cloud -> {
                        cloud.setDuration(Integer.MAX_VALUE);
                        cloud.setRadius(0.01f);
                        cloud.setParticle(Particle.SUSPENDED);
                        cloud.setWaitTime(0);
                        ThunderShrine.SHRINE.setTo(cloud, player.getUniqueId());
                    });
                    stand.remove();
                    context.getSender().sendMessage(translatable("modules.thunder-shrine.commands.create.success", YELLOW));
                }
            }))
        ).command(this.literal(builder, "remove")
            .handler(this.sync((context, player) -> {
                final @Nullable AreaEffectCloud cloud = Entities.getSingleNearbyEntityOfType(AreaEffectCloud.class, player.getLocation(), 3, 3, 3, c -> player.getUniqueId().equals(ThunderShrine.SHRINE.getFrom(c)));
                if (cloud == null) {
                    context.getSender().sendMessage(translatable("modules.thunder-shrine.commands.remove.fail.no-stands", RED));
                } else {
                    cloud.remove();
                    context.getSender().sendMessage(translatable("modules.thunder-shrine.commands.remove.success", YELLOW));
                }
            }))
        );

    }
}
