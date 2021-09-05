package me.machinemaker.vanillatweaks.multiplayersleep;

import com.google.common.collect.Lists;
import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

import java.util.List;

@NewConfig(name = "multiplayersleep/config", fileName = "config.yml", format = ConfigFormat.YAML)
class Config extends BaseConfig {

    @Path("sleep-percentage")
    public Double sleepPercentage = 0.5;

    @Path("included-worlds")
    public List<String> includedWorlds = Lists.newArrayList("world");
}
