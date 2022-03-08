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
package me.machinemaker.vanillatweaks.integrations.worldguard;

import me.machinemaker.vanillatweaks.integrations.AbstractIntegration;
import me.machinemaker.vanillatweaks.integrations.Interactions;
import org.jetbrains.annotations.NotNull;

public final class WGIntegration extends AbstractIntegration {

    public static final WGIntegration INSTANCE = new WGIntegration();

    private WGIntegration() {
    }

    @Override
    public void register() {
        Interactions.registerHandler(new WGInteractionHandler());
    }

    @Override
    public @NotNull String className() {
        return "com.sk89q.worldguard.WorldGuard";
    }

    @Override
    @NotNull
    public String name() {
        return "WorldGuard";
    }
}
