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
package me.machinemaker.papertweaks.modules.survival.durabilityping;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.validations.numbers.Min;
import me.machinemaker.papertweaks.config.I18nKey;
import me.machinemaker.papertweaks.config.PTConfig;
import me.machinemaker.papertweaks.menus.Menu;
import me.machinemaker.papertweaks.modules.SimpleMenuModuleConfig;
import net.kyori.adventure.text.Component;

@PTConfig
@Menu(commandPrefix = "/durabilityping admin config")
class Config extends SimpleMenuModuleConfig<Config> {

    @Key("uses-left")
    @I18nKey("modules.durability-ping.settings.uses-left")
    @Description("modules.durability-ping.settings.uses-left.extended")
    @Min(1)
    public int usesLeft = 10;

    @Key("notification-cooldown-seconds")
    @I18nKey("modules.durability-ping.settings.notification-cooldown-seconds")
    @Description("modules.durability-ping.settings.notification-cooldown-seconds.extended")
    @Min(0)
    public int notificationCooldown = 10;

    @Key("defaults.hand-ping")
    @I18nKey("modules.durability-ping.settings.defaults.hand-ping")
    @Description("modules.durability-ping.settings.defaults.hand-ping.extended")
    public boolean defaultHandPing = true;

    @Key("defaults.armor-ping")
    @I18nKey("modules.durability-ping.settings.defaults.armor-ping")
    @Description("modules.durability-ping.settings.defaults.armor-ping.extended")
    public boolean defaultArmorPing = true;

    @Key("defaults.play-sound")
    @I18nKey("modules.durability-ping.settings.defaults.play-sound")
    @Description("modules.durability-ping.settings.defaults.play-sound.extended")
    public boolean defaultPlaySound = true;

    @Key("defaults.display")
    @I18nKey("modules.durability-ping.settings.defaults.display")
    @Description("Default setting for displaying the notification to the player. Can be one of: TITLE, SUBTITLE, CHAT, ACTION_BAR, HIDDEN")
    public Settings.DisplaySetting defaultDisplaySetting = Settings.DisplaySetting.SUBTITLE;

    @Override
    protected Component title() {
        return buildDefaultTitle("Durability Ping");
    }
}
