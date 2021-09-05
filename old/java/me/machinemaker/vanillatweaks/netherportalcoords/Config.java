package me.machinemaker.vanillatweaks.netherportalcoords;

import com.google.common.collect.Lists;
import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

import java.util.List;

@NewConfig(name = "netherportalcoords/config", fileName = "config.yml", format = ConfigFormat.YAML)
class Config extends BaseConfig {
    @Path("overworld-type-worlds")
    public List<String> overworlds = Lists.newArrayList("world");

    @Path("nether-type-worlds")
    public List<String> netherWorlds = Lists.newArrayList("world_nether");
}
