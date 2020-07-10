package me.machinemaker.vanillatweaks.spawningspheres;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.spawningspheres.Commands.Color;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SpawningSpheres extends BaseModule {

    public SpawningSpheres(VanillaTweaks plugin) {
        super(plugin, config -> config.spawningSpheres);
        plugin.commandManager.getCommandCompletions().registerStaticCompletion("ss/colors", Arrays.stream(Color.values()).map(Color::name).collect(Collectors.toSet()));
        this.registerCommands(new Commands(this));
    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() {

    }
}
