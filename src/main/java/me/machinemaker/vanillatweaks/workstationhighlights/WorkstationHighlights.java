package me.machinemaker.vanillatweaks.workstationhighlights;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;

public class WorkstationHighlights extends BaseModule {

    public WorkstationHighlights(VanillaTweaks plugin) {
        super(plugin, config -> config.workstationHighlights);
        this.registerCommands(new Commands(this));
    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() {

    }
}
