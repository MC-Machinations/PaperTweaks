package me.machinemaker.vanillatweaks.xpmanagement;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.server.ServerLoadEvent.LoadType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

public class XPManagement extends BaseModule implements Listener {

    private final NamespacedKey recipeKey;
    private final FurnaceRecipe recipe;

    public XPManagement(VanillaTweaks plugin) {
        super(plugin, config -> config.xpManagement);
        this.recipeKey = new NamespacedKey(plugin, "xp_management_recipe");
        this.recipe = new FurnaceRecipe(recipeKey, new ItemStack(Material.GLASS_BOTTLE), Material.EXPERIENCE_BOTTLE, 12, 1);
    }

    private void addRecipe() {
        if (Bukkit.getRecipe(recipeKey) == null) {
            Bukkit.addRecipe(recipe);
        }
    }

    private void removeRecipe() {
        Bukkit.removeRecipe(recipeKey);
    }

    @Override
    public void register() {
        registerEvents(this);
        addRecipe();
    }

    @Override
    public void unregister() {
        unregisterEvents(this);
        removeRecipe();
    }

    @EventHandler
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

        event.setUseInteractedBlock(Result.DENY);
        event.setUseItemInHand(Result.ALLOW);

        Player player = event.getPlayer();
        player.giveExp(-12);
        event.getItem().setAmount(event.getItem().getAmount() - 1);
        player.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE));
        player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.PLAYERS, 1f, 1.25f);
    }

    @EventHandler
    public void onReload(ServerLoadEvent event) {
        if (event.getType() == LoadType.RELOAD) {
            addRecipe();
        }
    }
}
