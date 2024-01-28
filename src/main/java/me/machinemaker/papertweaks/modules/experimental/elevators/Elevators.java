/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.modules.experimental.elevators;

import java.util.Collection;
import java.util.Set;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.pdc.PDCKey;
import me.machinemaker.papertweaks.utils.Keys;

@ModuleInfo(name = "Elevators", configPath = "experimental.elevators", description = "Create vertical elevators on wool blocks by throwing an enderpearl on the wool block")
public class Elevators extends ModuleBase {

    static final PDCKey<Boolean> IS_ELEVATOR = PDCKey.bool(Keys.legacyKey("is_elevator"));

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(PlayerListener.class, ItemListener.class);
    }

    @Override
    protected Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }
}
