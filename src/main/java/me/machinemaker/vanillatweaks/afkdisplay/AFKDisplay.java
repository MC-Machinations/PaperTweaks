package me.machinemaker.vanillatweaks.afkdisplay;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;

public class AFKDisplay extends BaseModule implements Listener {

    AFKRunnable runnable;
    final NamespacedKey afkKey = new NamespacedKey(this.plugin, "afk");

    public AFKDisplay(VanillaTweaks vanillaTweaks) {
        super(vanillaTweaks, config -> config.afkDisplay);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getPersistentDataContainer().has(afkKey, PersistentDataType.BYTE)) {
            event.getPlayer().setDisplayName(event.getPlayer().getName());
            event.getPlayer().setPlayerListName(event.getPlayer().getName());
            event.getPlayer().getPersistentDataContainer().remove(afkKey);
            runnable.addPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        runnable.addPlayer(event.getPlayer());
    }

    @Override
    public void register() {
        this.registerEvents(this);
        runnable = new AFKRunnable(this);
        runnable.runTaskTimerAsynchronously(this.plugin, 1L, 20L);
    }

    @Override
    public void unregister() {
        runnable.cancel();
    }
}
