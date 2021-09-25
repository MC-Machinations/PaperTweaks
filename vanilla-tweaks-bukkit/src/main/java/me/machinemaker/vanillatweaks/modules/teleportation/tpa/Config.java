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
package me.machinemaker.vanillatweaks.modules.teleportation.tpa;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.validations.numbers.Min;
import me.machinemaker.vanillatweaks.config.I18nKey;
import me.machinemaker.vanillatweaks.config.VTConfig;
import me.machinemaker.vanillatweaks.menus.Menu;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static me.machinemaker.vanillatweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@Menu(commandPrefix = "/tpa config")
@VTConfig
class Config extends MenuModuleConfig<Config> {

    @Min(0)
    @I18nKey("modules.tpa.settings.request-cooldown")
    @Description("modules.tpa.settings.request-cooldown.extended")
    @Key("request-cooldown")
    int cooldown = 0;

    @Override
    public @NotNull Component title() {
        return join(text(" ".repeat(26) + "TPA"), text(" / ", GRAY), text("Global Settings" + " ".repeat(26) + "\n"));
    }
}
