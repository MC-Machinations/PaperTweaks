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
package me.machinemaker.papertweaks.modules.survival.fastleafdecay;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import me.machinemaker.papertweaks.modules.ModuleListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.plugin.java.JavaPlugin;

class LeafListener implements ModuleListener {

    private static final List<BlockFace> FACES = Lists.newArrayList(Arrays.stream(BlockFace.values()).filter(BlockFace::isCartesian).toList()); // mutable list due to Collections#shuffle
    private static final Set<Block> SCHEDULED = Sets.newHashSet();
    private final JavaPlugin plugin;

    @Inject
    LeafListener(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Material type = event.getBlock().getType();
        if (Tag.LOGS.isTagged(type) || Tag.LEAVES.isTagged(type)) {
            this.doDecay(event.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeavesDecay(final LeavesDecayEvent event) {
        this.doDecay(event.getBlock());
    }

    private void doDecay(final Block block) {
        Collections.shuffle(FACES);

        for (final BlockFace face : FACES) {
            final Block b = block.getRelative(face);
            if (SCHEDULED.contains(b)) continue;
            if (!(b.getBlockData() instanceof final Leaves leaves) || leaves.isPersistent() || leaves.getDistance() < 7) {
                continue; // https://github.com/MC-Machinations/VanillaTweaks/issues/54, datapacks modify the #minecraft:leaves block tag
            }
            SCHEDULED.add(b);
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                final LeavesDecayEvent decayEvent = new LeavesDecayEvent(b);
                Bukkit.getPluginManager().callEvent(decayEvent);
                if (decayEvent.isCancelled()) return;
                b.breakNaturally();
                SCHEDULED.remove(b);
            }, ThreadLocalRandom.current().nextLong(2, 9));

        }
    }
}
