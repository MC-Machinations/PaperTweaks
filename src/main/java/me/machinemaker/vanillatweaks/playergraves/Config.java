package me.machinemaker.vanillatweaks.playergraves;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

import java.util.Arrays;
import java.util.List;

@NewConfig(name = "playergraves/config", fileName = "config.yml", format = ConfigFormat.YAML)
class Config extends BaseConfig {

    @Path("disabled-worlds")
    List<String> disabledWorlds = Arrays.asList("disabled_world_name");
}
