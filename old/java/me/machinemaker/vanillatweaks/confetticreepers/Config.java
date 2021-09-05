package me.machinemaker.vanillatweaks.confetticreepers;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "confetticreepers/config", fileName = "config.yml", format = ConfigFormat.YAML)
class Config extends BaseConfig {

    @Path("chance")
    public Double chance = 1D;
}
