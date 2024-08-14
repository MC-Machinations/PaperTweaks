package me.machinemaker.papertweaks.modules.mobs.moremobheads;

import jakarta.inject.Inject;
import java.util.Set;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.modules.ModuleRecipe;
import org.bukkit.plugin.java.JavaPlugin;

class Lifecycle extends ModuleLifecycle {

    @Inject
    protected Lifecycle(final JavaPlugin plugin, final Set<ModuleCommand> commands, final Set<ModuleListener> listeners, final Set<ModuleConfig> configs, final Set<ModuleRecipe<?>> moduleRecipes) {
        super(plugin, commands, listeners, configs, moduleRecipes);
    }

    @Override
    public void onEnable() {
        MoreMobHeads.LOGGER.error("Please disable MoreMobHeads. MoreMobHeads is deprecated and will be removed, you can use the actual VanillaTweaks datapack without any performance hit and for improved functionality. It may be removed in future versions.");
        super.onEnable();
    }
}
