/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.graves;

import com.google.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.pdc.DataTypes;
import me.machinemaker.papertweaks.utils.CachedHashObjectWrapper;
import me.machinemaker.papertweaks.utils.Keys;
import org.apache.commons.lang3.mutable.MutableInt;
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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.type.tuple.Pair;

import static java.util.Objects.requireNonNull;
import static me.machinemaker.papertweaks.utils.Entities.getNearbyEntitiesOfType;
import static me.machinemaker.papertweaks.utils.Entities.getSingleNearbyEntityOfType;
import static me.machinemaker.papertweaks.utils.PTUtils.nullUnionList;
import static me.machinemaker.papertweaks.utils.PTUtils.toCachedMapCount;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

class PlayerListener implements ModuleListener {

    static final NamespacedKey GRAVE_KEY = Keys.legacyKey("graves.grave_key");

    static final NamespacedKey LAST_GRAVE_LOCATION = Keys.legacyKey("graves.last_grave_location");

    private static final NamespacedKey PROTECTED = Keys.legacyKey("protected");
    private static final NamespacedKey TIMESTAMP = Keys.legacyKey("timestamp");
    static final NamespacedKey PLAYER_UUID = Keys.legacyKey("player_uuid");
    static final NamespacedKey PLAYER_ALL_CONTENTS = Keys.legacyKey("player_all_contents");
    private static final NamespacedKey PLAYER_EXPERIENCE = Keys.legacyKey("graves.player_experience");

    private final JavaPlugin plugin;
    private final Config config;

