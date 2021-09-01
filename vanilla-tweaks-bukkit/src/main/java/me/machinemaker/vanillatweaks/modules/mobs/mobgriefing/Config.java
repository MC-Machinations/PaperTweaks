package me.machinemaker.vanillatweaks.modules.mobs.mobgriefing;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;

@LecternConfiguration
class Config extends ModuleConfig {

    @Key("anti-enderman-grief")
    @Description("Prevents enderman from picking up blocks")
    public boolean antiEndermanGrief = false;

    @Key("anti-ghast-grief")
    public boolean antiGhastGrief = false;

    @Key("anti-creeper-grief")
    public boolean antiCreeperGrief = false;

}
