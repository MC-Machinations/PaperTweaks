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
package me.machinemaker.vanillatweaks.modules.experimental.elevators;

import me.machinemaker.vanillatweaks.utils.VTUtils;
import me.machinemaker.vanillatweaks.utils.runnables.ItemDropFinder;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Marker;
import org.jetbrains.annotations.NotNull;

class ElevatorItemFinder extends ItemDropFinder {

    protected ElevatorItemFinder(@NotNull Item item) {
        super(item, 100);
    }

    @Override
    public boolean failCheck(@NotNull Item item) {
        return item.getItemStack().getAmount() != 1;
    }

    @Override
    public boolean successCheck(@NotNull Item item) {
        Location loc = item.getLocation().subtract(0, 0.25, 0);
        if (Tag.WOOL.isTagged(loc.getBlock().getType())) {
            if (VTUtils.getNearbyEntitiesOfType(Marker.class, loc.getBlock().getLocation().add(0.5, 0.5, 0.5), 0.1, 0.1, 0.1, Elevators.IS_ELEVATOR::has).isEmpty()) {
                createElevator(item, loc);
                return true;
            }
        }
        return false;
    }

    private void createElevator(Item item, Location location) {
        Block block = location.getBlock();
        Location middle = block.getLocation().add(0.5, 0.5, 0.5);
        block.getWorld().spawn(middle, Marker.class, m -> {
            m.setInvulnerable(true);
            m.setGravity(false);
            Elevators.IS_ELEVATOR.setTo(m, true);
        });
        item.getItemStack().setAmount(0);
        item.remove();
    }
}
