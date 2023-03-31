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
package me.machinemaker.vanillatweaks.modules.survival.realtimeclock;

import me.machinemaker.vanillatweaks.modules.ModuleMessageService;
import me.machinemaker.vanillatweaks.moonshine.annotation.TextColor;
import net.kyori.adventure.audience.Audience;
import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.annotation.Placeholder;
import org.bukkit.World;

public interface MessageService extends ModuleMessageService {

    @TextColor(TextColor.YELLOW)
    @Message("modules.real-time-clock.show-time.minutes")
    void showTimeMinutes(Audience audience, @Placeholder("m") int minutes, @Placeholder("s") int seconds, @Placeholder World world);

    @TextColor(TextColor.YELLOW)
    @Message("modules.real-time-clock.show-time.hours")
    void showTimeHours(Audience audience, @Placeholder("h") int hours, @Placeholder("m") int minutes, @Placeholder("s") int seconds, @Placeholder World world);

    @TextColor(TextColor.YELLOW)
    @Message("modules.real-time-clock.show-time.days")
    void showTimeDays(Audience audience, @Placeholder("d") long days, @Placeholder("h") int hours, @Placeholder("m") int minutes, @Placeholder("s") int seconds, @Placeholder World world);
}
