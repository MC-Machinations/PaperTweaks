/*
 * VanillaTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021 Machine_Maker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.machinemaker.vanillatweaks.modules.survival.graves;

import cloud.commandframework.types.tuples.Pair;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.pdc.DataTypes;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.utils.CachedHashObjectWrapper;
import me.machinemaker.vanillatweaks.utils.Keys;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static me.machinemaker.vanillatweaks.utils.VTUtils.getNearbyEntitiesOfType;
import static me.machinemaker.vanillatweaks.utils.VTUtils.getSingleNearbyEntityOfType;
import static me.machinemaker.vanillatweaks.utils.VTUtils.nullUnionList;
import static me.machinemaker.vanillatweaks.utils.VTUtils.toCachedMapCount;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class PlayerListener implements ModuleListener {

    static final NamespacedKey GRAVE_KEY = Keys.key("graves.grave_key");

    static final NamespacedKey LAST_GRAVE_LOCATION = Keys.key("graves.last_grave_location");

    private static final NamespacedKey PROTECTED = Keys.key("protected");
    private static final NamespacedKey TIMESTAMP = Keys.key("timestamp");
    private static final NamespacedKey PLAYER_UUID = Keys.key("player_uuid");
    private static final NamespacedKey PLAYER_ALL_CONTENTS = Keys.key("player_all_contents");
    private static final NamespacedKey PLAYER_EXPERIENCE = Keys.key("graves.player_experience");
    private static final @Deprecated NamespacedKey PLAYER_INV_CONTENTS = Keys.key("player_inventory_contents");
    private static final @Deprecated NamespacedKey PLAYER_ARM_CONTENTS = Keys.key("player_armor_contents");
    private static final @Deprecated NamespacedKey PLAYER_EXTRA_CONTENTS = Keys.key("player_extra_contents");

    private final JavaPlugin plugin;
    private final Config config;
    private final BukkitAudiences audiences;

    @Inject
    PlayerListener(JavaPlugin plugin, Config config, BukkitAudiences audiences) {
        this.plugin = plugin;
        this.config = config;
        this.audiences = audiences;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) return;
        if (event.getDrops().isEmpty() && (!this.config.xpCollection || event.getDroppedExp() == 0)) return;
        Player player = event.getEntity();
        Audience playerAudience = this.audiences.player(player);
        Location playerLocation = player.getLocation();
        World world = Objects.requireNonNull(playerLocation.getWorld());
        if (!player.hasPermission("vanillatweaks.playergraves")) return;
        if (config.disabledWorlds.contains(world.getName())) return;

        Block spawnBlock = playerLocation.getBlock();
        if (playerLocation.getBlockY() <= world.getMinHeight()) {
            Location bottom = playerLocation.clone();
            bottom.setY(world.getMinHeight());
            spawnBlock = bottom.getBlock();
            while (spawnBlock.getRelative(BlockFace.UP).getType() != Material.AIR) {
                spawnBlock = spawnBlock.getRelative(BlockFace.UP);
            }
            if (spawnBlock.getType() == Material.AIR) {
                spawnBlock.setType(Material.COBBLESTONE);
            }
        } else {
            while (spawnBlock.getType() == Material.AIR) {
                spawnBlock = spawnBlock.getRelative(BlockFace.DOWN);
                if (spawnBlock.getLocation().getBlockY() < world.getMinHeight()) {
                    spawnBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                    spawnBlock.setType(Material.COBBLESTONE);
                    break;
                }
            }
        }

        Location graveLocation = spawnBlock.getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5);
        PlayerInventory inventory = player.getInventory();
        List<ItemStack> drops = event.getDrops();
        @NotNull Map<CachedHashObjectWrapper<ItemStack>, MutableInt> cachedDrops = toCachedMapCount(drops);
        List<ItemStack> allContents = Arrays.asList(inventory.getContents());
        allContents = nullUnionList(allContents, cachedDrops);
        drops.clear();
        // If plugins add some drops - they should drop on the ground
        cachedDrops.forEach((wrapper, count) ->
                drops.addAll(Collections.nCopies(count.intValue(), wrapper.item))
        );

        if (this.config.graveLocating) {
            playerAudience.sendMessage(translatable("modules.graves.last-grave-location", GOLD, translatable("modules.graves.location-format", YELLOW, text(graveLocation.getBlockX()), text(graveLocation.getBlockY()), text(graveLocation.getBlockZ()))));
        }

        Long timestamp = System.nanoTime();
        ArmorStand block = (ArmorStand) world.spawnEntity(graveLocation.clone().subtract(-0.1, 1.77, 0), EntityType.ARMOR_STAND);
        setupStand(block, Material.PODZOL);
        block.getPersistentDataContainer().set(PLAYER_UUID, DataTypes.UUID, player.getUniqueId());
        block.getPersistentDataContainer().set(TIMESTAMP, PersistentDataType.LONG, timestamp);
        ArmorStand headstone = (ArmorStand) world.spawnEntity(graveLocation.clone().subtract(0.3, 1.37, 0), EntityType.ARMOR_STAND);
        PersistentDataContainer headstonePDC = headstone.getPersistentDataContainer();
        if (event.getDroppedExp() > 0 && this.config.xpCollection) {
            headstonePDC.set(PLAYER_EXPERIENCE, PersistentDataType.INTEGER, event.getDroppedExp());
            event.setDroppedExp(0);
        }
        headstonePDC.set(PLAYER_UUID, DataTypes.UUID, player.getUniqueId());
        headstonePDC.set(PLAYER_ALL_CONTENTS, DataTypes.ITEMSTACK_ARRAY, allContents.toArray(new ItemStack[0]));
        headstonePDC.set(TIMESTAMP, PersistentDataType.LONG, timestamp);
        setupStand(headstone, Graves.GRAVESTONES.get(0));
        Collections.shuffle(Graves.GRAVESTONES);
        headstone.setCustomName(player.getName());
        headstone.setCustomNameVisible(true);
        player.getPersistentDataContainer().set(LAST_GRAVE_LOCATION, DataTypes.LOCATION, headstone.getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!this.config.legacyShiftBehavior) return;
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        Location location = player.getLocation();
        Collection<ArmorStand> stands = getNearbyEntitiesOfType(ArmorStand.class, location, 0.5, 1, 0.5, stand -> stand.getPersistentDataContainer().has(PLAYER_UUID, DataTypes.UUID) && stand.getPersistentDataContainer().has(TIMESTAMP, PersistentDataType.LONG));
        Optional<GravePair> gravePairOptional = createGravePair(stands);
        if (gravePairOptional.isEmpty()) {
            return;
        }
        handleGrave(gravePairOptional.get(), player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
        if (this.config.legacyShiftBehavior) return;
        if (event.getRightClicked().getType() != EntityType.ARMOR_STAND || !event.getRightClicked().getPersistentDataContainer().has(TIMESTAMP, PersistentDataType.LONG)) return;
        ArmorStand headstone;
        ArmorStand base;
        Long timestamp = event.getRightClicked().getPersistentDataContainer().get(TIMESTAMP, PersistentDataType.LONG);
        if (isHeadstone(event.getRightClicked())) {
            headstone = (ArmorStand) event.getRightClicked();
            base = getSingleNearbyEntityOfType(ArmorStand.class, event.getRightClicked().getLocation(), 0.5, 0.5, 0, stand -> Objects.equals(timestamp, stand.getPersistentDataContainer().get(TIMESTAMP, PersistentDataType.LONG)) && stand != event.getRightClicked());
        } else {
            base = (ArmorStand) event.getRightClicked();
            headstone = getSingleNearbyEntityOfType(ArmorStand.class, event.getRightClicked().getLocation(), 0.5, 0.5, 0, stand -> Objects.equals(timestamp, stand.getPersistentDataContainer().get(TIMESTAMP, PersistentDataType.LONG)) && stand != event.getRightClicked());
        }
        if (headstone == null || base == null) {
            return;
        }
        handleGrave(new GravePair(headstone, base), event.getPlayer());
    }

    private void handleGrave(GravePair pair, Player player) {
        Audience audience = this.audiences.player(player);
        if (!this.config.graveRobbing && !player.getUniqueId().equals(pair.playerUUID) && (!player.hasPermission("vanillatweaks.admin.grave-key") || !(player.getInventory().getItemInMainHand().getItemMeta() != null && player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(GRAVE_KEY, DataTypes.BOOLEAN)))) {
            audience.sendMessage(translatable("modules.graves.grave-robbing.disabled", RED));
            return;
        }

        PersistentDataContainer headstone = pair.getHeadstone().getPersistentDataContainer();

        if (headstone.has(PLAYER_EXPERIENCE, PersistentDataType.INTEGER)) {
            player.getWorld().spawn(player.getLocation(), ExperienceOrb.class, xpOrb -> {
                xpOrb.setExperience(headstone.getOrDefault(PLAYER_EXPERIENCE, PersistentDataType.INTEGER, 0));
            });
        }

        PlayerInventory inventory = player.getInventory();
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null) {
                player.getWorld().dropItem(player.getLocation(), stack).setPickupDelay(0);
            }
        }

        ItemStack[] allContents = headstone.get(PLAYER_ALL_CONTENTS, DataTypes.ITEMSTACK_ARRAY);
        if (allContents != null) {
            inventory.setContents(allContents);
        } else {
            // legacy
            ItemStack[] storage = headstone.get(PLAYER_INV_CONTENTS, DataTypes.ITEMSTACK_ARRAY);
            ItemStack[] armor = headstone.get(PLAYER_ARM_CONTENTS, DataTypes.ITEMSTACK_ARRAY);
            ItemStack[] extra = headstone.get(PLAYER_EXTRA_CONTENTS, DataTypes.ITEMSTACK_ARRAY);
            inventory.setStorageContents(storage);
            inventory.setArmorContents(armor);
            inventory.setExtraContents(extra);
        }
        player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, pair.getHeadstone().getLocation().add(0, 1.7, 0), 10, 0, 0, 0, 0.05);
        pair.remove();
        if (pair.playerUUID.equals(player.getUniqueId())) {
            player.getPersistentDataContainer().remove(LAST_GRAVE_LOCATION);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                OfflinePlayer graveOwner = Bukkit.getOfflinePlayer(pair.playerUUID);
                if (graveOwner.getPlayer() != null) {
                    Bukkit.getScheduler().runTask(this.plugin, () -> graveOwner.getPlayer().getPersistentDataContainer().remove(LAST_GRAVE_LOCATION));
                }
            });
        }
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

    static Optional<GravePair> createGravePair(Collection<ArmorStand> stands) { // all armor stands should have player uuid and timestamp PDC values
        if (stands.size() < 2) return Optional.empty();
        Long timestamp;
        for (ArmorStand stand1 : stands) {
            timestamp = stand1.getPersistentDataContainer().get(TIMESTAMP, PersistentDataType.LONG);
            for (ArmorStand stand2 : stands) {
                if (stand1 == stand2) continue; // skip same
                if (Objects.equals(stand2.getPersistentDataContainer().get(TIMESTAMP, PersistentDataType.LONG), timestamp)) {
                    if (isHeadstone(stand1)) {
                        return Optional.of(new GravePair(stand1, stand2));
                    } else if (isHeadstone(stand2)) {
                        return Optional.of(new GravePair(stand2, stand1));
                    }
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    static boolean isHeadstone(PersistentDataHolder holder) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        return pdc.has(PLAYER_ALL_CONTENTS, DataTypes.ITEMSTACK_ARRAY) || pdc.has(PLAYER_EXPERIENCE, PersistentDataType.INTEGER) || pdc.has(PLAYER_INV_CONTENTS, DataTypes.ITEMSTACK_ARRAY);
    }

    static class GravePair extends Pair<ArmorStand, ArmorStand> {

        final @NotNull UUID playerUUID;

        GravePair(ArmorStand headStone, ArmorStand base) {
            super(headStone, base);
            this.playerUUID = headStone.getPersistentDataContainer().get(PLAYER_UUID, DataTypes.UUID);
        }

        ArmorStand getHeadstone() {
            return this.getFirst();
        }

        ArmorStand getBase() {
            return this.getSecond();
        }

        void remove() {
            this.getHeadstone().remove();
            this.getBase().remove();
        }
    }
}
