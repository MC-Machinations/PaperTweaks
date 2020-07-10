package me.machinemaker.vanillatweaks.durabilityping;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "durabilityping/config", fileName = "config.yml", format = ConfigFormat.YAML)
public class Config extends BaseConfig {

    @Path("notification-threshold-percent")
    public Double threshold = 0.02d;

    @Path("notification-cooldown-seconds")
    public Integer notificationCooldown = 10;
}
