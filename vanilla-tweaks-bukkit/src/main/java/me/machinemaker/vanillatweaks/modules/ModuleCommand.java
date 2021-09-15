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

import cloud.commandframework.permission.CommandPermission;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.VanillaTweaksCommand;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class ModuleCommand extends VanillaTweaksCommand {

    private @MonotonicNonNull ModuleLifecycle lifecycle;
    private boolean registered;

    final void registerCommands0(ModuleLifecycle lifecycle) {
        this.lifecycle = lifecycle;
        this.registerCommands();
        this.registered = true;
    }

    protected abstract void registerCommands();

    protected final @NonNull ModuleLifecycle lifecycle() {
        if (this.lifecycle == null) {
            throw new IllegalStateException("lifecycle hasn't been set on this command yet!");
        }
        return this.lifecycle;
    }

    protected final @NonNull CommandPermission modulePermission(@NonNull String permission) {
        return ModulePermission.of(lifecycle(), permission);
    }

    boolean isRegistered() {
        return this.registered;
    }
}
