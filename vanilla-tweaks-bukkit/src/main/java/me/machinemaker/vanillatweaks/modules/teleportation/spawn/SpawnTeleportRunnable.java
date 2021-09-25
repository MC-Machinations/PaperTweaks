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
package me.machinemaker.vanillatweaks.modules.teleportation.spawn;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.cooldown.CommandCooldownManager;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.modules.teleportation.back.Back;
import me.machinemaker.vanillatweaks.utils.runnables.TeleportRunnable;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class SpawnTeleportRunnable extends TeleportRunnable {

    @Inject
    private static CommandCooldownManager<CommandDispatcher, UUID> cooldownManager;

    private final Audience audience;
    private final Consumer<Player> callback;

    public SpawnTeleportRunnable(Player player, Audience audience, Location teleportLoc, long tickDelay, Consumer<Player> callback) {
        super(player, teleportLoc, tickDelay);
        this.audience = audience;
        this.callback = callback;
    }

    @Override
    public void onTeleport() {
        Back.setBackLocation(this.player, this.player.getLocation()); // Set back location
    }

    @Override
    public void onMove() {
        audience.sendMessage(translatable("modules.spawn.teleporting.moved", RED));
        cooldownManager.invalidate(player.getUniqueId(), Commands.SPAWN_CMD_COOLDOWN_KEY);
    }

    @Override
    public void onEnd() {
        this.callback.accept(this.player);
    }
}
