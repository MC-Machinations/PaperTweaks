package me.machinemaker.vanillatweaks.tag;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "tag/config", fileName = "config.yml", format = ConfigFormat.YAML)
class Config extends BaseConfig {

    @Path("show-messages")
    public Boolean showMessages = true;

    @Path("play-sound")
    public Boolean playSound = true;

    @Path("tag-cooldown-in-seconds")
    public Integer timeBetweenTags = 0;
}
