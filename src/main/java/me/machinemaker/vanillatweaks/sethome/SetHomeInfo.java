package me.machinemaker.vanillatweaks.sethome;

import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SetHomeInfo implements ConfigurationSerializable {

    public Integer timesUsed;
    public Location location;
    public Long cooldown = 0L;

    SetHomeInfo(Integer timesUsed, Location location) {
        this.timesUsed = timesUsed;
        this.location = location;
    }

    public SetHomeInfo(Map<String, Object> map) {
        timesUsed = (Integer) map.get("timesUsed");
        location = (Location) map.get("location");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("timesUsed", this.timesUsed);
        map.put("location", location);
        return map;
    }
}
