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
package me.machinemaker.papertweaks.cloud.parsers;

import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.db.model.teleportation.homes.Home;
import me.machinemaker.papertweaks.modules.ModuleBase;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.parser.ParserDescriptor;

import static org.incendo.cloud.key.CloudKey.cloudKey;
import static org.incendo.cloud.parser.ParserDescriptor.parserDescriptor;

public interface ParserFactory {

    static ParserDescriptor<CommandDispatcher, Home> homeDescriptor(final ParserFactory factory) {
        return parserDescriptor(factory.home(), Home.class);
    }

    static ParserDescriptor<CommandDispatcher, ModuleBase> moduleDescriptor(final ParserFactory factory, final @Nullable Boolean enabled) {
        return parserDescriptor(factory.module(enabled), ModuleBase.class);
    }

    HomeParser home();

    ModuleParser module(@Nullable Boolean enabled);
}
