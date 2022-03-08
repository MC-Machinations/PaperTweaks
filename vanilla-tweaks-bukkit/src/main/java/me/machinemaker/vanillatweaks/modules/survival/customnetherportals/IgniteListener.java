/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

class IgniteListener implements ModuleListener {

    @Inject
    private static Config config;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Block block = event.getBlock();
        World world = block.getWorld();
        if (isInValidDimension(world) && isPortalFrame(block.getRelative(BlockFace.DOWN))) {
            Axis portalAxis = findPortalAxis(world, block);
            if (portalAxis == null) return;

            event.setCancelled(new PortalShapeFinder(block, portalAxis).start());
        }
    }

    Axis findPortalAxis(World world, Block source) {
        for (Axis axis : Axis.values()) {
            if (axis.rayTraceBlock(world, source.getLocation())) {
                return axis;
            }
        }
        return null;
    }

    static boolean isPortalFrameResult(@Nullable RayTraceResult result) {
        if (result == null) return false;
        return isPortalFrame(result.getHitBlock());
    }

    static boolean isPortalFrame(@Nullable Block block) {
        if (block == null) return false;
        return config.portalFrameMaterials.contains(block.getType());
    }

    static boolean isInValidDimension(World world) {
        return world.getEnvironment() == World.Environment.NETHER || world.getEnvironment() == World.Environment.NORMAL;
    }

    enum Axis {
        X(org.bukkit.Axis.X, new Vector(1, 0, 0), new Vector(-1, 0, 0), BlockFace.EAST, BlockFace.WEST),
        Z(org.bukkit.Axis.Z, new Vector(0, 0, 1), new Vector(0, 0, -1), BlockFace.NORTH, BlockFace.SOUTH);

        final org.bukkit.Axis axis;
        final Vector pos;
        final Vector neg;
        final BlockFace left;
        final BlockFace right;

        Axis(org.bukkit.Axis axis, Vector pos, Vector neg, BlockFace left, BlockFace right) {
            this.axis = axis;
            this.pos = pos;
            this.neg = neg;
            this.left = left;
            this.right = right;
        }

        boolean rayTraceBlock(World world, Location source) {
            RayTraceResult positive = world.rayTraceBlocks(source, this.pos, config.maxPortalWidth, FluidCollisionMode.ALWAYS, false);
            RayTraceResult negative = world.rayTraceBlocks(source, this.neg, config.maxPortalWidth, FluidCollisionMode.ALWAYS, false);

            return isPortalFrameResult(positive) && isPortalFrameResult(negative);
        }

        void setOrientation(Block block) {
            BlockData blockData = block.getBlockData();
            if (blockData instanceof Orientable orientation) {
                orientation.setAxis(this.axis);
                block.setBlockData(orientation);
            }
        }
    }


}
