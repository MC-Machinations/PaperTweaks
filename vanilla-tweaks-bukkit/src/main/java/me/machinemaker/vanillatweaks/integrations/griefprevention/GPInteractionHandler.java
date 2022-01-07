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
package me.machinemaker.vanillatweaks.integrations.griefprevention;

import me.machinemaker.vanillatweaks.integrations.Interactions;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GPInteractionHandler implements Interactions.Handler {

    private static final GriefPrevention PLUGIN = Objects.requireNonNull(GriefPrevention.instance, "Could not find the instance of the GriefPrevention plugin");
    private static final DataStore DATA_STORE = Objects.requireNonNull(PLUGIN.dataStore, "Could not find the GriefPrevention dataStore");

    @Override
    public boolean checkBlock(@NotNull Player player, @NotNull Block clickedBlock) {
        final PlayerData playerData = DATA_STORE.getPlayerData(player.getUniqueId());
        final Claim claim = DATA_STORE.getClaimAt(clickedBlock.getLocation(), false, playerData.lastClaim);
        if (claim != null) {
            playerData.lastClaim = claim;
            String noAccessReason = claim.allowAccess(player);
            if (noAccessReason != null) {
                GriefPrevention.sendMessage(player, /*TextMode.Err*/ ChatColor.RED, noAccessReason);
                return false;
            }
        }
        return true;
    }
}
