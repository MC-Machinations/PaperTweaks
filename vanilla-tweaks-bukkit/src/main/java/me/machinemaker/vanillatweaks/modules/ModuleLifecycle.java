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
package me.machinemaker.vanillatweaks.modules;

import cloud.commandframework.CommandManager;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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
    protected ModuleLifecycle(final JavaPlugin plugin, final Set<ModuleCommand> commands, final Set<ModuleListener> listeners, final Set<ModuleConfig> configs, final Set<ModuleRecipe<?>> moduleRecipes) {
        this.plugin = plugin;
        this.commands = commands;
        this.listeners = listeners;
        this.configs = configs;
        this.moduleRecipes = moduleRecipes.stream().collect(Collectors.toMap(ModuleRecipe::key, ModuleRecipe::recipe));
    }

    public void onEnable() {
    }

    public void onDisable(final boolean isShutdown) {
    }

    public void onReload() {
    }

    public final ModuleState getState() {
        return this.state;
    }

    protected final JavaPlugin getPlugin() {
        return this.plugin;
    }

    public @NotNull ModuleInfo moduleInfo() {
        return this.moduleInfo;
    }

    final void enable() {
        try {
            this.enableCommands();
            this.registerListeners();
            this.configs.forEach(ModuleConfig::reloadAndSave);
            this.registerRecipes();
            this.onEnable();
            this.state = ModuleState.ENABLED;
        } catch (final Exception e) {
            VanillaTweaks.LOGGER.error("Failed to enable {}", this.moduleInfo.name(), e);
            e.printStackTrace();
            this.state = ModuleState.ENABLED_FAILED;
            // TODO disable commands
            this.unregisterListeners();
            this.onDisable(false);
        }
    }

    final void disable(final boolean isShutdown) {
        this.disable(true, isShutdown);
    }

    final void disable(final boolean changeState, final boolean isShutdown) {
        try {
            // TODO disable commands
            this.unregisterListeners();
            this.unregisterRecipes();
            this.onDisable(isShutdown);
            if (changeState) this.state = ModuleState.DISABLED;
        } catch (final Exception e) {
            VanillaTweaks.LOGGER.error("Failed to disable {}", this.moduleInfo.name(), e);
            if (changeState) this.state = ModuleState.DISABLE_FAILED;
        }
    }

    final void reload() {
        try {
            if (this.state.isRunning()) {
                this.configs.forEach(ModuleConfig::reloadAndSave);
                this.registerRecipes();
                this.onReload();
                this.state = ModuleState.ENABLED;
            }
        } catch (final Exception e) {
            VanillaTweaks.LOGGER.error("Failed to reload {}", this.moduleInfo.name(), e);
            this.state = ModuleState.RELOAD_FAILED;
        }
    }

    private void enableCommands() {
        this.commandManager.setSetting(CommandManager.ManagerSettings.ALLOW_UNSAFE_REGISTRATION, true);
        this.commands.stream().filter(Predicate.not(ModuleCommand::isRegistered)).forEach(moduleCommand -> moduleCommand.registerCommands0(this));
        this.commandManager.setSetting(CommandManager.ManagerSettings.ALLOW_UNSAFE_REGISTRATION, false);
    }

    private void registerListeners() {
        this.listeners.forEach(listener -> this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin));
    }

    private void unregisterListeners() {
        this.listeners.forEach(HandlerList::unregisterAll);
    }

    private void registerRecipes() {
        this.moduleRecipes.forEach((key, recipe) -> {
            if (Bukkit.getRecipe(key) == null) {
                Bukkit.addRecipe(recipe);
            }
        });
        Bukkit.getOnlinePlayers().forEach(p -> p.discoverRecipes(this.moduleRecipes.keySet()));
    }

    private void unregisterRecipes() {
        this.moduleRecipes.forEach((key, recipe) -> {
            Bukkit.removeRecipe(key);
        });
        Bukkit.getOnlinePlayers().forEach(p -> p.undiscoverRecipes(this.moduleRecipes.keySet()));
    }

    public static class Empty extends ModuleLifecycle {

        @Inject
        protected Empty(final JavaPlugin plugin, final Set<ModuleCommand> commands, final Set<ModuleListener> listeners, final Set<ModuleConfig> configs, final Set<ModuleRecipe<?>> moduleRecipes) {
            super(plugin, commands, listeners, configs, moduleRecipes);
        }
    }
}
