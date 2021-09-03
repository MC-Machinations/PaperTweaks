package me.machinemaker.vanillatweaks.modules.experimental.elevators;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

class ItemListener implements ModuleListener {

    private final JavaPlugin plugin;

    @Inject
    ItemListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().hasPermission("vanillatweaks.elevators.create") && event.getItemDrop().getItemStack().getType() == Material.ENDER_PEARL) {
            new ElevatorItemFinder(event.getItemDrop()).runTaskTimer(this.plugin, 1L, 1L);
        }
    }
}
