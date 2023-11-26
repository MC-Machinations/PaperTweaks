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
package me.machinemaker.papertweaks.integrations.griefprevention;

import com.google.common.base.Suppliers;
import java.util.Objects;
import java.util.function.Supplier;
import me.machinemaker.papertweaks.integrations.Interactions;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GPInteractionHandler implements Interactions.Handler {

    private static final Supplier<GriefPrevention> PLUGIN = Suppliers.memoize(() -> GriefPrevention.instance);
    private static final Supplier<DataStore> DATA_STORE = Suppliers.memoize(() -> PLUGIN.get().dataStore);

    @SuppressWarnings("deprecation") // GP api uses ChatColor
    @Override
    public boolean checkBlock(final Player player, final Block clickedBlock) {
        final PlayerData playerData = DATA_STORE.get().getPlayerData(player.getUniqueId());
        final Claim claim = DATA_STORE.get().getClaimAt(clickedBlock.getLocation(), false, playerData.lastClaim);
        if (claim != null) {
            playerData.lastClaim = claim;
            final String noAccessReason = claim.allowAccess(player);
            if (noAccessReason != null) {
                GriefPrevention.sendMessage(player, /*TextMode.Err*/ ChatColor.RED, noAccessReason);
                return false;
            }
        }
        return true;
    }
}
