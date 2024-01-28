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
package me.machinemaker.papertweaks.modules.experimental.elevators;

import me.machinemaker.papertweaks.utils.Entities;
import me.machinemaker.papertweaks.utils.runnables.ItemDropFinder;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Marker;

class ElevatorItemFinder extends ItemDropFinder {

    protected ElevatorItemFinder(final Item item) {
        super(item, 100);
    }

    @Override
    public boolean failCheck(final Item item) {
        return item.getItemStack().getAmount() != 1;
    }

    @Override
    public boolean successCheck(final Item item) {
        final Location loc = item.getLocation().subtract(0, 0.25, 0);
        if (Tag.WOOL.isTagged(loc.getBlock().getType())) {
            if (Entities.getNearbyEntitiesOfType(Marker.class, loc.getBlock().getLocation().add(0.5, 0.5, 0.5), 0.1, 0.1, 0.1, Elevators.IS_ELEVATOR::has).isEmpty()) {
                this.createElevator(item, loc);
                return true;
            }
        }
        return false;
    }

    private void createElevator(final Item item, final Location location) {
        final Block block = location.getBlock();
        final Location middle = block.getLocation().add(0.5, 0.5, 0.5);
        block.getWorld().spawn(middle, Marker.class, m -> {
            m.setInvulnerable(true);
            m.setGravity(false);
            Elevators.IS_ELEVATOR.setTo(m, true);
        });
        item.getItemStack().setAmount(0);
        item.remove();
    }
}
