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
package me.machinemaker.vanillatweaks.logging;

import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModuleLoggerFactory {

    private ModuleLoggerFactory() {
    }

    public static Logger module(Class<? extends ModuleBase> moduleClass) {
        ModuleInfo moduleInfo = moduleClass.getAnnotation(ModuleInfo.class);
        if (moduleInfo == null) {
            throw new IllegalArgumentException(moduleClass.getName() + " isn't annotated with " + ModuleInfo.class.getName());
        }
        return LoggerFactory.getLogger("[VanillaTweaks] [module: " + moduleInfo.name() + "]");
    }
}
