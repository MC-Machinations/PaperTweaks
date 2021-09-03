package me.machinemaker.vanillatweaks.modules.experimental.elevators;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;

@LecternConfiguration
class Config extends ModuleConfig {

    @Key("max-search-distance")
    @Description("Max search distance in the positive or negative Y to look for other elevators")
    public int maxVerticalSearch = 15;
}
