package me.machinemaker.vanillatweaks.unlockallrecipes;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.utils.Cacheable;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UnlockAllRecipes extends BaseModule implements Listener {

    private final Cacheable<Collection<NamespacedKey>> recipes;

    public UnlockAllRecipes(VanillaTweaks plugin) {
        super(plugin, config -> config.unlockAllRecipes);
        this.recipes = new Cacheable<>(() -> {
            Iterable<Recipe> iterable = Bukkit::recipeIterator;
            return StreamSupport.stream(iterable.spliterator(), false).filter(Keyed.class::isInstance).map(Keyed.class::cast).map(Keyed::getKey).collect(Collectors.toSet());
        }, 1000 * 60 * 5); // 5 minutes

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        discoverAllRecipes(event.getPlayer());
    }

    public void discoverAllRecipes(HumanEntity humanEntity) {
        humanEntity.discoverRecipes(recipes.get());
    }

    @Override
    public void register() {
        registerEvents(this);
        Bukkit.getOnlinePlayers().forEach(this::discoverAllRecipes);
    }

    @Override
    public void unregister() {
        unregisterEvents(this);
    }
}
