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

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.validations.numbers.Positive;
import me.machinemaker.vanillatweaks.config.VTConfig;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import org.bukkit.Material;

import java.util.LinkedHashSet;
import java.util.List;

@VTConfig
class Config extends ModuleConfig {

    @Key("portal-frame-materials")
    @Description("Any material in this list will be considered a portal frame material")
    public LinkedHashSet<Material> portalFrameMaterials = new LinkedHashSet<>(List.of(Material.OBSIDIAN, Material.CRYING_OBSIDIAN));

    @Positive
    @Key("size.min-portal-blocks")
    @Description("Minimum number of portals block spaces required to create a valid portal")
    public int minPortalSize = 6;

    @Positive
    @Key("size.max-portal-width")
    @Description("How wide the portal can be. Larger numbers may cause issues")
    public int maxPortalWidth = 23;

    @Positive
    @Key("size.max-portal-height")
    @Description("How tall the portal can be. Larger numbers may cause issues")
    public int maxPortalHeight = 23;
}