    @Inject
    PlayerListener(final JavaPlugin plugin, final Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    static Optional<GravePair> createGravePair(final Collection<ArmorStand> stands) { // all armor stands should have player uuid and timestamp PDC values
        if (stands.size() < 2) return Optional.empty();
        Long timestamp;
        for (final ArmorStand stand1 : stands) {
            timestamp = requireNonNull(stand1.getPersistentDataContainer().get(TIMESTAMP, PersistentDataType.LONG));
            for (final ArmorStand stand2 : stands) {
                if (stand1 == stand2) continue; // skip same
                if (Objects.equals(stand2.getPersistentDataContainer().get(TIMESTAMP, PersistentDataType.LONG), timestamp)) {
                    if (isHeadstone(stand1)) {
                        return Optional.of(new GravePair(stand1, stand2, timestamp));
                    } else if (isHeadstone(stand2)) {
                        return Optional.of(new GravePair(stand2, stand1, timestamp));
                    }
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    static boolean isHeadstone(final PersistentDataHolder holder) {
        final PersistentDataContainer pdc = holder.getPersistentDataContainer();
        return pdc.has(PLAYER_ALL_CONTENTS, DataTypes.ITEMSTACK_ARRAY) || pdc.has(PLAYER_EXPERIENCE, PersistentDataType.INTEGER) || pdc.has(PLAYER_INV_CONTENTS, DataTypes.ITEMSTACK_ARRAY);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (event.getKeepInventory()) return;
        if (event.getDrops().isEmpty() && (!this.config.xpCollection || event.getDroppedExp() == 0)) return;
        final Player player = event.getEntity();
        final Location playerLocation = player.getLocation();
        final World world = requireNonNull(playerLocation.getWorld());
        if (!player.hasPermission("vanillatweaks.playergraves") || !player.hasPermission("vanillatweaks.graves")) return;
        if (this.config.disabledWorlds.contains(world.getName())) return;

        Block spawnBlock = playerLocation.getBlock();
        if (playerLocation.getBlockY() <= world.getMinHeight()) {
            final Location bottom = playerLocation.clone();
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

        final Location graveLocation = spawnBlock.getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5);
        final PlayerInventory inventory = player.getInventory();
        final List<ItemStack> drops = event.getDrops();
        final Map<CachedHashObjectWrapper<ItemStack>, MutableInt> cachedDrops = toCachedMapCount(drops);
        List<@Nullable ItemStack> allContents = Arrays.asList(inventory.getContents());
        allContents = nullUnionList(allContents, cachedDrops);
        drops.clear();
        // If plugins add some drops - they should drop on the ground
        cachedDrops.forEach((wrapper, count) ->
                drops.addAll(Collections.nCopies(count.intValue(), wrapper.item))
        );

        if (this.config.graveLocating) {
            player.sendMessage(translatable("modules.graves.last-grave-location", GOLD, translatable("modules.graves.location-format", YELLOW, text(graveLocation.getBlockX()), text(graveLocation.getBlockY()), text(graveLocation.getBlockZ())), text(graveLocation.getWorld().getName(), YELLOW)));
        }

        final Long timestamp = System.currentTimeMillis();
        final ArmorStand block = (ArmorStand) world.spawnEntity(graveLocation.clone().subtract(-0.1, 1.77, 0), EntityType.ARMOR_STAND);
        this.setupStand(block, Material.PODZOL);
        block.getPersistentDataContainer().set(PLAYER_UUID, DataTypes.UUID, player.getUniqueId());
        block.getPersistentDataContainer().set(TIMESTAMP, PersistentDataType.LONG, timestamp);
        final ArmorStand headstone = (ArmorStand) world.spawnEntity(graveLocation.clone().subtract(0.3, 1.37, 0), EntityType.ARMOR_STAND);
        final PersistentDataContainer headstonePDC = headstone.getPersistentDataContainer();
        if (event.getDroppedExp() > 0 && this.config.xpCollection) {
            headstonePDC.set(PLAYER_EXPERIENCE, PersistentDataType.INTEGER, event.getDroppedExp());
            event.setDroppedExp(0);
        }
        headstonePDC.set(PLAYER_UUID, DataTypes.UUID, player.getUniqueId());
        headstonePDC.set(PLAYER_ALL_CONTENTS, DataTypes.ITEMSTACK_ARRAY, allContents.toArray(new ItemStack[0]));
        headstonePDC.set(TIMESTAMP, PersistentDataType.LONG, timestamp);
        this.setupStand(headstone, Graves.GRAVESTONES.get(0));
        Collections.shuffle(Graves.GRAVESTONES);
        headstone.customName(text(player.getName()));
        headstone.setCustomNameVisible(true);
        player.getPersistentDataContainer().set(LAST_GRAVE_LOCATION, DataTypes.LOCATION, headstone.getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerSneak(final PlayerToggleSneakEvent event) {
        if (!this.config.legacyShiftBehavior) return;
        if (!event.isSneaking()) return;
        final Player player = event.getPlayer();
        final Location location = player.getLocation();
        final Collection<ArmorStand> stands = getNearbyEntitiesOfType(ArmorStand.class, location, 0.5, 1, 0.5, stand -> stand.getPersistentDataContainer().has(PLAYER_UUID, DataTypes.UUID) && stand.getPersistentDataContainer().has(TIMESTAMP, PersistentDataType.LONG));
        final Optional<GravePair> gravePairOptional = createGravePair(stands);
        if (gravePairOptional.isEmpty()) {
            return;
        }
        this.handleGrave(gravePairOptional.get(), player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractAtEntityEvent(final PlayerInteractAtEntityEvent event) {
        if (this.config.legacyShiftBehavior) return;
        if (event.getRightClicked().getType() != EntityType.ARMOR_STAND || !event.getRightClicked().getPersistentDataContainer().has(TIMESTAMP, PersistentDataType.LONG))
            return;
        final @Nullable ArmorStand headstone;
        final @Nullable ArmorStand base;
        final @Nullable Long timestamp = event.getRightClicked().getPersistentDataContainer().get(TIMESTAMP, PersistentDataType.LONG);
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
        this.handleGrave(new GravePair(headstone, base, timestamp), event.getPlayer());
    }

    private void handleGrave(final GravePair pair, final Player player) {
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        final boolean isCarryingGraveKey = itemInMainHand.getItemMeta() != null && itemInMainHand.getItemMeta().getPersistentDataContainer().has(GRAVE_KEY, DataTypes.BOOLEAN) && player.hasPermission("vanillatweaks.graves.admin.grave-key");
        if (!player.getUniqueId().equals(pair.playerUUID) && !isCarryingGraveKey) {
            if (!this.config.graveRobbing) {
                player.sendMessage(translatable("modules.graves.grave-robbing.disabled", RED));
                return;
            }
            if (pair.timestamp != null && pair.timestamp + ((long)this.config.graveRobbingTimer * 1_000L) > System.currentTimeMillis()) {
                player.sendMessage(translatable("modules.graves.grave-robbing.disabled", RED));
                return;
            }
        }

        final PersistentDataContainer headstone = pair.getHeadstone().getPersistentDataContainer();

        if (headstone.has(PLAYER_EXPERIENCE, PersistentDataType.INTEGER)) {
            player.getWorld().spawn(player.getLocation(), ExperienceOrb.class, xpOrb -> {
                xpOrb.setExperience(headstone.getOrDefault(PLAYER_EXPERIENCE, PersistentDataType.INTEGER, 0));
            });
        }

        final PlayerInventory inventory = player.getInventory();
        for (final @Nullable ItemStack stack : inventory.getContents()) {
            if (stack != null) {
                player.getWorld().dropItem(player.getLocation(), stack).setPickupDelay(0);
            }
        }

        final ItemStack @Nullable [] allContents = headstone.get(PLAYER_ALL_CONTENTS, DataTypes.ITEMSTACK_ARRAY);
        if (allContents != null) {
            inventory.setContents(allContents);
        } else {
            // legacy
            handleLegacyGrave(headstone, inventory);
        }
        player.getWorld().spawnParticle(Particle.POOF, pair.getHeadstone().getLocation().add(0, 1.7, 0), 10, 0, 0, 0, 0.05);
        pair.remove();
        if (pair.playerUUID.equals(player.getUniqueId())) {
            player.getPersistentDataContainer().remove(LAST_GRAVE_LOCATION);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                final OfflinePlayer graveOwner = Bukkit.getOfflinePlayer(pair.playerUUID);
                if (graveOwner.getPlayer() != null) {
                    Bukkit.getScheduler().runTask(this.plugin, () -> graveOwner.getPlayer().getPersistentDataContainer().remove(LAST_GRAVE_LOCATION));
                }
            });
        }
    }

    @Deprecated
    private static final NamespacedKey PLAYER_INV_CONTENTS = Keys.legacyKey("player_inventory_contents");
    @Deprecated
    private static final NamespacedKey PLAYER_ARM_CONTENTS = Keys.legacyKey("player_armor_contents");
    @Deprecated
    private static final NamespacedKey PLAYER_EXTRA_CONTENTS = Keys.legacyKey("player_extra_contents");
    private static void handleLegacyGrave(final PersistentDataContainer headstone, final PlayerInventory inventory) {
        final ItemStack @Nullable [] storage = headstone.get(PLAYER_INV_CONTENTS, DataTypes.ITEMSTACK_ARRAY);
        final ItemStack @Nullable [] armor = headstone.get(PLAYER_ARM_CONTENTS, DataTypes.ITEMSTACK_ARRAY);
        final ItemStack @Nullable [] extra = headstone.get(PLAYER_EXTRA_CONTENTS, DataTypes.ITEMSTACK_ARRAY);
        if (storage != null) inventory.setStorageContents(storage);
        inventory.setArmorContents(armor);
        inventory.setExtraContents(extra);
    }

    // Don't let players mess with the graves
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArmorStandManipulation(final PlayerArmorStandManipulateEvent event) {
        final PersistentDataContainer pdc = event.getRightClicked().getPersistentDataContainer();
        if (pdc.get(PROTECTED, PersistentDataType.BYTE) != null) event.setCancelled(true);
    }

    private void setupStand(final ArmorStand stand, final Material head) {
        stand.setInvulnerable(true);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setArms(false);
        stand.setCollidable(false);
        stand.getPersistentDataContainer().set(PROTECTED, PersistentDataType.BYTE, (byte) 1);
        stand.getEquipment().setHelmet(new ItemStack(head));
    }

    static class GravePair extends Pair<ArmorStand, ArmorStand> {

        final @Nullable Long timestamp;
        final UUID playerUUID;

        GravePair(final ArmorStand headStone, final ArmorStand base, final @Nullable Long timestamp) {
            super(headStone, base);
            this.timestamp = timestamp;
            this.playerUUID = requireNonNull(headStone.getPersistentDataContainer().get(PLAYER_UUID, DataTypes.UUID));
        }

        ArmorStand getHeadstone() {
            return this.first();
        }

        ArmorStand getBase() {
            return this.second();
        }

        void remove() {
            this.getHeadstone().remove();
            this.getBase().remove();
        }
    }
}
