package me.machinemaker.vanillatweaks.wanderingtrades;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.MerchantRecipe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WanderingTrades extends BaseModule implements Listener {

    Config config = new Config();

    public WanderingTrades(VanillaTweaks plugin) {
        super(plugin, config -> config.wanderingTrades);
        config.init(plugin, new File(plugin.getDataFolder(), "wanderingtrades"));
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.WANDERING_TRADER) return;
        WanderingTrader trader = (WanderingTrader) event.getEntity();
        int headTrades = config.hermitHeadTradesEnabled ?  ThreadLocalRandom.current().nextInt(config.headMin, config.headMax + 1) : 0;
        int blockTrades = config.blockTradesEnabled ? ThreadLocalRandom.current().nextInt(config.blockMin, config.blockMax + 1) : 0;
        List<MerchantRecipe> recipes = new ArrayList<>(trader.getRecipes());
        for (int i = 0; i < headTrades; i++) {
            recipes.add(VTUtils.random(HermitHead.hermitHeads).getTrade());
        }
        for (int i = 0; i < blockTrades; i++) {
            recipes.add(VTUtils.random(BlockHead.blockHeads).getTrade());
        }
        trader.setRecipes(recipes);
    }

    @Override
    public void register() {
        this.registerEvents(this);
    }

    @Override
    public void unregister() {
        this.unregisterEvents(this);
    }

    @Override
    public void reload() {
        config.reload();
    }
}
