package me.machinemaker.vanillatweaks.playerheaddrops;

import com.google.common.collect.Lists;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerHeadDrops extends BaseModule implements Listener {

    private final Config config = new Config();

    public PlayerHeadDrops(VanillaTweaks plugin) {
        super(plugin, config -> config.playerHeadDrops);
        config.init(plugin, new File(plugin.getDataFolder(), "playerheaddrops"));
    }

    @EventHandler
    public void onPlayerKilledByPlayer(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        if (ThreadLocalRandom.current().nextDouble() < config.dropChance) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(event.getEntity());
            meta.setLore(Lists.newArrayList(ChatColor.RESET + ChatColor.WHITE.toString() + "Killed by " + ChatColor.YELLOW + event.getEntity().getKiller().getDisplayName()));
            skull.setItemMeta(meta);
            event.getDrops().add(skull);
        }
    }

    @Override
    public void register() {
        this.registerEvents(this);
    }

    @Override
    public void unregister() {
        this.unregisterEvents(this);
    }
}
