package me.machinemaker.vanillatweaks.tag;

import com.google.common.collect.Maps;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

class TagRunnable extends BukkitRunnable {

    private final Map<Player, Item> playerItemMap = Maps.newHashMap();
    private final Tag module;
    private int count = 1;

    public TagRunnable(Tag module) {
        this.module = module;
    }

    public void addPlayerItem(Player player, Item item) {
        playerItemMap.put(player, item);
    }

    @Override
    public void run() {
        for (Iterator<Entry<Player, Item>> iterator = playerItemMap.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<Player, Item> entry = iterator.next();
            if (entry.getValue().isDead()) {
                iterator.remove();
                continue;
            }
            entry.getValue().teleport(entry.getKey());
            if (count % 4 == 0) {
                count = 0;
                if (this.module.config.playSound) entry.getKey().playSound(entry.getKey().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 0.5f, 0.5f);
            }
        }
        count++;
    }
}
