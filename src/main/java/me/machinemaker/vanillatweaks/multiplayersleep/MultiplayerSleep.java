package me.machinemaker.vanillatweaks.multiplayersleep;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MultiplayerSleep extends BaseModule implements Listener {

    final Config config = new Config();
    private final List<World> worlds;
    private final Map<UUID, List<Player>> sleepMap = Maps.newHashMap();

    public MultiplayerSleep(VanillaTweaks plugin) {
        super(plugin, config -> config.multiplayerSleep);
        config.init(plugin, new File(plugin.getDataFolder(), "multiplayersleep"));
        worlds = Lists.newArrayList();
        verifyWorlds();
    }

    private void verifyWorlds() {
        worlds.clear();
        config.includedWorlds.forEach(worldKey -> {
            if (Bukkit.getWorld(worldKey) == null) {
                plugin.getLogger().severe(worldKey + " is not a valid world!");
            } else worlds.add(Bukkit.getWorld(worldKey));
        });
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != BedEnterResult.OK) return;
        List<Player> currentWorldSleeping = sleepMap.computeIfAbsent(event.getPlayer().getWorld().getUID(), uuid -> Lists.newArrayList());
        if (currentWorldSleeping.contains(event.getPlayer())) return;
        currentWorldSleeping.add(event.getPlayer());
        worlds.forEach(world -> {
            world.getPlayers().forEach(player -> player.sendMessage(ChatColor.YELLOW + event.getPlayer().getDisplayName() + " is sleeping (" + currentWorldSleeping.size() + "/" + world.getPlayers().size() + ")."));

            if ((double) currentWorldSleeping.size() / (double) world.getPlayers().size() >= config.sleepPercentage) {
                event.getPlayer().getWorld().setTime(0);
                event.getPlayer().getWorld().setThundering(false);
                event.getPlayer().getWorld().setWeatherDuration(0);
                event.getPlayer().getWorld().setStorm(false);
            }
        });
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        sleepMap.get(event.getPlayer().getWorld().getUID()).remove(event.getPlayer());
    }

    @Override
    public void register() {
        this.registerEvents(this);
    }

    @Override
    public void unregister() {
        this.unregisterEvents(this);
    }

    @Override
    public void reload() {
        config.reload();
        verifyWorlds();
    }
}
