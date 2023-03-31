/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.vanillatweaks.modules.items.armoredelytra;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.machinemaker.vanillatweaks.pdc.PDCKey;
import me.machinemaker.vanillatweaks.tags.Tags;
import me.machinemaker.vanillatweaks.utils.Entities;
import me.machinemaker.vanillatweaks.utils.Keys;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import static me.machinemaker.vanillatweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class ItemDropRunnable extends BukkitRunnable {

    static final PDCKey<ItemStack> ELYTRA_ITEM = PDCKey.itemStack(Keys.key("ae.elytra_item"));
    static final PDCKey<ItemStack> CHESTPLATE_ITEM = PDCKey.itemStack(Keys.key("ae.chestplate_item"));

    private final Item item;
    private final LookingFor lookingFor;
    private final BiPredicate<Item, Block> itemPredicate;
    private int counter = 0;

    ItemDropRunnable(final Item item, final LookingFor lookingFor) {
        this.item = item;
        this.lookingFor = lookingFor;
        this.itemPredicate = switch (lookingFor) {
            case CHESTPLATE -> constructBiPredicate(Tags.CHESTPLATES::isTagged);
            case ARMORED_ELYTRA ->
                    constructBiPredicate(i -> i.getItemStack().getType() == Material.ELYTRA && ItemListener.IS_ARMORED_ELYTRA.has(i.getItemStack()));
        };
    }

    private static BiPredicate<Item, Block> constructBiPredicate(final Predicate<Item> itemPredicate) {
        return (i, block) -> itemPredicate.test(i) && i.getLocation().subtract(0, 1, 0).getBlock().equals(block);
    }

    static void constructArmoredElytra(final Block block, final Item chestplate, final Item elytra) {
        final ItemStack chestStack = chestplate.getItemStack();
        if (chestStack.getItemMeta() == null) return;
        final ItemStack elytraStack = elytra.getItemStack();
        if (elytraStack.getItemMeta() == null) return;
        final ItemStack armoredElytra = new ItemStack(Material.ELYTRA);
        final ItemMeta armoredMeta = armoredElytra.getItemMeta();
        if (armoredMeta == null) return;

        final Material chestplateMaterial = chestStack.getType();
        armoredMeta.lore(List.of(join(text("+ "), translatable(chestplateMaterial)).color(GOLD).decoration(TextDecoration.ITALIC, false)));

        ItemListener.IS_ARMORED_ELYTRA.setTo(armoredMeta, true);
        ELYTRA_ITEM.setTo(armoredMeta, elytraStack);
        CHESTPLATE_ITEM.setTo(armoredMeta, chestStack);

        final Map<Enchantment, Integer> enchants = Maps.newHashMap(chestplate.getItemStack().getEnchantments());
        elytraStack.getEnchantments().forEach((enchantment, level) -> {
            final Map<Enchantment, Integer> conflicts = enchants.entrySet().stream().filter(entry -> enchantment.conflictsWith(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (conflicts.isEmpty()) {
                enchants.merge(enchantment, level, Math::max);
            } else {
                conflicts.put(enchantment, level); // add for easier sorting
                final var max = Collections.max(conflicts.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
                enchants.merge(max.getKey(), max.getValue(), Math::max);
            }
        });
        enchants.forEach((enchantment, integer) -> {
            armoredMeta.addEnchant(enchantment, integer, false);
        });

        final Multimap<Attribute, AttributeModifier> attributeMap = LinkedHashMultimap.create();
        final Multimap<Attribute, AttributeModifier> chestAttributes = chestStack.getItemMeta().getAttributeModifiers();
        if (chestAttributes != null) {
            attributeMap.putAll(chestAttributes);
        } else {
            attributeMap.putAll(chestStack.getType().getDefaultAttributeModifiers(EquipmentSlot.CHEST));
        }
        final Multimap<Attribute, AttributeModifier> elytraAttributes = elytraStack.getItemMeta().getAttributeModifiers();
        if (elytraAttributes != null) {
            attributeMap.putAll(elytraAttributes);
        } else {
            attributeMap.putAll(elytraStack.getType().getDefaultAttributeModifiers(EquipmentSlot.CHEST));
        }
        armoredMeta.setAttributeModifiers(attributeMap);

        armoredElytra.setItemMeta(armoredMeta);
        chestStack.setAmount(0);
        chestplate.remove();
        elytraStack.setAmount(0);
        elytra.remove();
        block.getWorld().dropItem(block.getLocation().add(0, 1.1, 0), armoredElytra);
        final Location location = block.getLocation();
        block.getWorld().playSound(Sound.sound(org.bukkit.Sound.BLOCK_ANVIL_USE.getKey(), Sound.Source.BLOCK, 1, 1), location.getX(), location.getY(), location.getZ());
    }

    static void breakArmoredElytra(final World world, final Location location, final Item elytra, final boolean sound) {
        final ItemStack elytraStack = elytra.getItemStack();
        if (elytraStack.getItemMeta() == null) return;

        final ItemStack elytraItem = ELYTRA_ITEM.getFrom(elytraStack);
        if (elytraItem != null) {
            world.dropItem(location.add(0, 1.1, 0), elytraItem);
        }
        final ItemStack chestItem = CHESTPLATE_ITEM.getFrom(elytraStack);
        if (chestItem != null) {
            world.dropItem(location.add(0, 1.1, 0), chestItem);
        }
        if (sound) {
            world.playSound(Sound.sound(org.bukkit.Sound.BLOCK_GRINDSTONE_USE.getKey(), Sound.Source.BLOCK, 1, 1), location.getX(), location.getY(), location.getZ());
        }
        elytraStack.setAmount(0);
        elytra.remove();
    }

    @Override
    public void run() {
        if (this.item.isDead() || this.counter >= 50) {
            this.cancel();
            return;
        }

        final Block block = this.item.getLocation().subtract(0, 1, 0).getBlock();
        if (this.lookingFor == LookingFor.ARMORED_ELYTRA) {
            if (block.getType() == Material.GRINDSTONE) {
                breakArmoredElytra(block.getWorld(), block.getLocation(), this.item, true);
            }
        } else {
            if (Tag.ANVIL.isTagged(block.getType())) {
                for (final Item nearbyItem : Entities.getNearbyEntitiesOfType(this.item, 0.5, 0.1, 0.5, i -> this.itemPredicate.test(i, block) && !i.isDead())) {
                    if (this.lookingFor == LookingFor.CHESTPLATE) {
                        constructArmoredElytra(block, nearbyItem, this.item);
                    }
                    this.cancel();
                    return;
                }
            }
        }

        this.counter++;
    }

    enum LookingFor {
        CHESTPLATE,
        ARMORED_ELYTRA
    }
}
