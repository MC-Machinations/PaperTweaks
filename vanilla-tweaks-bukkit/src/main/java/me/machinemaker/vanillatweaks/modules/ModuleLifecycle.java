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

import cloud.commandframework.CommandManager.ManagerSettings;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class ModuleLifecycle {

    private final JavaPlugin plugin;
    private final Set<ModuleCommand> commands;
    private final Set<ModuleListener> listeners;
    private final Set<ModuleConfig> configs;
    private final Map<NamespacedKey, Recipe> moduleRecipes;
    private ModuleState state = ModuleState.DISABLED;
    @Inject
    private PaperCommandManager<CommandDispatcher> commandManager;
    @Inject
    private ModuleInfo moduleInfo;

    @Inject
    protected ModuleLifecycle(JavaPlugin plugin, Set<ModuleCommand> commands, Set<ModuleListener> listeners, Set<ModuleConfig> configs, Set<ModuleRecipe<?>> moduleRecipes) {
        this.plugin = plugin;
        this.commands = commands;
        this.listeners = listeners;
        this.configs = configs;
        this.moduleRecipes = moduleRecipes.stream().collect(Collectors.toMap(ModuleRecipe::key, ModuleRecipe::recipe));
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

    public @NotNull ModuleInfo moduleInfo() {
        return moduleInfo;
    }

    final void enable() {
        try {
            enableCommands();
            registerListeners();
            configs.forEach(ModuleConfig::reloadAndSave);
            registerRecipes();
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
            if (!this.state.isErrored()) {
                configs.forEach(ModuleConfig::save);
            }
            unregisterRecipes();
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
                configs.forEach(ModuleConfig::reloadAndSave);
                registerRecipes();
                onReload();
                state = ModuleState.ENABLED;
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

    private void registerRecipes() {
        moduleRecipes.forEach((key, recipe) -> {
            if (Bukkit.getRecipe(key) == null) {
                Bukkit.addRecipe(recipe);
            }
        });
        Bukkit.getOnlinePlayers().forEach(p -> p.discoverRecipes(moduleRecipes.keySet()));
    }

    private void unregisterRecipes() {
        moduleRecipes.forEach((key, recipe) -> {
            Bukkit.removeRecipe(key);
        });
        Bukkit.getOnlinePlayers().forEach(p -> p.undiscoverRecipes(moduleRecipes.keySet()));
    }

    public static class Empty extends ModuleLifecycle {

        @Inject
        protected Empty(JavaPlugin plugin, Set<ModuleCommand> commands, Set<ModuleListener> listeners, Set<ModuleConfig> configs, Set<ModuleRecipe<?>> moduleRecipes) {
            super(plugin, commands, listeners, configs, moduleRecipes);
        }
    }
}