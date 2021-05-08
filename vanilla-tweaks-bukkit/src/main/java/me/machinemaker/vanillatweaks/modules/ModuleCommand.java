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
package me.machinemaker.vanillatweaks.modules;

import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;

public abstract class ModuleCommand {

    @Inject
    protected PaperCommandManager<CommandDispatcher> manager;
    private boolean registered;

    final void registerCommands0(ModuleLifecycle lifecycle) {
        this.registerCommands(lifecycle);
        this.registered = true;
    }

    protected abstract void registerCommands(ModuleLifecycle lifecycle);

    boolean isRegistered() {
        return this.registered;
    }
}
