package me.machinemaker.vanillatweaks.coordinateshud;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "coordinateshud/config", fileName = "config.yml", format = ConfigFormat.YAML)
class Config extends BaseConfig {

    @Path("hud-update-time-in-ticks")
    public Long ticks = 2L;
}
