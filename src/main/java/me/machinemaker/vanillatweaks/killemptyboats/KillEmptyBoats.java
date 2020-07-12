package me.machinemaker.vanillatweaks.killemptyboats;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;

public class KillEmptyBoats extends BaseModule {

    public KillEmptyBoats(VanillaTweaks plugin) {
        super(plugin, config -> config.killEmptyBoats);
        this.registerCommands(new Commands(this));
    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() {

    }
}
