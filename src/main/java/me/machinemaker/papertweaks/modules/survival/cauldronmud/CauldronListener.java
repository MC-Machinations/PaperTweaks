package me.machinemaker.papertweaks.modules.survival.cauldronmud;

import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import java.util.Set;
import me.machinemaker.papertweaks.modules.ModuleListener;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

class CauldronListener implements ModuleListener {

    private static final Set<Material> VALID_DIRT_TYPES = Set.of(
        Material.DIRT,
        Material.COARSE_DIRT,
        Material.ROOTED_DIRT
    );

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityInsideBlock(final EntityInsideBlockEvent event) {
        if (event.getEntity() instanceof final Item item
            && event.getBlock().getType() == Material.WATER_CAULDRON
            && VALID_DIRT_TYPES.contains(item.getItemStack().getType())) {
            item.getWorld().dropItem(
                item.getLocation(),
                new ItemStack(CauldronMud.toMudFromDirt(item.getItemStack().getType()), item.getItemStack().getAmount())
            );
            item.remove();
        }
    }
}
