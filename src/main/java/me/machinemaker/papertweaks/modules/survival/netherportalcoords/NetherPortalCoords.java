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
package me.machinemaker.papertweaks.modules.survival.netherportalcoords;

import me.machinemaker.papertweaks.LoggerFactory;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.moonshine.module.MoonshineModuleBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.moonshine.MoonshineBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Set;

@ModuleInfo(name = "NetherPortalCoords", configPath = "survival.nether-portal-coords", description = "Helper for determining portal locations in other dimensions")
public class NetherPortalCoords extends MoonshineModuleBase<MessageService> {

    static final Logger LOGGER = LoggerFactory.getModuleLogger(NetherPortalCoords.class);

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.Empty.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }

    @Override
    public @Nullable Class<MessageService> messageService() {
        return MessageService.class;
    }

    @Override
    public void placeholderStrategies(MoonshineBuilder.@NotNull Resolved<MessageService, Audience, String, Component, Component> resolved) {
        super.placeholderStrategies(resolved);
        resolved.weightedPlaceholderResolver(MessageService.CoordinatesComponent.class, new MessageService.CoordinatesComponentPlaceholderResolver(), 0);
    }
}
