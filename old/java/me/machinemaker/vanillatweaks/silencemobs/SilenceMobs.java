package me.machinemaker.vanillatweaks.silencemobs;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class SilenceMobs extends BaseModule implements Listener {

    public SilenceMobs(VanillaTweaks vanillaTweaks) {
        super(vanillaTweaks, config -> config.silenceMobs);
    }

    @EventHandler
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
                event.getRightClicked().setSilent(name.equalsIgnoreCase("silence me") || name.equalsIgnoreCase("silence_me"));
            }
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
