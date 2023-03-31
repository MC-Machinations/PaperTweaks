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
package me.machinemaker.vanillatweaks.integrations.griefprevention;

import me.machinemaker.vanillatweaks.integrations.AbstractIntegration;
import me.machinemaker.vanillatweaks.integrations.Interactions;
import org.jetbrains.annotations.NotNull;

public class GPIntegration extends AbstractIntegration {

    public static final GPIntegration INSTANCE = new GPIntegration();

    private GPIntegration() {
    }

    @Override
    public void register() {
        Interactions.registerHandler(new GPInteractionHandler());
    }

    @Override
    public @NotNull String className() {
        return "me.ryanhamshire.GriefPrevention.GriefPrevention";
    }

    @Override
    public @NotNull String name() {
        return "GriefPrevention";
    }
}
