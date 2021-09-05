package me.machinemaker.vanillatweaks.sethome;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "sethome/config", fileName = "config.yml", format = ConfigFormat.YAML)
class Config extends BaseConfig {
    @Path("default-sethome-limit")
    Integer defaultSetHomeLimit = 5;

    @Path("allow-across-dimension")
    Boolean allowAcrossDimension = true;

    @Path("home-command-cooldown-seconds")
    Long sethomeCooldown = 0L;
}
