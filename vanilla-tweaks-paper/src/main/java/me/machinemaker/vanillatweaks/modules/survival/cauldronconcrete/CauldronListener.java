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
package me.machinemaker.vanillatweaks.modules.survival.cauldronconcrete;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

class CauldronListener implements ModuleListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityInsideBlock(EntityInsideBlockEvent event) {
        if (event.getEntity() instanceof Item item && event.getBlock().getType() == Material.WATER_CAULDRON && MaterialTags.CONCRETE_POWDER.isTagged(item.getItemStack())) {
            item.getWorld().dropItem(item.getLocation(), new ItemStack(CauldronConcrete.toConcreteFromPowder(item.getItemStack().getType()), item.getItemStack().getAmount()));
            item.remove();
        }
    }
}
