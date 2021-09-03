package me.machinemaker.vanillatweaks.modules.experimental.elevators;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.modules.ModuleRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

class Lifecycle extends ModuleLifecycle {

    @Inject
    Lifecycle(JavaPlugin plugin, Set<ModuleCommand> commands, Set<ModuleListener> listeners, Set<ModuleConfig> configs, Set<ModuleRecipe<?>> moduleRecipes) {
        super(plugin, commands, listeners, configs, moduleRecipes);
    }

    private @Nullable BukkitTask particlesTask;

    @Override
    public void onEnable() {
        this.particlesTask = new PortalParticles().runTaskTimer(this.getPlugin(), 1L, 10L);
    }

    @Override
    public void onDisable() {
        if (this.particlesTask != null && !this.particlesTask.isCancelled()) {
            this.particlesTask.cancel();
        }
    }
}