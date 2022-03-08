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
package me.machinemaker.vanillatweaks.modules.mobs.moremobheads;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;

@ModuleCommand.Info(value = "moremobheads", aliases = {"mmheads", "mmh"}, i18n = "more-mob-heads", perm = "moremobheads")
class Commands extends ConfiguredModuleCommand {

    private final Config config;

    @Inject
    Commands(Config config) {
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        this.config.createCommands(this, this.player());
    }
}
