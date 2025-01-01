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
package me.machinemaker.papertweaks.modules.hermitcraft.wanderingtrades;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.utils.PTUtils;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.MerchantRecipe;

class EntityListener implements ModuleListener {

    private final Config config;
    private final WanderingTrades wanderingTrades;

    @Inject
    EntityListener(final Config config, final WanderingTrades wanderingTrades) {
        this.config = config;
        this.wanderingTrades = wanderingTrades;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof final WanderingTrader trader)) return;
        if (this.config.headMin > this.config.headMax || this.config.blockMin > this.config.blockMax) {
            WanderingTrades.LOGGER.warn("You have configured the minimum number of trades to be higher than the max, no trades will be added.");
            return;
        }
        final int headTrades = this.config.hermitHeadTradesEnabled ? ThreadLocalRandom.current().nextInt(this.config.headMin, this.config.headMax + 1) : 0;
        final int blockTrades = this.config.blockTradesEnabled ? ThreadLocalRandom.current().nextInt(this.config.blockMin, this.config.blockMax + 1) : 0;
        final List<MerchantRecipe> recipes = new ArrayList<>(trader.getRecipes());
        for (int i = 0; i < blockTrades; i++) {
            recipes.addFirst(PTUtils.random(this.wanderingTrades.blockTrades).createTrade());
        }
        for (int i = 0; i < headTrades; i++) {
            recipes.addFirst(PTUtils.random(this.wanderingTrades.hermitTrades).createTrade());
        }
        trader.setRecipes(recipes);
    }
}
