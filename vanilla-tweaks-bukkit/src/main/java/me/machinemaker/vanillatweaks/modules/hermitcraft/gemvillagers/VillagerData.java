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
package me.machinemaker.vanillatweaks.modules.hermitcraft.gemvillagers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.machinemaker.vanillatweaks.common.PlayerSkull;
import me.machinemaker.vanillatweaks.utils.ReflectionUtils;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

class VillagerData {

    private static final Class<?> NMS_ENTITY_CLASS = ReflectionUtils.getMinecraftClass("world.entity.Entity");
    private static final Class<?> NMS_CHAT_COMPONENT_CLASS = ReflectionUtils.findMinecraftClass("network.chat.IChatBaseComponent", "network.chat.Component");
    private static final ReflectionUtils.MethodInvoker ENTITY_SET_CUSTOM_NAME_METHOD = ReflectionUtils.method(NMS_ENTITY_CLASS, Void.TYPE, NMS_CHAT_COMPONENT_CLASS).named( "a", "setCustomName").build();
    private static final Class<?> CRAFT_ENTITY_CLASS = ReflectionUtils.getCraftBukkitClass("entity.CraftEntity");
    private static final ReflectionUtils.MethodInvoker CRAFT_ENTITY_GET_HANDLE_METHOD = ReflectionUtils.getTypedMethod(CRAFT_ENTITY_CLASS, "getHandle", NMS_ENTITY_CLASS);

    private final Component name;
    private final PlayerSkull head;
    private final List<Offer> offers;

    @JsonCreator
    private VillagerData(@NotNull String name, @NotNull PlayerSkull head, List<Offer> offers) {
        this.name = GsonComponentSerializer.gson().deserialize(name);
        this.head = head;
        this.offers = offers;
    }

    public void spawnVillager(@NotNull World world, @NotNull Location location) {
        world.spawn(location, Villager.class, villager -> {
            ENTITY_SET_CUSTOM_NAME_METHOD.invoke(CRAFT_ENTITY_GET_HANDLE_METHOD.invoke(villager), MinecraftComponentSerializer.get().serialize(this.name));
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
            List<MerchantRecipe> recipes = new ArrayList<>();
            for (Offer offer : this.offers) {
                recipes.add(offer.toRecipe());
            }
            villager.setRecipes(recipes);
        });
    }

    @JsonDeserialize(using = Offer.Deserializer.class)
    private static class Offer {

        private final ItemStack input;
        private final ItemStack output;

        private Offer(ItemStack input, ItemStack output) {
            this.input = input;
            this.output = output;
        }

        public @NotNull MerchantRecipe toRecipe() {
            var recipe = new MerchantRecipe(this.output.clone(), Integer.MAX_VALUE);
            recipe.addIngredient(this.input.clone());
            return recipe;
        }

        private static class Deserializer extends JsonDeserializer<Offer> {

            @Override
            public Offer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                ObjectNode objectNode = p.readValueAsTree();
                ObjectNode input = (ObjectNode) objectNode.get("input");
                ItemStack inputStack;
                if (input.has("id")) {
                    Material material = requireNonNull(Registry.MATERIAL.get(NamespacedKey.fromString(input.get("id").asText())), "Need a valid material");
                    int count = 1;
                    if (input.has("count")) {
                        count = input.get("count").asInt();
                    }
                    inputStack = new ItemStack(material, count);
                } else {
                    inputStack = GemVillagers.JSON_MAPPER.treeToValue(input, PlayerSkull.class).cloneOriginal();
                }
                ObjectNode output = (ObjectNode) objectNode.get("output");
                ItemStack outputStack = GemVillagers.JSON_MAPPER.treeToValue(output, PlayerSkull.class).cloneOriginal();
                return new Offer(inputStack, outputStack);
            }
        }
    }
}
