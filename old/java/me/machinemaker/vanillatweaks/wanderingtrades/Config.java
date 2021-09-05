package me.machinemaker.vanillatweaks.wanderingtrades;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "wanderingtrades/config", fileName = "config.yml", format = ConfigFormat.YAML)
public class Config extends BaseConfig {

    @Path("block-trades.enabled")
    public Boolean blockTradesEnabled = true;

    @Path("block-trades.min")
    public Integer blockMin = 5;

    @Path("block-trades.max")
    public Integer blockMax = 7;

    @Path("hermit-head-trades.enabled")
    public Boolean hermitHeadTradesEnabled = true;

    @Path("hermit-head-trades.min")
    public Integer headMin = 1;

    @Path("hermit-head-trades.max")
    public Integer headMax = 3;
}
