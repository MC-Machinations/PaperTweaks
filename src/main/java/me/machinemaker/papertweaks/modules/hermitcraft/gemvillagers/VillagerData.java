/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.modules.hermitcraft.gemvillagers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.machinemaker.papertweaks.common.PlayerSkull;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import static java.util.Objects.requireNonNull;

class VillagerData {

    private final Component name;
    private final PlayerSkull head;
    private final List<Offer> offers;

    @JsonCreator
    private VillagerData(final PlayerSkull head, final List<Offer> offers) {
        this.name = head.name();
        this.head = head;
        this.offers = offers;
    }

    public void spawnVillager(final World world, final Location location) {
        world.spawn(location, Villager.class, villager -> {
            villager.customName(this.name);
            villager.getEquipment().setHelmet(this.head.cloneSingle());
            villager.setVillagerLevel(5);
            villager.setProfession(Villager.Profession.MASON);
            villager.setVillagerType(Villager.Type.SWAMP);
            villager.setInvulnerable(true);
            villager.setCustomNameVisible(true);
            villager.setCanPickupItems(false);
            villager.setSilent(true);
            requireNonNull(villager.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED), "missing movement speed attribute").setBaseValue(0);
            requireNonNull(villager.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE), "missing knockback attribute").setBaseValue(0);
            final List<MerchantRecipe> recipes = new ArrayList<>();
            for (final Offer offer : this.offers) {
                recipes.add(offer.toRecipe());
            }
            villager.setRecipes(recipes);
        });
    }

    @JsonDeserialize(using = Offer.Deserializer.class)
    private static class Offer {

        private final ItemStack input;
        private final ItemStack output;

        private Offer(final ItemStack input, final ItemStack output) {
            this.input = input;
            this.output = output;
        }

        public MerchantRecipe toRecipe() {
            final MerchantRecipe recipe = new MerchantRecipe(this.output.clone(), Integer.MAX_VALUE);
            recipe.addIngredient(this.input.clone());
            return recipe;
        }

        private static class Deserializer extends JsonDeserializer<Offer> {

            @Override
            public Offer deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
                final ObjectNode objectNode = p.readValueAsTree();
                final ObjectNode input = (ObjectNode) objectNode.get("input");
                final ItemStack inputStack;
                if (input.has("id")) {
                    final Material material = requireNonNull(Registry.MATERIAL.get(requireNonNull(NamespacedKey.fromString(input.get("id").asText()))), "Need a valid material");
                    int count = 1;
                    if (input.has("count")) {
                        count = input.get("count").asInt();
                    }
                    inputStack = new ItemStack(material, count);
                } else {
                    inputStack = parseStack(input);
                }
                final ObjectNode output = (ObjectNode) objectNode.get("output");
                final ItemStack outputStack = parseStack(output);
                return new Offer(inputStack, outputStack);
            }

            private static ItemStack parseStack(final ObjectNode node) throws JsonProcessingException {
                if (node.size() == 1 && node.has("count")) {
                    return new ItemStack(Material.PLAYER_HEAD, node.get("count").asInt());
                } else {
                    return GemVillagers.JSON_MAPPER.treeToValue(node, PlayerSkull.class).cloneOriginal();
                }
            }
        }
    }
}
