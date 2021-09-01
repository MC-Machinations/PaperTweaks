package me.machinemaker.vanillatweaks.modules.teleportation.spawn;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;

@LecternConfiguration
class Config extends ModuleConfig {

    @Key("spawn-cooldown")
    @Description("Time in seconds between using /spawn")
    public long cooldown = 0;

    @Key("delay")
    @Description("Delay in seconds after using /spawn before teleportation occurs")
    public long delay = 0;
}
