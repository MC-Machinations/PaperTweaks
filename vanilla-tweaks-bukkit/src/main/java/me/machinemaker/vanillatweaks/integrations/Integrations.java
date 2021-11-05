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
package me.machinemaker.vanillatweaks.integrations;

import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.integrations.griefprevention.GPIntegration;
import me.machinemaker.vanillatweaks.integrations.worldguard.WGIntegration;
import org.bukkit.Bukkit;

import java.util.List;

public final class Integrations {

    private static boolean loaded = false;

    private Integrations() {
    }

    public static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        List.of(
                WGIntegration.INSTANCE,
                GPIntegration.INSTANCE
        ).forEach(integration -> {
            try {
                Class.forName(integration.className());
                integration.register();
                VanillaTweaks.LOGGER.info("Detected {} and successfully hooked into it!", integration.name());
            } catch (ClassNotFoundException ignored) {
                if (Bukkit.getPluginManager().getPlugin(integration.name()) != null) {
                    VanillaTweaks.LOGGER.error("Detected {} but was unable to hook into it!", integration.name());
                }
            }
        });
    }
}
