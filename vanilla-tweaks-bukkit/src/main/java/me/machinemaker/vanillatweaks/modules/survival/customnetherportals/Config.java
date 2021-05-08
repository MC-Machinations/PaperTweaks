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

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@LecternConfiguration
class Config extends ModuleConfig {

    @Inject
    private static JavaPlugin plugin;

    @Key("portal-frame-materials")
    @Description("Any material in this list will be considered a portal frame material")
    public List<String> portalFrameMaterialStrings = List.of(Material.OBSIDIAN.getKey().toString(), Material.CRYING_OBSIDIAN.getKey().toString());


    @Key("size.min-portal-blocks")
    @Description("Minimum number of portals block spaces required to create a valid portal")
    public int minPortalSize = 6;

    @Key("size.max-portal-width")
    @Description("How wide the portal can be. Larger numbers may cause issues")
    public int maxPortalWidth = 23;

    @Key("size.max-portal-height")
    @Description("How tall the portal can be. Larger numbers may cause issues")
    public int maxPortalHeight = 23;

    List<Material> portalFrameMaterials(boolean log) {
        List<Material> mats = Lists.newArrayList();
        for (String string : this.portalFrameMaterialStrings) {
            Material m = Material.matchMaterial(string);
            if (m != null) {
                mats.add(m);
            } else if (log) {
                plugin.getLogger().warning(string + " could not be turned into a valid material, skipping");
            }
        }
        return mats;
    }
}
