package me.machinemaker.vanillatweaks.wrenches;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "wanderingtrades/config", fileName = "config.yml", format = ConfigFormat.YAML)
class Config extends BaseConfig {

    @Path("suggest-resource-pack")
    public Boolean suggestResourcePack = true;
}
