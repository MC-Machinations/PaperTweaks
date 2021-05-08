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
package me.machinemaker.vanillatweaks.modules.survival.customnetherportals;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Orientable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.function.ToIntFunction;

class PortalShapeFinder {

    @Inject private static Config config;
    @Inject private static JavaPlugin plugin;

    private final Set<Block> portalInterior;
    private final Set<Long> checkedLocations;
    private final IgniteListener.Axis axis;

    PortalShapeFinder(Block first, IgniteListener.Axis axis) {
        this.portalInterior = Sets.newConcurrentHashSet(Set.of(first));
        this.checkedLocations = Sets.newConcurrentHashSet();
        this.axis = axis;
    }

    boolean start() {
        boolean build = true;

        while (portalInterior.size() <= config.maxPortalHeight * config.maxPortalWidth && this.checkedLocations.size() < this.portalInterior.size()) {
            var interiorIter = this.portalInterior.iterator();

            finished: {
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

            int maxY = Collections.max(this.portalInterior, Comparator.comparingInt(Block::getY)).getY();
            int minY = Collections.min(this.portalInterior, Comparator.comparingInt(Block::getY)).getY();

            if (maxY - minY > config.maxPortalHeight) {
                return false;
            }

            ToIntFunction<Block> flatFunction = this.axis == IgniteListener.Axis.X ? Block::getX : Block::getZ;

            Block maxFlatBlock = Collections.max(this.portalInterior, Comparator.comparingInt(flatFunction));
            Block minFlatBlock = Collections.max(this.portalInterior, Comparator.comparingInt(flatFunction));

            if (flatFunction.applyAsInt(maxFlatBlock) - flatFunction.applyAsInt(minFlatBlock) > config.maxPortalWidth) {
                return false;
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                this.portalInterior.forEach(block -> {
                    block.setType(Material.NETHER_PORTAL);
                    this.axis.setOrientation(block);
                });
            }, 1L);
            return true;
        }
        return false;
    }

    boolean checkSurrounding(Block source) {
        return checkValidPortalInterior(source, BlockFace.UP)
                && checkValidPortalInterior(source, BlockFace.DOWN)
                && checkValidPortalInterior(source, this.axis.left)
                && checkValidPortalInterior(source, this.axis.right);
    }

    boolean checkValidPortalInterior(Block source, BlockFace face) {
        Block toCheck = source.getRelative(face);
        if (isReplaceable(toCheck)) {
            this.portalInterior.add(toCheck);
            return true;
        }
        return IgniteListener.isPortalFrame(toCheck);
    }

    static boolean isReplaceable(Block block) {
        Material type = block.getType();
        return type == Material.AIR || type == Material.CAVE_AIR || type == Material.VOID_AIR || type == Material.FIRE;
    }

    static long toLong(Block block) {
        return toLong(block.getLocation());
    }

    static long toLong(Location location) {
        return (((long) location.getBlockX() & 67108863L) << 38) | (((long) location.getBlockY() & 4095L)) | (((long) location.getZ() & 67108863L) << 12);
    }
}
