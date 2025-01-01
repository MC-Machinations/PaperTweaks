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
package me.machinemaker.papertweaks.modules.hermitcraft.treasuregems;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.machinemaker.papertweaks.LoggerFactory;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.config.Mixins;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.utils.PTUtils;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.slf4j.Logger;

@ModuleInfo(name = "TreasureGems", configPath = "hermitcraft.treasure-gems", description = "Adds several gems to various loot tables")
public class TreasureGems extends ModuleBase {

    static final Logger LOGGER = LoggerFactory.getModuleLogger(TreasureGems.class);
    static final ObjectMapper JSON_MAPPER = Mixins.registerMixins(new ObjectMapper().registerModule(new ParameterNamesModule()));

    final Set<LootTables> tables;
    final Map<String, ItemStack> heads;

    @Inject
    TreasureGems(@Named("plugin") final ClassLoader loader) {
        Set<LootTables> tempTables;
        final Map<String, ItemStack> tempHeads = new HashMap<>();
        this.heads = Collections.unmodifiableMap(tempHeads);
        try {
            tempTables = JSON_MAPPER.readValue(loader.getResourceAsStream("data/treasure_gems/loot_tables.json"), new TypeReference<Set<LootTables>>() {
            });
            final ObjectNode heads = JSON_MAPPER.readValue(loader.getResourceAsStream("data/treasure_gems/heads.json"), ObjectNode.class);
            final Iterator<Map.Entry<String, JsonNode>> iter = heads.fields();
            while (iter.hasNext()) {
                final Map.Entry<String, JsonNode> entry = iter.next();
                tempHeads.put(entry.getKey(), PTUtils.getSkull(GsonComponentSerializer.gson().deserialize(entry.getValue().get("name").asText()), UUID.fromString(entry.getValue().get("uuid").asText()), entry.getValue().get("texture").asText(), 1));
            }

        } catch (final Exception ex) {
            tempTables = Collections.emptySet();
            LOGGER.error("Could not load treasure gems from data/treasure_gems/. This module will not work properly", ex);
        }

        this.tables = tempTables;
    }

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(LootListener.class);
    }

    @Override
    protected Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }
}
