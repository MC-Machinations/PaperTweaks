/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2026 Machine_Maker
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
package me.machinemaker.papertweaks.modules.other.chunkloader;

import com.google.inject.Inject;
import me.machinemaker.papertweaks.modules.ModuleListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class ChunkLoaderListener implements ModuleListener {

    private final ChunkLoaderManager manager;

    @Inject
    private ChunkLoaderListener(ChunkLoaderManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        assert event.getClickedBlock() != null;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() != Material.LODESTONE) {
            return;
        }

        assert event.getItem() != null;
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.NETHER_STAR) {
            return;
        }

        if (clickedBlock.getChunk().isForceLoaded()) {
            event.getPlayer().sendActionBar(Component.text("This chunk is already loaded!", NamedTextColor.YELLOW));
            return;
        }

        this.manager.addChunkLoader(clickedBlock.getLocation());
        item.setAmount(item.getAmount() - 1);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.LODESTONE) {
            return;
        }

        if (this.manager.isChunkLoader(block.getLocation())) {
            this.manager.removeChunkLoader(block.getLocation());
        }
    }
}
