/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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
package me.machinemaker.vanillatweaks.cloud;

import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.permission.AndPermission;
import cloud.commandframework.permission.CommandPermission;
import cloud.commandframework.permission.Permission;
import cloud.commandframework.permission.PredicatePermission;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;

import java.util.Set;

public final class ModulePermission {

    private ModulePermission() {
    }

    public static CommandPermission of(ModuleLifecycle lifecycle, String permission) {
        return AndPermission.of(Set.of(
                Permission.of(permission),
                of(lifecycle)
        ));
    }

    public static CommandPermission of(ModuleLifecycle lifecycle) {
        return PredicatePermission.of(SimpleCloudKey.of(lifecycle.moduleInfo().name() + "-lifecycle"), ignored -> lifecycle.getState().isRunning());
    }
}
