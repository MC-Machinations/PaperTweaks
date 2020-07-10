package me.machinemaker.vanillatweaks.playerheaddrops;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "playerheaddrops/config", fileName = "config.yml", format = ConfigFormat.YAML)
class Config extends BaseConfig {
    @Path("head-drop-chance")
    public Double dropChance = 1d;
}
