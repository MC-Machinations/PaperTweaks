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
package me.machinemaker.papertweaks.modules.survival.cauldronconcrete;

import com.destroystokyo.paper.MaterialTags;
import java.util.Collection;
import java.util.Set;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import org.bukkit.Material;

import static java.util.Objects.requireNonNull;

@ModuleInfo(name = "CauldronConcrete", configPath = "survival.cauldron-concrete", description = "Make concrete using cauldrons")
public class CauldronConcrete extends ModuleBase {

    static Material toConcreteFromPowder(final Material concretePowder) {
        if (!MaterialTags.CONCRETE_POWDER.isTagged(concretePowder)) {
            throw new IllegalArgumentException(concretePowder + " is not a concrete powder");
        }

        return requireNonNull(Material.matchMaterial(concretePowder.name().split("_POWDER")[0]));
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
