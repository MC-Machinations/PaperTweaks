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
package me.machinemaker.vanillatweaks.modules.mobs.moremobheads;

import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.machinemaker.vanillatweaks.LoggerFactory;
import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Set;

@ModuleInfo(name = "MoreMobHeads", configPath = "mobs.more-mob-heads", description = "Adds heads for a lot more mobs")
public class MoreMobHeads extends ModuleBase {

    static final Logger LOGGER = LoggerFactory.getModuleLogger(MoreMobHeads.class);

    final Multimap<Class<? extends LivingEntity>, MobHead> heads;

    @Inject
    MoreMobHeads(@Named("plugin") ClassLoader loader) {
        this.heads = MobHead.createMobHeadMap(loader);
    }

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.Empty.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(EntityListener.class);
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }
}
