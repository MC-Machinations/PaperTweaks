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
package me.machinemaker.papertweaks.modules.survival.cauldronmud;

import java.util.Collection;
import java.util.Set;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import org.bukkit.Material;

@ModuleInfo(name = "CauldronMud", configPath = "survival.cauldron-mud", description = "Make mud using cauldrons")
public class CauldronMud extends ModuleBase {

    static Material toMudFromDirt(final Material dirt) {
        if (!(dirt.equals(Material.DIRT) || dirt.equals(Material.COARSE_DIRT) || dirt.equals(Material.ROOTED_DIRT))) {
            throw new IllegalArgumentException(dirt + " is not a valid dirt type!");
        }

        return Material.MUD;
    }

    @Override
    protected Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(CauldronListener.class);
    }

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.Empty.class;
    }
}
