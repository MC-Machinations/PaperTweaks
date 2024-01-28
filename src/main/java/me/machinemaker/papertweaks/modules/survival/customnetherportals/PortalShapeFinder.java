/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.customnetherportals;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.function.ToIntFunction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;

class PortalShapeFinder {

    @Inject private static Config config;
    @Inject private static JavaPlugin plugin;

    private final Set<Block> portalInterior;
    private final Set<Long> checkedLocations;
    private final IgniteListener.Axis axis;

    PortalShapeFinder(final Block first, final IgniteListener.Axis axis) {
        this.portalInterior = Sets.newConcurrentHashSet(Set.of(first));
        this.checkedLocations = Sets.newConcurrentHashSet();
        this.axis = axis;
    }

    static boolean isReplaceable(final Block block) {
        final Material type = block.getType();
        return type == Material.AIR || type == Material.CAVE_AIR || type == Material.VOID_AIR || type == Material.FIRE;
    }

    static long toLong(final Block block) {
        return toLong(block.getLocation());
    }

    static long toLong(final Location location) {
        return (((long) location.getBlockX() & 67108863L) << 38) | (((long) location.getBlockY() & 4095L)) | (((long) location.getZ() & 67108863L) << 12);
    }

    boolean start() {
        boolean build = true;

        while (this.portalInterior.size() <= config.maxPortalHeight * config.maxPortalWidth && this.checkedLocations.size() < this.portalInterior.size()) {
            final Iterator<Block> interiorIter = this.portalInterior.iterator();

            finished:
            {
                Block currentBlock;
                do {
                    do {
                        if (!interiorIter.hasNext()) {
                            break finished;
                        }

                        currentBlock = interiorIter.next();
                    } while (this.checkedLocations.contains(toLong(currentBlock)));

                    this.checkedLocations.add(toLong(currentBlock));
                } while (this.checkSurrounding(currentBlock));

                build = false;
            }

            if (!build) {
                break;
            }
        }

        if (build && this.portalInterior.size() >= config.minPortalSize) {

            final int maxY = Collections.max(this.portalInterior, Comparator.comparingInt(Block::getY)).getY();
            final int minY = Collections.min(this.portalInterior, Comparator.comparingInt(Block::getY)).getY();

            if (maxY - minY > config.maxPortalHeight) {
                return false;
            }

            final ToIntFunction<Block> flatFunction = this.axis == IgniteListener.Axis.X ? Block::getX : Block::getZ;

            final Block maxFlatBlock = Collections.max(this.portalInterior, Comparator.comparingInt(flatFunction));
            final Block minFlatBlock = Collections.max(this.portalInterior, Comparator.comparingInt(flatFunction));

            if (flatFunction.applyAsInt(maxFlatBlock) - flatFunction.applyAsInt(minFlatBlock) > config.maxPortalWidth) {
                return false;
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> this.portalInterior.forEach(block -> {
                block.setType(Material.NETHER_PORTAL);
                this.axis.setOrientation(block);
            }), 1L);
            return true;
        }
        return false;
    }

    boolean checkSurrounding(final Block source) {
        return this.checkValidPortalInterior(source, BlockFace.UP)
            && this.checkValidPortalInterior(source, BlockFace.DOWN)
            && this.checkValidPortalInterior(source, this.axis.left)
            && this.checkValidPortalInterior(source, this.axis.right);
    }

    boolean checkValidPortalInterior(final Block source, final BlockFace face) {
        final Block toCheck = source.getRelative(face);
        if (isReplaceable(toCheck)) {
            this.portalInterior.add(toCheck);
            return true;
        }
        return IgniteListener.isPortalFrame(toCheck);
    }
}
