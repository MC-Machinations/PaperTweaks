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
package me.machinemaker.vanillatweaks.modules.hermitcraft.thundershrine;

import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.pdc.PDCKey;
import me.machinemaker.vanillatweaks.utils.Keys;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@ModuleInfo(name = "ThunderShrine", configPath = "hermitcraft.thunder-shrine", description = "Create a shrine to summon thunderstorms")
public class ThunderShrine extends ModuleBase {

    static final PDCKey<UUID> SHRINE = PDCKey.uuid(Keys.key("shrine"));

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }
}
