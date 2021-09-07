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
package me.machinemaker.vanillatweaks.modules.other.persistentheads;

import com.google.inject.Inject;
import io.papermc.lib.PaperLib;
import io.papermc.lib.features.blockstatesnapshot.BlockStateSnapshotResult;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.pdc.PDCKey;
import me.machinemaker.vanillatweaks.pdc.PaperDataTypes;
import me.machinemaker.vanillatweaks.utils.Keys;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class BlockListener implements ModuleListener {

    private static final PDCKey<Component> HEAD_NAME = new PDCKey<>(Keys.key("head_name"), PaperDataTypes.COMPONENT);
    private static final PDCKey<List<Component>> HEAD_LORE = new PDCKey<>(Keys.key("head_lore"), PaperDataTypes.COMPONENT_LIST);

    private final Plugin plugin;

    @Inject
    BlockListener(Plugin plugin) {
        this.plugin = plugin;
    }

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
            HEAD_NAME.setTo(skullState, name);
        }
        if (lore != null) {
            HEAD_LORE.setTo(skullState, lore);
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
                itemstack.editMeta(meta -> {
                    meta.displayName(name);
                    meta.lore(lore);
                });
            }
        }
    }

    /**
     * Prevents player from removing player-head NBT by water logging them
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        handleBlock(event.getBlock(), event, false);
    }

    /**
     * Prevents player from removing player-head NBT using running water
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLiquidFlow(BlockFromToEvent event) {
        handleBlock(event.getToBlock(), event, true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplosion(BlockExplodeEvent event) {
        handleExplosionEvent(event.blockList(), event.getYield());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplosion(EntityExplodeEvent event) {
        handleExplosionEvent(event.blockList(), event.getYield());
    }

    private void handleExplosionEvent(@NotNull final List<Block> blocksExploded, final float explosionYield) {
        final Random random = ThreadLocalRandom.current();
        var iter = blocksExploded.iterator();
        while (iter.hasNext()) {
            Block block = iter.next();
            if (block.getState() instanceof Skull && random.nextFloat() <= explosionYield) {
                handleBlock(block, null, false);
                iter.remove();
            }
        }
    }

    @Contract("_, null, true -> fail")
    private void handleBlock(Block block, Cancellable event, boolean shouldCancelEvent) {
        if (block.getState() instanceof Skull skull) {
            final Optional<ItemStack> skullStack = block.getDrops().stream().filter(is -> is.getType() == Material.PLAYER_HEAD).findAny();
            skullStack.ifPresent(stack -> {
                boolean edited = stack.editMeta(meta -> {
                    meta.displayName(HEAD_NAME.getFrom(skull));
                    meta.lore(HEAD_LORE.getFrom(skull));
                });
                if (!edited) return;

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> block.getWorld().dropItemNaturally(block.getLocation(), stack), 1L);
                block.setType(Material.AIR);
                if (shouldCancelEvent) event.setCancelled(true);
                block.getWorld().getBlockAt(block.getLocation()).getState().update(true, true);
            });
        }
    }
}
