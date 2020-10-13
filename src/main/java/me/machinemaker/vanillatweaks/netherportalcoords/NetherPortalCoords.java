package me.machinemaker.vanillatweaks.netherportalcoords;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;

import java.io.File;

public class NetherPortalCoords extends BaseModule {

    final Config config = new Config();
    private Commands commands;

    public NetherPortalCoords(VanillaTweaks plugin) {
        super(plugin, config -> config.netherPortalCoords);
        config.init(plugin, new File(plugin.getDataFolder(), "netherportalcoords"));
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

    @Override
    public void reload() {
        config.reload();
    }
}
