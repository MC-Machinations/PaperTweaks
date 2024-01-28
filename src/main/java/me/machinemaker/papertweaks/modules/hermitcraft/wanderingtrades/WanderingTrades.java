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
package me.machinemaker.papertweaks.modules.hermitcraft.wanderingtrades;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import me.machinemaker.papertweaks.LoggerFactory;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.config.Mixins;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import org.slf4j.Logger;

@ModuleInfo(name = "WanderingTrades", configPath = "hermitcraft.wandering-trades", description = "Adds a bunch of new trades to wandering traders for micro-blocks and player heads")
public class WanderingTrades extends ModuleBase {

    static final Logger LOGGER = LoggerFactory.getModuleLogger(WanderingTrades.class);
    static final ObjectMapper JSON_MAPPER = new ObjectMapper().registerModule(new ParameterNamesModule());

    static {
        Mixins.registerMixins(JSON_MAPPER);
    }

    final List<Trade> blockTrades;
    final List<Trade> hermitTrades;

    @Inject
    WanderingTrades(@Named("plugin") final ClassLoader loader) {
        List<Trade> tempTrades;
        try {
            tempTrades = JSON_MAPPER.readValue(loader.getResourceAsStream("data/wandering_trades.json"), new TypeReference<List<Trade>>() {});
        } catch (final IOException e) {
            LOGGER.error("Could not load wandering trades from data/wandering_trades.json. This module will not work properly", e);
            tempTrades = Collections.emptyList();
        }
        this.blockTrades = tempTrades.stream().filter(Trade::isBlockTrade).toList();
        this.hermitTrades = tempTrades.stream().filter(Predicate.not(Trade::isBlockTrade)).toList();
    }

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.Empty.class;
    }

    @Override
    protected Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(EntityListener.class);
    }

    @Override
    protected Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }

    @Override
    protected Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }
}
