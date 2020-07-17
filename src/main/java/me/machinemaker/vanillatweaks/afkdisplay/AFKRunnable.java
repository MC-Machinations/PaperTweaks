package me.machinemaker.vanillatweaks.afkdisplay;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

class AFKRunnable extends BukkitRunnable {

    private final Map<UUID, LocationTime> locationMap = Maps.newConcurrentMap();
    private final AFKDisplay module;

    public AFKRunnable(AFKDisplay module) {
        this.module = module;
        Bukkit.getOnlinePlayers().forEach(this::addPlayer);
    }

    public void addPlayer(Player player) {
        locationMap.put(player.getUniqueId(), new LocationTime(System.currentTimeMillis(), player.getLocation()));
    }

    public boolean hasPlayer(UUID uuid) {
        return locationMap.containsKey(uuid);
    }


    @Override
    public void run() {
        Iterator<Entry<UUID, LocationTime>> iterator = locationMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<UUID, LocationTime> entry = iterator.next();
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            if (!player.isOnline() || !player.hasPlayedBefore() || player.getPlayer() == null) iterator.remove();
            else if (!player.getPlayer().hasPermission("vanillatweaks.afkdisplay")) iterator.remove();
            else if (notEqual(entry.getValue().loc, player.getPlayer().getLocation())) {
                entry.getValue().loc = player.getPlayer().getLocation();
                entry.getValue().time = System.currentTimeMillis();
            } else if (entry.getValue().time < System.currentTimeMillis() - (1000L * 60 * 5)) {
                player.getPlayer().setDisplayName(ChatColor.GRAY + player.getPlayer().getDisplayName() + ChatColor.RESET);
                player.getPlayer().setPlayerListName(ChatColor.GRAY + player.getPlayer().getDisplayName() + ChatColor.RESET);
                player.getPlayer().getPersistentDataContainer().set(module.afkKey, PersistentDataType.BYTE, (byte) 1);
                locationMap.remove(entry.getKey());
            }
        }
    }

    private static class LocationTime {
        public Long time;
        public Location loc;

        private LocationTime(Long time, Location loc) {
            this.time = time;
            this.loc = loc;
        }
    }

    private boolean notEqual(Location loc1, Location loc2) {
        return loc1.getBlockX() != loc2.getBlockX()
                || loc1.getBlockY() != loc2.getBlockY()
                || loc1.getBlockZ() != loc2.getBlockZ();
    }
}
