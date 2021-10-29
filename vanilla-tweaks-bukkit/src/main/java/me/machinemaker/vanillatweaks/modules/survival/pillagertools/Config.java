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
package me.machinemaker.vanillatweaks.modules.survival.pillagertools;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.vanillatweaks.config.I18nKey;
import me.machinemaker.vanillatweaks.config.VTConfig;
import me.machinemaker.vanillatweaks.menus.Menu;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@Menu(commandPrefix = "/pillagertools admin config")
@VTConfig
class Config extends MenuModuleConfig<Config> {

    @Key("bad-omen")
    @I18nKey("modules.pillager-tools.settings.bad_omen")
    @Description("modules.pillager-tools.settings.bad_omen.extended")
    public boolean badOmen = true;

    @Key("patrol-leaders")
    @I18nKey("modules.pillager-tools.settings.patrol_leaders")
    @Description("modules.pillager-tools.settings.patrol_leaders.extended")
    public boolean patrolLeaders = true;

    @Key("patrols")
    @I18nKey("modules.pillager-tools.settings.patrols")
    @Description("modules.pillager-tools.settings.patrols.extended")
    public boolean patrols = true;

    boolean getSettingValue(PillagerTools.ToggleOption option) {
        return switch (option) {
            case PATROLS -> this.patrols;
            case BAD_OMEN -> this.badOmen;
            case PATROL_LEADERS -> this.patrolLeaders;
        };
    }

    @Override
    public @NotNull Component title() {
        return buildDefaultTitle("Pillager Tools");
    }
}
