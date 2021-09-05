package me.machinemaker.vanillatweaks.spawningspheres;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.spawningspheres.Commands.Color;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SpawningSpheres extends BaseModule {

    private Commands commands;

    public SpawningSpheres(VanillaTweaks plugin) {
        super(plugin, config -> config.spawningSpheres);
        plugin.commandManager.getCommandCompletions().registerStaticCompletion("ss/colors", Arrays.stream(Color.values()).map(Color::name).collect(Collectors.toSet()));
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
