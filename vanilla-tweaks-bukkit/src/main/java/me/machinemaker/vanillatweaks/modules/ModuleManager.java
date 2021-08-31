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

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import me.machinemaker.lectern.LecternConfig;
import me.machinemaker.vanillatweaks.utils.ReflectionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static org.apache.commons.lang.BooleanUtils.isFalse;
import static org.apache.commons.lang.BooleanUtils.isTrue;

public class ModuleManager {

    private static final Class<?> MINECRAFT_SERVER_CLASS = ReflectionUtils.getMinecraftClass("server.MinecraftServer");
    private static final ReflectionUtils.MethodInvoker GET_MINECRAFT_SERVER_METHOD = ReflectionUtils.getTypedMethod(MINECRAFT_SERVER_CLASS, null, MINECRAFT_SERVER_CLASS);
    private static final Object MINECRAFT_SERVER = GET_MINECRAFT_SERVER_METHOD.invoke(null);

    private static final Class<?> PLAYER_LIST_CLASS = ReflectionUtils.getMinecraftClass("server.players.PlayerList");
    private static final ReflectionUtils.MethodInvoker GET_PLAYER_LIST_METHOD = ReflectionUtils.getTypedMethod(MINECRAFT_SERVER_CLASS, null, PLAYER_LIST_CLASS);
    private static final Object PLAYER_LIST = GET_PLAYER_LIST_METHOD.invoke(MINECRAFT_SERVER);

    private static final Class<?> CRAFT_SERVER_CLASS = Bukkit.getServer().getClass();
    private static final ReflectionUtils.MethodInvoker SYNC_COMMANDS_METHOD = ReflectionUtils.getMethod(CRAFT_SERVER_CLASS, "syncCommands");

    private static final ReflectionUtils.MethodInvoker RESEND_DATA_METHOD = ReflectionUtils.getMethod(PLAYER_LIST_CLASS, "reload");

    private static void resendData() {
        RESEND_DATA_METHOD.invoke(PLAYER_LIST);
    }

    private static void reSyncCommands() {
        SYNC_COMMANDS_METHOD.invoke(Bukkit.getServer());
    }

    private final Map<String, ModuleBase> moduleMap;
    private final Injector baseInjector;
    private final Map<String, Injector> moduleInjectors = Maps.newHashMap();
    private final LecternConfig modulesConfig;

    @Inject
    public ModuleManager(Map<String, ModuleBase> moduleMap, Injector baseInjector, @Named("modules") LecternConfig modulesConfig) {
        this.moduleMap = moduleMap;
        this.baseInjector = baseInjector;
        this.modulesConfig = modulesConfig;
    }

    public int loadModules() {
        for (var entry : moduleMap.entrySet()) {
            moduleInjectors.put(entry.getKey(), baseInjector.createChildInjector(entry.getValue()));
        }
        return moduleMap.size();

    }

    public int enableModules() {
        int count = 0;
        for (var entry : moduleMap.entrySet()) {
            if (isTrue(modulesConfig.get(entry.getValue().getConfigPath()))) {
                moduleInjectors.get(entry.getValue().getName()).getInstance(ModuleLifecycle.class).enable();
                count++;
            }
        }
        return count;
    }

    public int disableModules() {
        int count = 0;
        for (var entry : moduleMap.entrySet()) {
            if (isTrue(modulesConfig.get(entry.getValue().getConfigPath()))) {
                moduleInjectors.get(entry.getValue().getName()).getInstance(ModuleLifecycle.class).disable();
                count++;
            }
        }
        return count;
    }

    public ReloadResult reloadModules() {
        int disableCount = 0;
        int reloadCount = 0;
        int enableCount = 0;
        for (var entry : moduleMap.entrySet()) {
            ModuleLifecycle moduleLifecycle = this.moduleInjectors.get(entry.getKey()).getInstance(ModuleLifecycle.class);
            String configPath = entry.getValue().getConfigPath();
            if (moduleLifecycle.getState().isRunning() && isFalse(this.modulesConfig.get(configPath))) {
                moduleLifecycle.disable();
                disableCount++;
            } else if (moduleLifecycle.getState().isRunning() && isTrue(this.modulesConfig.get(configPath))) {
                moduleLifecycle.reload();
                reloadCount++;
            } else if (!moduleLifecycle.getState().isRunning() && isTrue(this.modulesConfig.get(configPath))) {
                moduleLifecycle.enable();
                enableCount++;
            }
        }
        return new ReloadResult(disableCount, reloadCount, enableCount);
    }

    public Optional<ModuleLifecycle> getLifecycle(String moduleName) {
        return getLifecycle(moduleName, ModuleLifecycle.class);
    }

    public <L extends ModuleLifecycle> Optional<L> getLifecycle(String moduleName, Class<L> classOfL) {
        return Optional.ofNullable(this.moduleInjectors.get(moduleName)).map(injector -> injector.getInstance(classOfL));
    }

    public Component enableModule(String moduleName) {
        ModuleLifecycle lifecycle = getLifecycle(moduleName, ModuleLifecycle.class).orElseThrow();
        if (lifecycle.getState().isRunning()) {
            return translatable("commands.enable.fail.already-enabled", YELLOW, text(moduleName, GOLD));
        }
        lifecycle.enable();
        reSyncCommands();
        resendData();
        if (lifecycle.getState() == ModuleState.ENABLED_FAILED) {
            lifecycle.disable(false);
            return translatable("commands.enable.fail.error", RED, text(moduleName, GOLD));
        }
        this.modulesConfig.set(this.moduleMap.get(moduleName).getConfigPath(), true);
        this.modulesConfig.save();
        return translatable("commands.enable.success", GREEN, text(moduleName, GOLD));
    }

    public Component disableModule(String moduleName) {
        ModuleLifecycle lifecycle = getLifecycle(moduleName, ModuleLifecycle.class).orElseThrow();
        if (!lifecycle.getState().isRunning()) {
            return translatable("commands.disable.fail.already-disabled", YELLOW, text(moduleName, GOLD));
        }
        lifecycle.disable();
        resendData();
        if (lifecycle.getState() == ModuleState.DISABLE_FAILED) {
            return translatable("commands.disable.fail.error", RED, text(moduleName, GOLD));
        }
        this.modulesConfig.set(this.moduleMap.get(moduleName).getConfigPath(), false);
        this.modulesConfig.save();
        return translatable("commands.disable.success", GREEN, text(moduleName, GOLD));
    }

    public Component reloadModule(String moduleName) {
        ModuleLifecycle lifecycle = getLifecycle(moduleName, ModuleLifecycle.class).orElseThrow();
        if (lifecycle.getState().isRunning()) {
            lifecycle.reload();
            if (lifecycle.getState() == ModuleState.RELOAD_FAILED) {
                return translatable("commands.reload.module.fail.error", RED, text(moduleName, GOLD));
            }
            return translatable("commands.reload.module.success", GREEN, text(moduleName, GOLD));
        } else {
            return translatable("commands.reload.module.fail.not-enabled", RED, text(moduleName, GOLD));
        }
    }

    public Map<String, ModuleBase> getModules() {
        return moduleMap;
    }

    public record ReloadResult(int disableCount, int reloadCount, int enableCount) {}
}
