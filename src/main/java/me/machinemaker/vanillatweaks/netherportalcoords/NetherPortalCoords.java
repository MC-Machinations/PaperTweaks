package me.machinemaker.vanillatweaks.netherportalcoords;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;

import java.io.File;

public class NetherPortalCoords extends BaseModule {

    final Config config = new Config();

    public NetherPortalCoords(VanillaTweaks plugin) {
        super(plugin, config -> config.netherPortalCoords);
        config.init(plugin, new File(plugin.getDataFolder(), "netherportalcoords"));
        this.registerCommands(new Commands(this));
    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() {

    }

    @Override
    public void reload() {
        config.reload();
    }
}
