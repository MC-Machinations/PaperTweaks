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

import cloud.commandframework.Command;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class ConfiguredModuleCommand extends ModuleCommand {

    private final String i18nName;
    private final String permName;

    protected ConfiguredModuleCommand(@NonNull String name) {
        this(name, name);
    }

    protected ConfiguredModuleCommand(@NonNull String i18nName, @NonNull String permName) {
        this.i18nName = "modules." + i18nName + ".commands";
        this.permName = "vanillatweaks." + permName;
    }

    protected final <C> Command.@NonNull Builder<C> literal(Command.@NonNull Builder<C> builder, @NonNull String name) {
        return this.literal(builder, this.lifecycle(), this.i18nName, this.permName, name);
    }
}
