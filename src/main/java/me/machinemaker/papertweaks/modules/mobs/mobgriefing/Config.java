/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.papertweaks.modules.mobs.mobgriefing;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.papertweaks.config.VTConfig;
import me.machinemaker.papertweaks.migrations.ModulesFileMigrations;
import me.machinemaker.papertweaks.modules.ModuleConfig;

@VTConfig
class Config extends ModuleConfig {

    @Key("anti-enderman-grief")
    @Description("Prevents enderman from picking up blocks")
    public boolean antiEndermanGrief = Boolean.parseBoolean(System.getProperty(ModulesFileMigrations.MOB_GRIEFING_ENDERMAN, Boolean.FALSE.toString()));

    @Key("anti-ghast-grief")
    public boolean antiGhastGrief = Boolean.parseBoolean(System.getProperty(ModulesFileMigrations.MOB_GRIEFING_GHAST, Boolean.FALSE.toString()));

    @Key("anti-creeper-grief")
    public boolean antiCreeperGrief = Boolean.parseBoolean(System.getProperty(ModulesFileMigrations.MOB_GRIEFING_CREEPER, Boolean.FALSE.toString()));;

    @Key("disable-entity-damage")
    @Description("When enabled both block and entity damage will be cancelled for creeper and ghast fireball explosions. Disable to turn on entity damage for those explosions")
    public boolean disableEntityDamage = true;
}
