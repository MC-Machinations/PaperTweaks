package me.machinemaker.papertweaks.modules.hermitcraft.treasuregems;

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
        TreasureGems.LOGGER.error("Please disable TreasureGems. TreasureGems is deprecated and won't be updated or supported in 1.21+. It may be removed in future versions.");
        super.onEnable();
    }
}
