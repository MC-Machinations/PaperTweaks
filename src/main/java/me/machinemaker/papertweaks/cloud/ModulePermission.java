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
package me.machinemaker.papertweaks.cloud;

import java.util.Set;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.permission.PredicatePermission;

import static org.incendo.cloud.key.CloudKey.cloudKey;

public final class ModulePermission {

    private ModulePermission() {
    }

    public static Permission of(final ModuleLifecycle lifecycle, final String permission) {
        return Permission.allOf(Set.of(
            Permission.of(permission),
            of(lifecycle)
        ));
    }

    public static Permission of(final ModuleLifecycle lifecycle) {
        return PredicatePermission.of(cloudKey(lifecycle.moduleInfo().name() + "-lifecycle"), ignored -> lifecycle.getState().isRunning());
    }
}
