package me.machinemaker.vanillatweaks.mobheads;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "mobheads/config", fileName = "config.yml", format = ConfigFormat.YAML)
class Config extends BaseConfig {

    @Path("require-player-kill")
    public Boolean requirePlayerKill = false;
}
