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

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ConfiguredModuleCommand;

class Commands extends ConfiguredModuleCommand {

    private final Config config;

    @Inject
    Commands(Config config) {
        super("wandering-trades", "wanderingtrades");
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        var builder = playerCmd("wanderingtrades", "modules.wandering-trades.commands.root", "wtrades", "wt");

        this.config.createCommands(this, builder);
    }
}
