/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import me.machinemaker.lectern.ConfigurationNode;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Singleton
public final class ModuleManager {

    private final JavaPlugin plugin;
    private final NavigableMap<String, ModuleBase> moduleMap;
    private final Injector baseInjector;
    private final Map<String, Injector> moduleInjectors = Maps.newHashMap();
    private final ConfigurationNode modulesConfig;
    @Inject
    public ModuleManager(final JavaPlugin plugin, final Map<String, ModuleBase> moduleMap, final Injector baseInjector, @Named("modules") final ConfigurationNode modulesConfig) {
        this.plugin = plugin;
        this.moduleMap = new TreeMap<>(moduleMap);
        this.baseInjector = baseInjector;
        this.modulesConfig = modulesConfig;
    }

    public int loadModules() {
        for (final Map.Entry<String, ModuleBase> entry : this.moduleMap.entrySet()) {
            this.moduleInjectors.put(entry.getKey(), this.baseInjector.createChildInjector(entry.getValue()));
        }
        return this.moduleMap.size();

    }

    public int enableModules() {
        int count = 0;
        for (final Map.Entry<String, ModuleBase> entry : this.moduleMap.entrySet()) {
            if (isTrue(this.modulesConfig.get(entry.getValue().getConfigPath()))) {
                this.moduleInjectors.get(entry.getKey()).getInstance(ModuleLifecycle.class).enable();
                count++;
            }
        }
        return count;
    }

    public int disableModules(final boolean isShutdown) {
        int count = 0;
        for (final Map.Entry<String, ModuleBase> entry : this.moduleMap.entrySet()) {
            if (isTrue(this.modulesConfig.get(entry.getValue().getConfigPath()))) {
                final Injector injector = this.moduleInjectors.get(entry.getKey());
                if (injector != null) {
                    injector.getInstance(ModuleLifecycle.class).disable(isShutdown);
                    count++;
                }
            }
        }
        return count;
    }

    public ReloadResult reloadModules() {
        int disableCount = 0;
        int reloadCount = 0;
        int enableCount = 0;
        for (final Map.Entry<String, ModuleBase> entry : this.moduleMap.entrySet()) {
            final ModuleLifecycle moduleLifecycle = this.moduleInjectors.get(entry.getKey()).getInstance(ModuleLifecycle.class);
            final String configPath = entry.getValue().getConfigPath();
            if (moduleLifecycle.getState().isRunning() && isFalse(this.modulesConfig.get(configPath))) {
                moduleLifecycle.disable(false);
                disableCount++;
            } else if (moduleLifecycle.getState().isRunning() && isTrue(this.modulesConfig.get(configPath))) {
                moduleLifecycle.reload();
                reloadCount++;
            } else if (!moduleLifecycle.getState().isRunning() && isTrue(this.modulesConfig.get(configPath))) {
                moduleLifecycle.enable();
                enableCount++;
            }
        }
        if (enableCount > 0) {
            this.plugin.getServer().getOnlinePlayers().forEach(Player::updateCommands);
        }
        if (enableCount > 0 || disableCount > 0) {
            this.plugin.getServer().updateRecipes();
        }
        return new ReloadResult(disableCount, reloadCount, enableCount);
    }

    public Optional<ModuleBase> getModule(final String moduleName) {
        return this.getModule(moduleName, ModuleBase.class);
    }

    @SuppressWarnings("unchecked")
    public <M extends ModuleBase> Optional<M> getModule(final String moduleName, final Class<M> classOfM) {
        final ModuleBase module = this.moduleMap.get(moduleName.toLowerCase(Locale.US));
        if (module == null) {
            return Optional.empty();
        } else if (classOfM.isInstance(module)) {
            return Optional.of((M) module);
        } else {
            throw new IllegalArgumentException(module + " is not an instance of " + classOfM);
        }
    }

    public Optional<ModuleLifecycle> getLifecycle(final String moduleName) {
        return this.getLifecycle(moduleName, ModuleLifecycle.class);
    }

    public <L extends ModuleLifecycle> Optional<L> getLifecycle(final String moduleName, final Class<L> classOfL) {
        return Optional.ofNullable(this.moduleInjectors.get(moduleName.toLowerCase(Locale.US))).map(injector -> injector.getInstance(classOfL));
    }

    public Component enableModule(final String moduleName) {
        final ModuleLifecycle lifecycle = this.getLifecycle(moduleName, ModuleLifecycle.class).orElseThrow();
        if (lifecycle.getState().isRunning()) {
            return translatable("commands.enable.fail.already-enabled", YELLOW, text(moduleName, GOLD));
        }
        lifecycle.enable();
        this.plugin.getServer().getOnlinePlayers().forEach(Player::updateCommands);
        if (lifecycle.getState() == ModuleState.ENABLED_FAILED) {
            lifecycle.disable(false);
            return translatable("commands.enable.fail.error", RED, text(moduleName, GOLD));
        }
        this.modulesConfig.set(this.getModule(moduleName).orElseThrow().getConfigPath(), true);
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, this.modulesConfig::save);
        } else {
            this.modulesConfig.save();
        }
        return translatable("commands.enable.success", GREEN, text(moduleName, GOLD));
    }

    public void disableModule(final String moduleName, final Consumer<Component> msgConsumer) {
        final ModuleLifecycle lifecycle = this.getLifecycle(moduleName, ModuleLifecycle.class).orElseThrow();
        if (!lifecycle.getState().isRunning()) {
            msgConsumer.accept(translatable("commands.disable.fail.already-disabled", YELLOW, text(moduleName, GOLD)));
            return;
        }
        lifecycle.disable(false);
        if (lifecycle.getState() == ModuleState.DISABLE_FAILED) {
            msgConsumer.accept(translatable("commands.disable.fail.error", RED, text(moduleName, GOLD)));
            return;
        }
        // run a full reload after disabling the module
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            this.plugin.getServer().reloadData();
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, this.modulesConfig::save);
            msgConsumer.accept(translatable("commands.disable.success", GREEN, text(moduleName, GOLD)));
        }, 1L);
        this.modulesConfig.set(this.getModule(moduleName).orElseThrow().getConfigPath(), false);
    }

    public Component reloadModule(final String moduleName) {
        final ModuleLifecycle lifecycle = this.getLifecycle(moduleName, ModuleLifecycle.class).orElseThrow();
        if (lifecycle.getState().isRunning()) {
            lifecycle.reload();
            if (lifecycle.getState() == ModuleState.RELOAD_FAILED) {
                return translatable("commands.reload.module.fail.error", RED, text(moduleName, GOLD));
            }
            this.plugin.getServer().updateRecipes();
            return translatable("commands.reload.module.success", GREEN, text(moduleName, GOLD));
        } else {
            return translatable("commands.reload.module.fail.not-enabled", RED, text(moduleName, GOLD));
        }
    }

    public Map<String, ModuleBase> getModules() {
        return this.moduleMap;
    }

    public record ReloadResult(int disableCount, int reloadCount, int enableCount) {}
}
