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
package me.machinemaker.papertweaks.modules.hermitcraft.gemvillagers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import me.machinemaker.papertweaks.LoggerFactory;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.config.Mixins;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import org.slf4j.Logger;

@ModuleInfo(name = "GemVillagers", configPath = "hermitcraft.gem-villagers", description = "Create villager shops to trade gems from TreasureGems")
public class GemVillagers extends ModuleBase {

    static final Logger LOGGER = LoggerFactory.getModuleLogger(GemVillagers.class);
    static final ObjectMapper JSON_MAPPER = Mixins.registerMixins(new ObjectMapper().registerModule(new ParameterNamesModule()));

    final Map<String, VillagerData> villagers;

    @Inject
    GemVillagers(@Named("plugin") final ClassLoader loader) {
        Map<String, VillagerData> tempVillagers;
        try {
            tempVillagers = JSON_MAPPER.readValue(loader.getResourceAsStream("data/gem_villagers.json"), new TypeReference<Map<String, VillagerData>>() {});
        } catch (final Exception e) {
            tempVillagers = Collections.emptyMap();
            LOGGER.error("Could not load gem villagers from data/gem_villagers.json. This module will not work properly", e);
        }
        this.villagers = tempVillagers;
    }

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }
}
