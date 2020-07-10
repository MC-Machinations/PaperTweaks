package me.machinemaker.vanillatweaks.thundershrine;

import me.machinemaker.vanillatweaks.Lang;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

class ShrineRunnable extends BukkitRunnable {

    private final ThunderShrine module;

    public ShrineRunnable(ThunderShrine module) {
        this.module = module;
    }

    @Override
    public void run() {
        module.shrineLocations.forEach(entity -> entity.getNearbyEntities(0.5, 0.5, 0.5).forEach(nearby -> {
            if (nearby.getType() != EntityType.DROPPED_ITEM) return;
            Item item = (Item) nearby;
            ItemStack stack = item.getItemStack();
            if (stack.getType() != Material.NETHER_STAR || stack.getAmount() != 1) return;
            item.remove();
            entity.getWorld().getPlayers().forEach(player -> {
                player.getWorld().setWeatherDuration(6000);
                player.getWorld().setThunderDuration(6000);
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(true);
                System.out.println(player.getWorld().getThunderDuration());
                player.sendMessage(Lang.STORM_STARTED.p());
            });
        }));
    }
}
