package me.machinemaker.vanillatweaks.killemptyboats;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;

public class KillEmptyBoats extends BaseModule {

    private Commands commands;

    public KillEmptyBoats(VanillaTweaks plugin) {
        super(plugin, config -> config.killEmptyBoats);
    }

    @Override
    public void register() {
        this.commands = new Commands(this);
        this.registerCommands(commands);
    }

    @Override
    public void unregister() {
        this.unregisterCommands(commands);
    }
}
