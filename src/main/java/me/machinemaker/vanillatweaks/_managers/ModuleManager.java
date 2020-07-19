package me.machinemaker.vanillatweaks._managers;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.afkdisplay.AFKDisplay;
import me.machinemaker.vanillatweaks.coordinateshud.CoordinatesHUD;
import me.machinemaker.vanillatweaks.durabilityping.DurabilityPing;
import me.machinemaker.vanillatweaks.killemptyboats.KillEmptyBoats;
import me.machinemaker.vanillatweaks.largerphantoms.LargerPhantoms;
import me.machinemaker.vanillatweaks.mobcounting.MobCounting;
import me.machinemaker.vanillatweaks.mobdrops.MobDrops;
import me.machinemaker.vanillatweaks.mobgriefing.MobGriefing;
import me.machinemaker.vanillatweaks.mobheads.MobHeads;
import me.machinemaker.vanillatweaks.multiplayersleep.MultiplayerSleep;
import me.machinemaker.vanillatweaks.netherportalcoords.NetherPortalCoords;
import me.machinemaker.vanillatweaks.persistentheads.PersistentHeads;
import me.machinemaker.vanillatweaks.pillagertools.PillagerTools;
import me.machinemaker.vanillatweaks.playergraves.PlayerGraves;
import me.machinemaker.vanillatweaks.playerheaddrops.PlayerHeadDrops;
import me.machinemaker.vanillatweaks.sethome.SetHome;
import me.machinemaker.vanillatweaks.silencemobs.SilenceMobs;
import me.machinemaker.vanillatweaks.spawningspheres.SpawningSpheres;
import me.machinemaker.vanillatweaks.spectatortoggle.SpectatorToggleEffect;
import me.machinemaker.vanillatweaks.tag.Tag;
import me.machinemaker.vanillatweaks.thundershrine.ThunderShrine;
import me.machinemaker.vanillatweaks.trackrawstats.TrackRawStats;
import me.machinemaker.vanillatweaks.villagerdeathmessages.VillagerDeathMessages;
import me.machinemaker.vanillatweaks.wanderingtrades.WanderingTrades;
import me.machinemaker.vanillatweaks.workstationhighlights.WorkstationHighlights;
import me.machinemaker.vanillatweaks.wrenches.Wrench;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ModuleManager {

    private List<BaseModule> allModules;
    public final List<BaseModule> enabled = Lists.newArrayList();
    @Inject
    VanillaTweaks plugin;

    public void load() {
        reload(false);
    }

    public void reload() {
        reload(true);
    }

    private void reload(boolean unregister) {
        this.enabled.clear();
        allModules().forEach(baseModule -> {
            if (unregister && baseModule.registered) {
                baseModule.unregister();
                baseModule.registered = false;
            }
            if (baseModule.shouldEnable()) {
                if (!enabled.contains(baseModule)) enabled.add(baseModule);
                baseModule.register();
                baseModule.registered = true;
            }
        });
        if (unregister) enabled.forEach(BaseModule::reload);
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    public List<BaseModule> allModules() {
        if (this.allModules == null) {
            this.allModules = Arrays.asList(
                    new MobGriefing(plugin),
                    new MobDrops(plugin),
                    new LargerPhantoms(plugin),
                    new MobHeads(plugin),
                    new SilenceMobs(plugin),
                    new MobCounting(plugin),

                    new DurabilityPing(plugin),
                    new Wrench(plugin),
                    new KillEmptyBoats(plugin),

                    new AFKDisplay(plugin),
                    new SetHome(plugin),
                    new MultiplayerSleep(plugin),
                    new PlayerGraves(plugin),
                    new CoordinatesHUD(plugin),
                    new NetherPortalCoords(plugin),
                    new SpectatorToggleEffect(plugin),
                    new ThunderShrine(plugin),
                    new SpawningSpheres(plugin),
                    new PlayerHeadDrops(plugin),
                    new TrackRawStats(plugin),
                    new Tag(plugin),
                    new PersistentHeads(plugin),

                    new VillagerDeathMessages(plugin),
                    new PillagerTools(plugin),
                    new WorkstationHighlights(plugin),
                    new WanderingTrades(plugin)
            );
        }
        return this.allModules;
    }
}
