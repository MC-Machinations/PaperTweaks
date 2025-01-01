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
package me.machinemaker.papertweaks.modules.survival.customnetherportals;

import com.google.inject.Inject;
import me.machinemaker.papertweaks.modules.ModuleListener;
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
import org.checkerframework.checker.nullness.qual.Nullable;

class IgniteListener implements ModuleListener {

    @Inject
    private static Config config;

    static boolean isPortalFrameResult(final @Nullable RayTraceResult result) {
        if (result == null) return false;
        return isPortalFrame(result.getHitBlock());
    }

    static boolean isPortalFrame(final @Nullable Block block) {
        if (block == null) return false;
        return config.portalFrameMaterials.contains(block.getType());
    }

    static boolean isInValidDimension(final World world) {
        return world.getEnvironment() == World.Environment.NETHER || world.getEnvironment() == World.Environment.NORMAL;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        final Block block = event.getBlock();
        final World world = block.getWorld();
        if (isInValidDimension(world) && isPortalFrame(block.getRelative(BlockFace.DOWN))) {
            final @Nullable Axis portalAxis = this.findPortalAxis(world, block);
            if (portalAxis == null) return;

            event.setCancelled(new PortalShapeFinder(block, portalAxis).start());
        }
    }

    @Nullable Axis findPortalAxis(final World world, final Block source) {
        for (final Axis axis : Axis.values()) {
            if (axis.rayTraceBlock(world, source.getLocation())) {
                return axis;
            }
        }
        return null;
    }

    enum Axis {
        X(org.bukkit.Axis.X, new Vector(1, 0, 0), new Vector(-1, 0, 0), BlockFace.EAST, BlockFace.WEST),
        Z(org.bukkit.Axis.Z, new Vector(0, 0, 1), new Vector(0, 0, -1), BlockFace.NORTH, BlockFace.SOUTH);

        final org.bukkit.Axis axis;
        final Vector pos;
        final Vector neg;
        final BlockFace left;
        final BlockFace right;

        Axis(final org.bukkit.Axis axis, final Vector pos, final Vector neg, final BlockFace left, final BlockFace right) {
            this.axis = axis;
            this.pos = pos;
            this.neg = neg;
            this.left = left;
            this.right = right;
        }

        boolean rayTraceBlock(final World world, final Location source) {
            final @Nullable RayTraceResult positive = world.rayTraceBlocks(source, this.pos, config.maxPortalWidth, FluidCollisionMode.ALWAYS, false);
            final @Nullable RayTraceResult negative = world.rayTraceBlocks(source, this.neg, config.maxPortalWidth, FluidCollisionMode.ALWAYS, false);

            return isPortalFrameResult(positive) && isPortalFrameResult(negative);
        }

        void setOrientation(final Block block) {
            final BlockData blockData = block.getBlockData();
            if (blockData instanceof final Orientable orientation && orientation.getAxis() != this.axis) {
                orientation.setAxis(this.axis);
                block.setBlockData(orientation);
            }
        }
    }


}
