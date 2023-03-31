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
package me.machinemaker.vanillatweaks.modules.teleportation.tpa;

import com.google.inject.Inject;
import io.papermc.lib.PaperLib;
import java.util.Optional;
import java.util.UUID;
import me.machinemaker.vanillatweaks.modules.teleportation.back.Back;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

record Request(UUID from, UUID to, long cancelAfter) {

    @Inject
    private static BukkitAudiences audiences;

    boolean complete() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be accessed from main thread");
        }
        final @Nullable Player from = Bukkit.getPlayer(this.from);
        final @Nullable Player to = Bukkit.getPlayer(this.to);
        if (from == null || to == null) {
            if (from == null && to != null) {
                audiences.player(to).sendMessage(translatable("modules.tpa.teleport.fail.sender-offline", RED));
            } else if (from != null) {
                audiences.player(from).sendMessage(translatable("modules.tpa.teleport.fail.target-offline", RED));
            }
            return false;
        }
        audiences.player(from).sendMessage(translatable("modules.tpa.teleport.success.sender", GOLD, text(to.getName(), YELLOW)));
        audiences.player(to).sendMessage(translatable("modules.tpa.teleport.success.target", GOLD, text(from.getName(), YELLOW)));
        Back.setBackLocation(from, from.getLocation());
        if (to.getLocation().getChunk().isLoaded()) {
            from.teleport(to);
        } else {
            PaperLib.teleportAsync(from, to.getLocation());
        }
        return true;
    }

    Optional<Player> playerFrom() {
        return Optional.ofNullable(Bukkit.getPlayer(this.from));
    }

    Optional<Player> playerTo() {
        return Optional.ofNullable(Bukkit.getPlayer(this.to));
    }
}
