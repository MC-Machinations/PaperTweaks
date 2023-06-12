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
package me.machinemaker.papertweaks.modules.items.wrenches;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import java.util.List;
import me.machinemaker.papertweaks.integrations.Interactions;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.tags.MaterialTag;
import me.machinemaker.papertweaks.tags.Tags;
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

class WrenchListener implements ModuleListener {

    private static final List<BlockFace> FACES = List.of(BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN);

    private final Config config;

    @Inject
    WrenchListener(final Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.useItemInHand() != Event.Result.DENY
            && event.getHand() == EquipmentSlot.HAND
            && event.getAction() == Action.RIGHT_CLICK_BLOCK
            && event.getClickedBlock() != null
            && event.getItem() != null
            && event.getItem().equals(Lifecycle.WRENCH)
        ) {
            final Block block = event.getClickedBlock();
            final BlockData blockData = block.getBlockData();
            if (blockData instanceof final Directional directional
                && (this.isValid(block, event.getPlayer(), this.config.redstoneWrench, Tags.REDSTONE_COMPONENTS, "vanillatweaks.wrench.redstone")
                || this.isValid(block, event.getPlayer(), this.config.terracottaWrench, Tags.GLAZED_TERRACOTTA, "vanillatweaks.wrench.terracotta"))) {
                if (!(directional instanceof final Piston piston && piston.isExtended()) && Interactions.isAllowedInteraction(event.getPlayer(), event.getClickedBlock())) { // Don't allow rotating extended pistons
                    final List<BlockFace> applicableFaces = Lists.newArrayList();
                    for (final BlockFace face : FACES) {
                        if (directional.getFaces().contains(face)) {
                            applicableFaces.add(face);
                        }
                    }
                    final int facing = applicableFaces.indexOf(directional.getFacing());
                    final BlockFace nextFace = applicableFaces.get(event.getPlayer().isSneaking() ? (applicableFaces.size() + (facing - 1)) % applicableFaces.size() : (facing + 1) % applicableFaces.size());
                    event.setUseInteractedBlock(Event.Result.DENY);
                    directional.setFacing(nextFace);
                    block.setBlockData(directional);
                }
            }
        }

    }

    private boolean isValid(final Block block, final Player player, final boolean config, final MaterialTag tag, final String permission) {
        return config && tag.isTagged(block.getType()) && player.hasPermission(permission);
    }
}
