package me.machinemaker.vanillatweaks;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "config", fileName = "config.yml", format = ConfigFormat.YAML)
public class VanillaTweaksConfig extends BaseConfig {
    @Path("enable-bstats")
    public Boolean enabled = true;
}
