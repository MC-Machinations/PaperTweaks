/*
 * GNU General Public License v3
 *
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
package me.machinemaker.vanillatweaks.modules.items.armoredelytra;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import me.machinemaker.vanillatweaks.pdc.PDCKey;
import me.machinemaker.vanillatweaks.tags.Tags;
import me.machinemaker.vanillatweaks.utils.Keys;
import me.machinemaker.vanillatweaks.utils.VTUtils;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.TextComponent.ofChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class ItemDropRunnable extends BukkitRunnable {

    static final PDCKey<ItemStack> ELYTRA_ITEM = PDCKey.itemStack(Keys.key("ae.elytra_item"));
    static final PDCKey<ItemStack> CHESTPLATE_ITEM = PDCKey.itemStack(Keys.key("ae.chestplate_item"));

    private final Item item;
    private final LookingFor lookingFor;
    private final BiPredicate<Item, Block> itemPredicate;
    private int counter = 0;

    ItemDropRunnable(Item item, LookingFor lookingFor) {
        this.item = item;
        this.lookingFor = lookingFor;
        this.itemPredicate = switch (lookingFor) {
            case CHESTPLATE -> constructBiPredicate(Tags.CHESTPLATES::isTagged);
            case ARMORED_ELYTRA -> constructBiPredicate(i -> i.getItemStack().getType() == Material.ELYTRA && ItemListener.IS_ARMORED_ELYTRA.has(i.getItemStack()));
        };
    }

    enum LookingFor {
        CHESTPLATE,
        ARMORED_ELYTRA
    }

    private static BiPredicate<Item, Block> constructBiPredicate(Predicate<Item> itemPredicate) {
        return (i, block) -> itemPredicate.test(i) && i.getLocation().subtract(0, 1, 0).getBlock().equals(block);
    }

    @Override
    public void run() {
        if (item.isDead() || counter >= 50) {
            this.cancel();
            return;
        }

        final Block block = item.getLocation().subtract(0, 1, 0).getBlock();
        if (this.lookingFor == LookingFor.ARMORED_ELYTRA) {
            if (block.getType() == Material.GRINDSTONE) {
                breakArmoredElytra(block.getWorld(), block.getLocation(), item, true);
            }
        } else {
            if (Tag.ANVIL.isTagged(block.getType())) {
                for (Item nearbyItem : VTUtils.getNearbyEntitiesOfType(item, 0.5, 0.1, 0.5, i -> this.itemPredicate.test(i, block) && !i.isDead())) {
                    switch (lookingFor) {
                        case CHESTPLATE -> constructArmoredElytra(block, nearbyItem, item);
                    }
                    this.cancel();
                    return;
                }
            }
        }

        counter++;
    }

    static void constructArmoredElytra(Block block, Item chestplate, Item elytra) {
        ItemStack chestStack = chestplate.getItemStack();
        if (chestStack.getItemMeta() == null) return;
        ItemStack elytraStack = elytra.getItemStack();
        if (elytraStack.getItemMeta() == null) return;;
        ItemStack armoredElytra = new ItemStack(Material.ELYTRA);
        ItemMeta armoredMeta = armoredElytra.getItemMeta();
        if (armoredMeta == null) return;

        Material chestplateMaterial = chestStack.getType();
        armoredMeta.lore(List.of(ofChildren(text("+ "), translatable(chestplateMaterial)).color(GOLD).decoration(TextDecoration.ITALIC, false)));

        ItemListener.IS_ARMORED_ELYTRA.setFrom(armoredMeta, true);
        ELYTRA_ITEM.setFrom(armoredMeta, elytraStack);
        CHESTPLATE_ITEM.setFrom(armoredMeta, chestStack);

        Map<Enchantment, Integer> enchants = Maps.newHashMap(chestplate.getItemStack().getEnchantments());
        elytraStack.getEnchantments().forEach((enchantment, level) -> {
            Map<Enchantment, Integer> conflicts = enchants.entrySet().stream().filter(entry -> enchantment.conflictsWith(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (conflicts.isEmpty()) {
                enchants.merge(enchantment, level, Math::max);
            } else {
                conflicts.put(enchantment, level); // add for easier sorting
                var max = Collections.max(conflicts.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
                enchants.merge(max.getKey(), max.getValue(), Math::max);
            }
        });
        enchants.forEach((enchantment, integer) -> {
            armoredMeta.addEnchant(enchantment, integer, false);
        });

        Multimap<Attribute, AttributeModifier> attributeMap = LinkedHashMultimap.create();
        Multimap<Attribute, AttributeModifier> chestAttributes = chestStack.getItemMeta().getAttributeModifiers();
        if (chestAttributes != null) {
            attributeMap.putAll(chestAttributes);
        } else {
            // TODO when paper fix is done, can just be a putAll
            chestStack.getType().getItemAttributes(EquipmentSlot.CHEST).forEach((attribute, mod) -> {
                attributeMap.put(attribute, new AttributeModifier(mod.getUniqueId(), mod.getName(), mod.getAmount(), mod.getOperation(), EquipmentSlot.CHEST));
            });
        }
        Multimap<Attribute, AttributeModifier> elytraAttributes = elytraStack.getItemMeta().getAttributeModifiers();
        if (elytraAttributes != null) {
            attributeMap.putAll(elytraAttributes);
        } else {
            elytraStack.getType().getItemAttributes(EquipmentSlot.CHEST).forEach((attribute, mod) -> {
                attributeMap.put(attribute, new AttributeModifier(mod.getUniqueId(), mod.getName(), mod.getAmount(), mod.getOperation(), EquipmentSlot.CHEST));
            });
        }
        armoredMeta.setAttributeModifiers(attributeMap);

        armoredElytra.setItemMeta(armoredMeta);
        chestStack.setAmount(0);
        chestplate.remove();
        elytraStack.setAmount(0);
        elytra.remove();
        block.getWorld().dropItem(block.getLocation().add(0, 1.1, 0), armoredElytra);
        block.getWorld().playSound(Sound.sound(org.bukkit.Sound.BLOCK_ANVIL_USE.getKey(), Sound.Source.BLOCK, 1, 1));
    }

    static void breakArmoredElytra(World world, Location location, Item elytra, boolean sound) {
        ItemStack elytraStack = elytra.getItemStack();
        if (elytraStack.getItemMeta() == null) return;

        ItemStack elytraItem = ELYTRA_ITEM.getFrom(elytraStack);
        if (elytraItem != null) {
            world.dropItem(location.add(0, 1.1, 0), elytraItem);
        }
        ItemStack chestItem = CHESTPLATE_ITEM.getFrom(elytraStack);
        if (chestItem != null) {
            world.dropItem(location.add(0, 1.1, 0), chestItem);
        }
        if (sound) {
            world.playSound(Sound.sound(org.bukkit.Sound.BLOCK_GRINDSTONE_USE.getKey(), Sound.Source.BLOCK, 1, 1));
        }
        elytraStack.setAmount(0);
        elytra.remove();
    }
}
