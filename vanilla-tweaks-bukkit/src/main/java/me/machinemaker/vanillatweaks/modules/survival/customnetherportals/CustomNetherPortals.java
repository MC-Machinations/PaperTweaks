/*
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
package me.machinemaker.vanillatweaks.modules.survival.customnetherportals;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.modules.ModuleRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

@ModuleInfo(name = "CustomNetherPortals", configPath = "survival.custom-nether-portals", description = "Create non-rectangular nether portals")
public class CustomNetherPortals extends ModuleBase {

    @Override
    protected void configure() {
        super.configure();
        requestStaticInjection(PortalShapeFinder.class);
        requestStaticInjection(Config.class);
        requestStaticInjection(IgniteListener.class);
    }

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.SimpleLifecycle.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(IgniteListener.class);
    }

    static class Lifecycle extends ModuleLifecycle {

        private final Config config;

        @Inject
        protected Lifecycle(JavaPlugin plugin, Set<ModuleCommand> commands, Set<ModuleListener> listeners, Set<ModuleConfig> configs, Config config, Set<ModuleRecipe<?>> moduleRecipes) {
            super(plugin, commands, listeners, configs, moduleRecipes);
            this.config = config;
        }

        @Override
        public void onEnable() {
            this.config.portalFrameMaterials(true);
        }

        @Override
        public void onReload() {
            this.config.portalFrameMaterials(true);
        }
    }
}
