/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2020-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules.other.persistentheads;

import com.google.inject.Inject;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.pdc.PDCKey;
import me.machinemaker.papertweaks.pdc.PaperDataTypes;
import me.machinemaker.papertweaks.utils.Keys;
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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

import static java.util.Objects.requireNonNull;

class BlockListener implements ModuleListener {

    private static final PDCKey<Component> HEAD_NAME = new PDCKey<>(Keys.legacyKey("head_name"), PaperDataTypes.COMPONENT);
    private static final PDCKey<List<Component>> HEAD_LORE = new PDCKey<>(Keys.legacyKey("head_lore"), PaperDataTypes.COMPONENT_LIST);

    private final Plugin plugin;

    @Inject
    BlockListener(final Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlaceEvent(final BlockPlaceEvent event) {
        final ItemStack headItem = event.getItemInHand();
        if (headItem.getType() != Material.PLAYER_HEAD) return;
        final ItemMeta meta = headItem.getItemMeta();
        if (meta == null) return;
        final @Nullable Component name = meta.displayName();
        final @Nullable List<Component> lore = meta.lore();
        final Block block = event.getBlockPlaced();
        final TileState skullState = (TileState) block.getState(true);
        if (name != null) {
            HEAD_NAME.setTo(skullState, name);
        }
        if (lore != null) {
            HEAD_LORE.setTo(skullState, lore);
        }
        if (skullState.isSnapshot()) skullState.update();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDropItemEvent(final BlockDropItemEvent event) {
        final BlockState blockState = event.getBlockState();
        final Material blockType = blockState.getType();
        if (blockType != Material.PLAYER_HEAD && blockType != Material.PLAYER_WALL_HEAD) return;
        final TileState skullState = (TileState) blockState;
        final @Nullable Component name = HEAD_NAME.getFrom(skullState);
        final @Nullable List<Component> lore = HEAD_LORE.getFrom(skullState);
        for (final Item item : event.getItems()) { // Ideally should only be one...
            final ItemStack itemstack = item.getItemStack();
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
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        this.handleBlock(event.getBlock(), event, false);
    }

    /**
     * Prevents player from removing player-head NBT using running water
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLiquidFlow(final BlockFromToEvent event) {
        this.handleBlock(event.getToBlock(), event, true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplosion(final BlockExplodeEvent event) {
        this.handleExplosionEvent(event.blockList(), event.getYield());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplosion(final EntityExplodeEvent event) {
        this.handleExplosionEvent(event.blockList(), event.getYield());
    }

    private void handleExplosionEvent(final List<Block> blocksExploded, final float explosionYield) {
        final Random random = ThreadLocalRandom.current();
        final Iterator<Block> iter = blocksExploded.iterator();
        while (iter.hasNext()) {
            final Block block = iter.next();
            if (block.getState() instanceof Skull && random.nextFloat() <= explosionYield) {
                this.handleBlock(block, null, false);
                iter.remove();
            }
        }
    }

    @Contract("_, null, true -> fail")
    private void handleBlock(final Block block, final @Nullable Cancellable event, final boolean shouldCancelEvent) {
        if (block.getState() instanceof final Skull skull) {
            final Optional<ItemStack> skullStack = block.getDrops().stream().filter(is -> is.getType() == Material.PLAYER_HEAD).findAny();
            skullStack.ifPresent(stack -> {
                final boolean edited = stack.editMeta(meta -> {
                    meta.displayName(HEAD_NAME.getFrom(skull));
                    meta.lore(HEAD_LORE.getFrom(skull));
                });
                if (!edited) return;

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> block.getWorld().dropItemNaturally(block.getLocation(), stack), 1L);
                block.setType(Material.AIR);
                if (shouldCancelEvent) requireNonNull(event, "can't cancel a null event").setCancelled(true);
                block.getWorld().getBlockAt(block.getLocation()).getState().update(true, true);
            });
        }
    }
}
