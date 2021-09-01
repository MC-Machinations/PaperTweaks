package me.machinemaker.vanillatweaks.modules.mobs.silencemobs;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

class EntityInteractionListener implements ModuleListener {

    private final Plugin plugin;

    @Inject
    EntityInteractionListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!event.getPlayer().hasPermission("vanillatweaks.silencemobs")) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.NAME_TAG) {
            if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
                String name = item.getItemMeta().getDisplayName();
                boolean toSilent = name.equalsIgnoreCase("silence me") || name.equalsIgnoreCase("silence_me");
                if (toSilent) {
                    event.getRightClicked().setSilent(true);
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> event.getRightClicked().setCustomName("silenced"), 5L);
                }
            }
        }
    }

}
