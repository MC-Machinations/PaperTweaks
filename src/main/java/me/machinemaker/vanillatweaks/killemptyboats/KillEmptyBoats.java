package me.machinemaker.vanillatweaks.killemptyboats;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;

public class KillEmptyBoats extends BaseModule {

    private final Commands commands = new Commands(this);

    public KillEmptyBoats(VanillaTweaks plugin) {
        super(plugin, config -> config.killEmptyBoats);
    }

    @Override
    public void register() {
        this.registerCommands(commands);
    }

    @Override
    public void unregister() {
        this.unregisterCommands(commands);
    }
}
