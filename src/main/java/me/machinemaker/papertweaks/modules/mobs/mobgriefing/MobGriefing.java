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
package me.machinemaker.papertweaks.modules.mobs.mobgriefing;

import com.google.inject.Inject;
import me.machinemaker.papertweaks.LoggerFactory;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.modules.ModuleRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Set;

@ModuleInfo(name = "MobGriefing", configPath = "mobs.mob-griefing", description = "Disable various mobs griefing behaviors")
public class MobGriefing extends ModuleBase {

    static final Logger LOGGER = LoggerFactory.getModuleLogger(MobGriefing.class);

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(MobListener.class);
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }

    static class Lifecycle extends ModuleLifecycle {

        private final Config config;

        @Inject
        Lifecycle(JavaPlugin plugin, Set<ModuleCommand> commands, Set<ModuleListener> listeners, Set<ModuleConfig> configs, Set<ModuleRecipe<?>> moduleRecipes, Config config) {
            super(plugin, commands, listeners, configs, moduleRecipes);
            this.config = config;
        }

        @Override
        public void onEnable() {
            if (!this.config.antiCreeperGrief && !this.config.antiGhastGrief && !this.config.antiEndermanGrief) {
                LOGGER.warn("Enabling this module will have no effect without at least one of the anti-grief settings configured in " + this.config.file());
            }
        }
    }
}
