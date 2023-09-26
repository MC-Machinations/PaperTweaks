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
package me.machinemaker.papertweaks.modules.survival.durabilityping;

import java.util.Collection;
import java.util.Set;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

@ModuleInfo(name = "DurabilityPing", configPath = "survival.durability-ping", description = "Notifies players when their tools are close to breaking")
public class DurabilityPing extends ModuleBase {

    static final Sound SOUND = Sound.sound(Key.key("block.anvil.land"), Sound.Source.MASTER, 2, 1);

    @Override
    protected void configure() {
        this.bind(Settings.class).asEagerSingleton();
        super.configure();
    }

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(PlayerListener.class);
    }

    @Override
    protected Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }

    @Override
    protected Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }
}
