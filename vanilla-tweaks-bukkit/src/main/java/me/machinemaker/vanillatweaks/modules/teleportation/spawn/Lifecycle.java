package me.machinemaker.vanillatweaks.modules.teleportation.spawn;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.modules.ModuleRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

class Lifecycle extends ModuleLifecycle {

    @Inject
    Lifecycle(JavaPlugin plugin, Set<ModuleCommand> commands, Set<ModuleListener> listeners, Set<ModuleConfig> configs, Set<ModuleRecipe<?>> moduleRecipes, Spawn spawn) {
        super(plugin, commands, listeners, configs, moduleRecipes);
    }

    @Override
    public void onDisable() {
        Commands.AWAITING_TELEPORT.forEach((uuid, bukkitTask) -> {
            if (!bukkitTask.isCancelled()) {
                bukkitTask.cancel();
            }
        });
        Commands.AWAITING_TELEPORT.clear();
    }
}
