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
package me.machinemaker.vanillatweaks.integrations.worldguard;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.machinemaker.vanillatweaks.integrations.Interactions;
import org.bukkit.Effect;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;

public class WGInteractionHandler implements Interactions.Handler {

    private static final WorldGuardPlugin WORLD_GUARD_PLUGIN = WorldGuardPlugin.inst();
    private static final WorldGuard WORLD_GUARD = WorldGuard.getInstance();

    @Override
    public boolean test(PlayerInteractEvent event) {
        LocalPlayer localPlayer = WORLD_GUARD_PLUGIN.wrapPlayer(event.getPlayer());
        if (WORLD_GUARD.getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            return true;
        }
        RegionQuery query = WORLD_GUARD.getPlatform().getRegionContainer().createQuery();
        boolean result = query.testBuild(localPlayer.getLocation(), localPlayer);
        if (!result) {
            localPlayer.printRaw(query.queryValue(localPlayer.getLocation(), localPlayer, Flags.DENY_MESSAGE).replace("%what%", "use that"));
            if (WORLD_GUARD_PLUGIN.getConfigManager().particleEffects && event.getClickedBlock() != null) {
                event.getPlayer().playEffect(event.getClickedBlock().getLocation().add(0, 1, 0), Effect.SMOKE, BlockFace.UP);
            }
            return false;
        }
        return true;
    }
}
