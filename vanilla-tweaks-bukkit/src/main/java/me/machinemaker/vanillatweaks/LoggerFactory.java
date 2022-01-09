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
package me.machinemaker.vanillatweaks;

import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import org.slf4j.Logger;

public final class LoggerFactory {

    private static final String GLOBAL_PREFIX = "VanillaTweaks";
    private static final Logger PLUGIN_LOGGER = org.slf4j.LoggerFactory.getLogger(GLOBAL_PREFIX);

    private LoggerFactory() {
    }

    public static Logger getLogger() {
        return PLUGIN_LOGGER;
    }

    public static Logger getModuleLogger(Class<? extends ModuleBase> moduleClass) {
        return getLogger(moduleClass.getAnnotation(ModuleInfo.class).name());
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getSimpleName());
    }

    public static Logger getLogger(String name) {
        return org.slf4j.LoggerFactory.getLogger(PLUGIN_LOGGER.getName() + ": " + name);
    }
}
