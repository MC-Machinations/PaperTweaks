package me.machinemaker.vanillatweaks.pillagertools;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "pillagertools/config", fileName = "config.yml", format = ConfigFormat.YAML)
class Config extends BaseConfig {

    @Path("bad-omen")
    public Boolean badOmen = true;

    @Path("patrol-leaders")
    public Boolean patrolLeaders = true;

    @Path("patrols")
    public Boolean patrols = true;
}
