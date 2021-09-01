package me.machinemaker.vanillatweaks.modules.experimental.xpmanagement;

import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements ModuleListener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (
                event.getHand() != EquipmentSlot.HAND ||
                        event.getAction() != Action.RIGHT_CLICK_BLOCK ||
                        event.getClickedBlock() == null ||
                        event.getClickedBlock().getType() != Material.ENCHANTING_TABLE ||
                        event.getItem() == null ||
                        event.getItem().getType() != Material.GLASS_BOTTLE ||
                        event.getPlayer().getTotalExperience() <= 11
        ) {
            return;
        }

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.ALLOW);

        Player player = event.getPlayer();
        player.giveExp(-12);
        event.getItem().setAmount(event.getItem().getAmount() - 1);
        player.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE));
        player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.PLAYERS, 1f, 1.25f);
    }
}
