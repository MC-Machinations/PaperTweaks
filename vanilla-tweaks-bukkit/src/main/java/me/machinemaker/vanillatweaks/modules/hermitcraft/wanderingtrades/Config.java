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
package me.machinemaker.vanillatweaks.modules.hermitcraft.wanderingtrades;

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

@Menu(commandPrefix = "/wanderingtrades admin config")
@VTConfig
class Config extends MenuModuleConfig<Config> {

    @I18nKey("modules.wandering-trades.settings.block-trades")
    @Description("modules.wandering-trades.settings.block-trades.extended")
    @Key("block-trades.enabled")
    boolean blockTradesEnabled = true;

    @Min(0)
    @I18nKey("modules.wandering-trades.settings.block-trades-min")
    @Key("block-trades.min")
    int blockMin = 5;

    @Min(1)
    @I18nKey("modules.wandering-trades.settings.block-trades-max")
    @Key("block-trades.max")
    int blockMax = 7;

    @I18nKey("modules.wandering-trades.settings.hermit-trades")
    @Description("modules.wandering-trades.settings.hermit-trades.extended")
    @Key("hermit-head-trades.enabled")
    boolean hermitHeadTradesEnabled = true;

    @Min(0)
    @I18nKey("modules.wandering-trades.settings.hermit-trades-min")
    @Key("hermit-head-trades.min")
    int headMin = 1;

    @Min(1)
    @I18nKey("modules.wandering-trades.settings.hermit-trades-max")
    @Key("hermit-head-trades.max")
    int headMax = 3;

    @Override
    public @NotNull Component title() {
        return join(text(" ".repeat(18) + "WanderingTrades"), text(" / ", GRAY), text("Global Settings" + " ".repeat(18) + "\n"));
    }
}

