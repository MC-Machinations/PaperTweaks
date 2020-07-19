package me.machinemaker.vanillatweaks.workstationhighlights;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;

public class WorkstationHighlights extends BaseModule {

    private Commands commands;

    public WorkstationHighlights(VanillaTweaks plugin) {
        super(plugin, config -> config.workstationHighlights);
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
