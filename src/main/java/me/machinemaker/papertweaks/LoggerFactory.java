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
package me.machinemaker.papertweaks;

import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.modules.ModuleBase;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.slf4j.Logger;

@DefaultQualifier(NonNull.class)
public final class LoggerFactory {

    static final String GLOBAL_PREFIX = "PaperTweaks";
    private static final Logger PLUGIN_LOGGER = org.slf4j.LoggerFactory.getLogger(GLOBAL_PREFIX);

    private LoggerFactory() {
    }

    public static Logger getLogger() {
        return PLUGIN_LOGGER;
    }

    public static Logger getModuleLogger(final Class<? extends ModuleBase> moduleClass) {
        return getLogger(moduleClass.getAnnotation(ModuleInfo.class).name());
    }

    public static Logger getLogger(final Class<?> clazz) {
        return getLogger(clazz.getSimpleName());
    }

    public static Logger getLogger(final String name) {
        return org.slf4j.LoggerFactory.getLogger(PLUGIN_LOGGER.getName() + ": " + name);
    }
}
