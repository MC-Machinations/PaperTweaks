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
package me.machinemaker.vanillatweaks.modules.teleportation.back;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.cooldown.CommandCooldownManager;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.utils.runnables.TeleportRunnable;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class BackTeleportRunnable extends TeleportRunnable {

    @Inject private static CommandCooldownManager<CommandDispatcher, UUID> cooldownManager;
    @Inject private static Plugin plugin;
    private static final Map<UUID, BukkitTask> AWAITING_TELEPORT = Maps.newHashMap();

    private final Audience audience;

    protected BackTeleportRunnable(@NotNull Player player, @NotNull Location teleportLoc, long tickDelay, Audience audience) {
        super(player, teleportLoc, tickDelay);
        this.audience = audience;
    }

    public void start() {
        AWAITING_TELEPORT.put(this.player.getUniqueId(), this.runTaskTimer(plugin, 1L, 1L));
    }

    @Override
    public void onTeleport() {
        Back.setBackLocation(this.player, this.player.getLocation());
    }

    @Override
    public void onMove() {
        this.audience.sendMessage(translatable("modules.back.commands.root.moved", RED));
        cooldownManager.invalidate(this.player.getUniqueId(), Commands.BACK_COMMAND_COOLDOWN_KEY);
        super.onMove();
    }

    @Override
    public void onEnd() {
        AWAITING_TELEPORT.remove(this.player.getUniqueId());
    }
}
