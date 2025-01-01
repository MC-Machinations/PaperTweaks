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
package me.machinemaker.papertweaks.modules.survival.graves;

import java.util.List;
import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.papertweaks.config.I18nKey;
import me.machinemaker.papertweaks.config.PTConfig;
import me.machinemaker.papertweaks.menus.Menu;
import me.machinemaker.papertweaks.modules.SimpleMenuModuleConfig;
import net.kyori.adventure.text.Component;

@PTConfig
@Menu(commandPrefix = "/graves admin config")
class Config extends SimpleMenuModuleConfig<Config> {

    @Key("legacy-shift-behavior")
    @I18nKey("modules.graves.settings.legacy-shift-behavior")
    @Description("modules.graves.settings.legacy-shift-behavior.extended")
    public boolean legacyShiftBehavior = false;

    @Key("grave-robbing")
    @I18nKey("modules.graves.settings.grave-robbing")
    @Description("modules.graves.settings.grave-robbing.extended")
    public boolean graveRobbing = false;

    @Key("grave-robbing-timer")
    @I18nKey("modules.graves.settings.grave-robbing-timer")
    @Description("modules.graves.settings.grave-robbing-timer.extended")
    public int graveRobbingTimer = 0;

    @Key("grave-locating")
    @I18nKey("modules.graves.settings.grave-locating")
    @Description("modules.graves.settings.grave-locating.extended")
    public boolean graveLocating = true;

    @Key("xp-collection")
    @I18nKey("modules.graves.settings.xp-collection")
    @Description("modules.graves.settings.xp-collection.extended")
    public boolean xpCollection = true;

    @Key("disabled-worlds")
    @Description("Worlds listed here will not create graves for players")
    public List<String> disabledWorlds = List.of("disabled_world_name");

    @Override
    protected Component title() {
        return buildDefaultTitle("Graves");
    }
}
