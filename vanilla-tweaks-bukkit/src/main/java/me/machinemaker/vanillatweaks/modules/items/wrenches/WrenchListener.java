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
package me.machinemaker.vanillatweaks.modules.items.wrenches;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.integrations.Interactions;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.tags.Tags;
import me.machinemaker.vanillatweaks.tags.types.MaterialTag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Piston;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

class WrenchListener implements ModuleListener {

    private static final List<BlockFace> FACES = List.of(BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN);

    private final Config config;

    @Inject
    WrenchListener(Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useItemInHand() != Event.Result.DENY
                && event.getHand() == EquipmentSlot.HAND
                && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.getClickedBlock() != null
                && event.getItem() != null
                && event.getItem().equals(Lifecycle.WRENCH)
        ) {
            Block block = event.getClickedBlock();
            BlockData blockData = block.getBlockData();
            if (blockData instanceof Directional directional
                    && (isValid(block, event.getPlayer(), config.redstoneWrench, Tags.REDSTONE_COMPONENTS, "vanillatweaks.wrench.redstone")
                    || isValid(block, event.getPlayer(), config.terracottaWrench, Tags.GLAZED_TERRACOTTA, "vanillatweaks.wrench.terracotta"))) {
                if (!(directional instanceof Piston piston && piston.isExtended()) && Interactions.isAllowedInteraction(event.getPlayer(), event.getClickedBlock())) { // Don't allow rotating extended pistons
                    List<BlockFace> applicableFaces = Lists.newArrayList();
                    for (BlockFace face : FACES) {
                        if (directional.getFaces().contains(face)) {
                            applicableFaces.add(face);
                        }
                    }
                    int facing = applicableFaces.indexOf(directional.getFacing());
                    BlockFace nextFace = applicableFaces.get(event.getPlayer().isSneaking() ? (applicableFaces.size() + (facing - 1)) % applicableFaces.size() : (facing + 1) % applicableFaces.size());
                    event.setUseInteractedBlock(Event.Result.DENY);
                    directional.setFacing(nextFace);
                    block.setBlockData(directional);
                }
            }
        }

    }

    private boolean isValid(Block block, Player player, boolean config, MaterialTag tag, String permission) {
        return config && tag.isTagged(block.getType()) && player.hasPermission(permission);
    }
}
