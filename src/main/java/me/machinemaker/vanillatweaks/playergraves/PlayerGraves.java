package me.machinemaker.vanillatweaks.playergraves;

import com.google.common.collect.Lists;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.Lang;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.utils.DataType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PlayerGraves extends BaseModule implements Listener {

    private final NamespacedKey PROTECTED = new NamespacedKey(this.plugin, "protected");
    private final NamespacedKey TIMESTAMP = new NamespacedKey(this.plugin, "timestamp");
    private final NamespacedKey PLAYER_UUID = new NamespacedKey(this.plugin, "player_uuid");
    private final NamespacedKey PLAYER_INV_CONTENTS = new NamespacedKey(this.plugin, "player_inventory_contents");
    private final NamespacedKey PLAYER_ARM_CONTENTS = new NamespacedKey(this.plugin, "player_armor_contents");

    private final List<Material> graves = Lists.newArrayList(Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL);

    public PlayerGraves(VanillaTweaks plugin) {
        super(plugin, config -> config.playerGraves);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) return;
        boolean empty = true;
        for (ItemStack stack : event.getEntity().getInventory().getContents()) {
            if (stack != null) {
                empty = false;
                break;
            }
        }
        if (empty) return;
        Block spawnBlock = event.getEntity().getLocation().getBlock();
        while (spawnBlock.getType() == Material.AIR) {
            spawnBlock = spawnBlock.getRelative(BlockFace.DOWN);
        }
        Location location = spawnBlock.getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5);
        event.getEntity().sendMessage(Lang.GRAVE_AT.p().replace("%x%", String.valueOf(location.getBlockX())).replace("%y%", String.valueOf(location.getBlockY())).replace("%z%", String.valueOf(location.getBlockZ())));
        Long timestamp = System.currentTimeMillis();
        ArmorStand block = (ArmorStand) location.getWorld().spawnEntity(location.clone().subtract(-0.1, 1.77, 0), EntityType.ARMOR_STAND);
        setupStand(block, Material.PODZOL);
        block.getPersistentDataContainer().set(PLAYER_UUID, DataType.UUID, event.getEntity().getUniqueId());
        block.getPersistentDataContainer().set(TIMESTAMP, PersistentDataType.LONG, timestamp);
        ArmorStand headstone = (ArmorStand) location.getWorld().spawnEntity(location.clone().subtract(0.3, 1.37, 0), EntityType.ARMOR_STAND);
        PersistentDataContainer container = headstone.getPersistentDataContainer();
        container.set(PLAYER_UUID, DataType.UUID, event.getEntity().getUniqueId());
        container.set(PLAYER_INV_CONTENTS, DataType.ITEMSTACK_ARRAY, event.getEntity().getInventory().getStorageContents());
        container.set(PLAYER_ARM_CONTENTS, DataType.ITEMSTACK_ARRAY, event.getEntity().getInventory().getArmorContents());
        container.set(TIMESTAMP, PersistentDataType.LONG, timestamp);
        setupStand(headstone, graves.get(0));
        Collections.shuffle(graves);
        event.getDrops().clear();
        event.getEntity().getInventory().clear();
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Collection<Entity> entities = event.getPlayer().getLocation().getWorld().getNearbyEntities(event.getPlayer().getLocation(), 0.5, 1, 0.5, entity -> entity.getType() == EntityType.ARMOR_STAND && event.getPlayer().getUniqueId().equals(entity.getPersistentDataContainer().get(PLAYER_UUID, DataType.UUID)));
        if (entities.size() < 2) return;
        Long timestamp = entities.stream().findAny().get().getPersistentDataContainer().get(TIMESTAMP, PersistentDataType.LONG);
        entities.stream().filter(entity -> entity.getPersistentDataContainer().get(TIMESTAMP, PersistentDataType.LONG).equals(timestamp)).forEach(entity -> {
            PersistentDataContainer container = entity.getPersistentDataContainer();
            if (!container.has(PLAYER_ARM_CONTENTS, DataType.ITEMSTACK_ARRAY)) {
                entity.remove();
                return;
            }
            for (ItemStack stack : event.getPlayer().getInventory().getContents()) {
                if (stack != null) event.getPlayer().getLocation().getWorld().dropItemNaturally(event.getPlayer().getLocation(), stack);
            }
            ItemStack[] storage = container.get(PLAYER_INV_CONTENTS, DataType.ITEMSTACK_ARRAY);
            ItemStack[] armor = container.get(PLAYER_ARM_CONTENTS, DataType.ITEMSTACK_ARRAY);
            event.getPlayer().getInventory().setStorageContents(storage);
            event.getPlayer().getInventory().setArmorContents(armor);
            event.getPlayer().sendMessage("Retrieved items");
            entity.remove();
        });
    }

    // Don't let players mess with the graves
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArmorStandManipulation(PlayerArmorStandManipulateEvent event) {
        PersistentDataContainer pdc = event.getRightClicked().getPersistentDataContainer();
        if (pdc.get(PROTECTED, PersistentDataType.BYTE) != null) event.setCancelled(true);
    }

    private void setupStand(ArmorStand stand, Material head) {
        stand.setInvulnerable(true);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setArms(false);
        stand.setCollidable(false);
        stand.getPersistentDataContainer().set(PROTECTED, PersistentDataType.BYTE, (byte)1);
        stand.getEquipment().setHelmet(new ItemStack(head));
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
