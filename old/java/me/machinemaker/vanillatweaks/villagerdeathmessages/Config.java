package me.machinemaker.vanillatweaks.villagerdeathmessages;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "villagerdeathmessages/config", fileName = "config.yml", format = ConfigFormat.YAML)
class Config extends BaseConfig {

    @Path("show-message-on-death")
    public Boolean showMessageOnDeath = true;

    @Path("show-message-on-conversion")
    public Boolean showMessageOnConversion = true;
}
