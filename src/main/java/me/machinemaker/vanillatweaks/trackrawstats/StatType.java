package me.machinemaker.vanillatweaks.trackrawstats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.machinemaker.vanillatweaks.trackrawstats.stats.Custom;
import me.machinemaker.vanillatweaks.trackrawstats.stats.IStat;
import me.machinemaker.vanillatweaks.trackrawstats.stats.Misc;
import me.machinemaker.vanillatweaks.trackrawstats.stats.Simple;
import me.machinemaker.vanillatweaks.trackrawstats.stats.entities.KillEntity;
import me.machinemaker.vanillatweaks.trackrawstats.stats.entities.KilledByEntity;
import org.bukkit.scoreboard.Objective;

import java.util.Map;
import java.util.Set;

public enum StatType {
    SIMPLE(Sets.newHashSet(Simple.values())),
    CUSTOM(Sets.newHashSet(Custom.values())),
    //    BLOCKS_MINED(BlockMined.stats()),
    KILL_ENTITY(Sets.newHashSet(KillEntity.values())),
    KILLED_BY_ENTITY(Sets.newHashSet(KilledByEntity.values())),
    MISC(Sets.newHashSet(Misc.values()));

    final Set<? extends IStat> stats;
    final Map<String, Objective> objectiveMap = Maps.newHashMap();

    StatType(Set<? extends IStat> stats) {
        this.stats = stats;
    }

    public void addToMap(IStat stat, Objective objective) {
        objectiveMap.put(stat.getCommandName(), objective);
    }
}
