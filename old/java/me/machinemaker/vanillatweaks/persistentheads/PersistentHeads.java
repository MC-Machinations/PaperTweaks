/*
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
package me.machinemaker.vanillatweaks.modules.other.persistentheads;

import io.papermc.lib.PaperLib;
import io.papermc.lib.features.blockstatesnapshot.BlockStateSnapshotResult;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.pdc.PDCKey;
import me.machinemaker.vanillatweaks.pdc.PaperDataTypes;
import me.machinemaker.vanillatweaks.utils.Keys;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

class BlockListener implements ModuleListener {

    private static final PDCKey<Component> HEAD_NAME = new PDCKey<>(Keys.key("head_name"), PaperDataTypes.COMPONENT);
    private static final PDCKey<List<Component>> HEAD_LORE = new PDCKey<>(Keys.key("head_lore"), PaperDataTypes.COMPONENT_LIST);

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        final ItemStack headItem = event.getItemInHand();
        if (headItem.getType() != Material.PLAYER_HEAD) return;
        ItemMeta meta = headItem.getItemMeta();
        if (meta == null) return;
        Component name = meta.displayName();
        List<Component> lore = meta.lore();
        Block block = event.getBlockPlaced();
        // NOTE: Not using snapshots is broken: https://github.com/PaperMC/Paper/issues/3913
        BlockStateSnapshotResult blockStateSnapshotResult = PaperLib.getBlockState(block, true);
        TileState skullState = (TileState) blockStateSnapshotResult.getState();
        if (name != null) {
            HEAD_NAME.setFrom(skullState, name);
        }
        if (lore != null) {
            HEAD_LORE.setFrom(skullState, lore);
        }
        if (blockStateSnapshotResult.isSnapshot()) skullState.update();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDropItemEvent(BlockDropItemEvent event) {
        @NotNull BlockState blockState = event.getBlockState();
        Material blockType = blockState.getType();
        if (blockType != Material.PLAYER_HEAD && blockType != Material.PLAYER_WALL_HEAD) return;
        TileState skullState = (TileState) blockState;
        @Nullable Component name = HEAD_NAME.getFrom(skullState);
        @Nullable List<Component> lore = HEAD_LORE.getFrom(skullState);
        for (Item item : event.getItems()) { // Ideally should only be one...
            @NotNull ItemStack itemstack = item.getItemStack();
            if (itemstack.getType() == Material.PLAYER_HEAD) {
                @Nullable ItemMeta meta = itemstack.getItemMeta();
                if (meta == null) continue; // This shouldn't happen
                meta.displayName(name);
                meta.lore(lore);
                itemstack.setItemMeta(meta);
            }
        }

    }

    /**
     * Prevents player from removing player-head NBT by water logging them
     */
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        handleEvent(event::getBlock, event, false);
    }

    /**
     * Prevents player from removing player-head NBT using running water
     */
    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        handleEvent(event::getToBlock, event, true);
    }

    private void handleEvent(Supplier<Block> blockSupplier, Cancellable event, boolean cancelEvent) {
        Block block = blockSupplier.get();
        @NotNull BlockState blockState = block.getState();
        if (blockState.getType() != Material.PLAYER_HEAD && blockState.getType() != Material.PLAYER_WALL_HEAD) return;
        Skull skullState = (Skull) blockState;
        @Nullable Component name = HEAD_NAME.getFrom(skullState);
        @Nullable List<Component> lore = HEAD_LORE.getFrom(skullState);
        if (name == null) return;
        @NotNull Optional<ItemStack> skullStack = block.getDrops().stream().filter(is -> is.getType() == Material.PLAYER_HEAD).findAny();
        if (skullStack.isPresent()) {
            if (updateDrop(block, name, lore, skullStack.get())) return; // This shouldn't happen
            if (cancelEvent) event.setCancelled(true);
        }

        BlockState blockState1 = block.getWorld().getBlockAt(block.getLocation()).getState();
        blockState1.update(true, true);
    }

    private boolean updateDrop(Block block, @Nullable Component name, @Nullable List<Component> lore, @NotNull ItemStack itemstack) {
        @Nullable ItemMeta meta = itemstack.getItemMeta();
        if (meta == null) return true;
        meta.displayName(name);
        meta.lore(lore);
        itemstack.setItemMeta(meta);

        block.getWorld().dropItemNaturally(block.getLocation(), itemstack);
        block.getDrops().clear();
        block.setType(Material.AIR);
        return false;
    }

}
