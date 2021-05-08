/*
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
package me.machinemaker.vanillatweaks.modules.survival.durabilityping;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;

@LecternConfiguration
class Config extends ModuleConfig {

    @Key("notification-threshold-percent")
    @Description("Value between 0 (inclusive) and 1 (exclusive) for the percentage at which to start notifying the player")
    public double threshold = 0.02d;

    @Key("notification-cooldown-seconds")
    @Description("Cooldown in seconds between notifications to limit spam")
    public int notificationCooldown = 10;

    @Key("defaults.hand-ping")
    @Description("Default setting for notifying players when their main/off hand tools get low")
    public boolean defaultHandPing = true;

    @Key("defaults.armor-ping")
    @Description("Default setting for notifying players when their equipped armor gets low")
    public boolean defaultArmorPing = true;

    @Key("defaults.play-sound")
    @Description("Default setting for playing a sound upon a notification")
    public boolean defaultPlaySound = true;

    @Key("defaults.display")
    @Description("Default setting for displaying the notification to the player. Can be one of: TITLE, SUBTITLE, CHAT, ACTION_BAR, HIDDEN")
    public Settings.DisplaySetting defaultDisplaySetting = Settings.DisplaySetting.SUBTITLE;


    @Key("enabled-by-default")
    public Boolean enabledByDefault = true;
}
