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
package me.machinemaker.vanillatweaks.modules.items.playerheaddrops;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.validations.numbers.Max;
import me.machinemaker.lectern.annotations.validations.numbers.Min;
import me.machinemaker.vanillatweaks.config.I18nKey;
import me.machinemaker.vanillatweaks.config.VTConfig;
import me.machinemaker.vanillatweaks.menus.Menu;
import me.machinemaker.vanillatweaks.modules.SimpleMenuModuleConfig;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@VTConfig
@Menu(commandPrefix = "/playerheaddrops admin config")
class Config extends SimpleMenuModuleConfig<Config> {

    @Key("head-drop-chance")
    @I18nKey("modules.player-head-drops.settings.drop-chance")
    @Description("modules.player-head-drops.settings.drop-chance.extended")
    @Min(0)
    @Max(1)
    public double dropChance = 1d;

    @Key("require-player-kill")
    @I18nKey("modules.player-head-drops.settings.require-player-kill")
    @Description("modules.player-head-drops.settings.require-player-kill.extended")
    public boolean requirePlayerKill = true;

    @Override
    protected @NotNull Component title() {
        return buildDefaultTitle("Player Head Drops");
    }
}
