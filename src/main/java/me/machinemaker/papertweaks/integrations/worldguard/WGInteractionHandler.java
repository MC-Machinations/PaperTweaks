/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.integrations.worldguard;

import com.google.common.base.Suppliers;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.function.Supplier;
import me.machinemaker.papertweaks.integrations.Interactions;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class WGInteractionHandler implements Interactions.Handler {

    private static final Supplier<WorldGuardPlugin> WORLD_GUARD_PLUGIN = Suppliers.memoize(WorldGuardPlugin::inst);
    private static final Supplier<WorldGuard> WORLD_GUARD = Suppliers.memoize(WorldGuard::getInstance);

    @Override
    @SuppressWarnings("deprecation")
    public boolean checkBlock(final Player player, final Block clickedBlock) {
        final LocalPlayer localPlayer = WORLD_GUARD_PLUGIN.get().wrapPlayer(player);
        if (WORLD_GUARD.get().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            return true;
        }
        final RegionQuery query = WORLD_GUARD.get().getPlatform().getRegionContainer().createQuery();
        final boolean result = query.testBuild(localPlayer.getLocation(), localPlayer);
        if (!result) {
            localPlayer.printRaw(query.queryValue(localPlayer.getLocation(), localPlayer, Flags.DENY_MESSAGE).replace("%what%", "use that"));
            if (WORLD_GUARD_PLUGIN.get().getConfigManager().particleEffects) {
                player.playEffect(clickedBlock.getLocation().add(0, 1, 0), Effect.SMOKE, BlockFace.UP);
            }
            return false;
        }
        return true;
    }
}
