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
package me.machinemaker.vanillatweaks.modules;

import cloud.commandframework.CommandManager.ManagerSettings;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.cloud.CommandDispatcher;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;

public abstract class ModuleLifecycle {

    private final JavaPlugin plugin;
    private final Set<ModuleCommand> commands;
    private final Set<ModuleListener> listeners;
    private final Set<ModuleConfig> configs;
    private ModuleState state = ModuleState.DISABLED;
    @Inject
    private PaperCommandManager<CommandDispatcher> commandManager;
    @Inject
    private ModuleInfo moduleInfo;

    @Inject
    protected ModuleLifecycle(JavaPlugin plugin, Set<ModuleCommand> commands, Set<ModuleListener> listeners, Set<ModuleConfig> configs) {
        this.plugin = plugin;
        this.commands = commands;
        this.listeners = listeners;
        this.configs = configs;
    }

    public void onEnable() { }

    public void onDisable() { }

    public void onReload() { }

    public final ModuleState getState() {
        return state;
    }

    protected final JavaPlugin getPlugin() {
        return this.plugin;
    }

    final void enable() {
        try {
            enableCommands();
            registerListeners();
            configs.forEach(ModuleConfig::reloadOrSaveAndSave);
            onEnable();
            state = ModuleState.ENABLED;
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, e, () -> "Failed to enable " + this.moduleInfo.name());
            e.printStackTrace();
            state = ModuleState.ENABLED_FAILED;
            // TODO disable commands
            unregisterListeners();
            onDisable();
        }
    }

    final void disable() {
        disable(true);
    }

    final void disable(boolean changeState) {
        try {
            // TODO disable commands
            unregisterListeners();
            configs.forEach(ModuleConfig::save);
            onDisable();
            if (changeState) state = ModuleState.DISABLED;
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, e, () -> "Failed to disable " + this.moduleInfo.name());
            if (changeState) state = ModuleState.DISABLE_FAILED;
        }
    }

    final void reload() {
        try {
            if (state.isRunning()) {
                configs.forEach(ModuleConfig::reloadOrSaveAndSave);
                onReload();
            }
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, e, () -> "Failed to reload " + this.moduleInfo.name());
            state = ModuleState.RELOAD_FAILED;
        }
    }

    private void enableCommands() {
        commandManager.setSetting(ManagerSettings.ALLOW_UNSAFE_REGISTRATION, true);
        commands.stream().filter(Predicate.not(ModuleCommand::isRegistered)).forEach(moduleCommand -> moduleCommand.registerCommands0(this));
        commandManager.setSetting(ManagerSettings.ALLOW_UNSAFE_REGISTRATION, false);
    }

    private void registerListeners() {
        listeners.forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
    }

    private void unregisterListeners() {
        listeners.forEach(HandlerList::unregisterAll);
    }

    public static class SimpleLifecycle extends ModuleLifecycle {

        @Inject
        protected SimpleLifecycle(JavaPlugin plugin, Set<ModuleCommand> commands, Set<ModuleListener> listeners, Set<ModuleConfig> configs) {
            super(plugin, commands, listeners, configs);
        }
    }
}