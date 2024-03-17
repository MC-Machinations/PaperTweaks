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
package me.machinemaker.papertweaks;

import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.paper.PaperCommandManager;
import com.google.inject.Inject;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ModuleManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class GlobalListener implements Listener {

    private final ModuleManager moduleManager;
    private final PaperCommandManager<CommandDispatcher> commandManager;

    @Inject
    public GlobalListener(final ModuleManager moduleManager, final PaperCommandManager<CommandDispatcher> commandManager) {
        this.moduleManager = moduleManager;
        this.commandManager = commandManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBukkitReload(final ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD) {
            this.moduleManager.reloadModules();
        } else if (event.getType() == ServerLoadEvent.LoadType.STARTUP && this.commandManager.hasCapability(CloudBukkitCapabilities.COMMODORE_BRIGADIER)) {
            // This is very annoying
            ModuleManager.reSyncCommands();
        }
    }

}
