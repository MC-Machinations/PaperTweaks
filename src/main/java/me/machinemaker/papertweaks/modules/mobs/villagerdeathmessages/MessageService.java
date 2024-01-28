/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.modules.mobs.villagerdeathmessages;

import me.machinemaker.papertweaks.modules.ModuleMessageService;
import net.kyori.adventure.audience.Audience;
import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.annotation.Placeholder;
import org.bukkit.World;

interface MessageService extends ModuleMessageService {

    @Message("modules.villager-death-messages.on-death")
    void onVillagerDeath(Audience audience, @Placeholder int x, @Placeholder int y, @Placeholder int z, @Placeholder World world);

    @Message("modules.villager-death-messages.on-conversion")
    void onVillagerConversion(Audience audience, @Placeholder int x, @Placeholder int y, @Placeholder int z, @Placeholder World world);
}
