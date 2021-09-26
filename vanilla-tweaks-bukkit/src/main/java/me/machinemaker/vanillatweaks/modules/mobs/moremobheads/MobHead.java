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
package me.machinemaker.vanillatweaks.modules.mobs.moremobheads;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import me.machinemaker.vanillatweaks.config.Mixins;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

class MobHead {

    private final String lootTable;
    private final String name;
    private final ItemStack skull;
    private final boolean needsPlayer;
    private final boolean requiresCustomization;
    private final float chance;
    private final float lootingMultiplier;
    private Predicate<? extends LivingEntity> predicate;

    @JsonCreator
    MobHead(@JsonProperty("tableName") String lootTable, UUID uuid, String name, String texture, boolean needsPlayer, boolean requiresCustomization, float chance, float lootingMultiplier) {
        this.lootTable = lootTable;
        this.name = name;
        this.skull = VTUtils.getSkullWithGameProfileName(name, uuid, texture);
        this.needsPlayer = needsPlayer;
        this.requiresCustomization = requiresCustomization;
        this.chance = chance;
        this.lootingMultiplier = lootingMultiplier;
    }

    public String lootTable() {
        return lootTable;
    }

    public String name() {
        return name;
    }

    public @NotNull ItemStack createSkull() {
        return skull.clone();
    }

    public boolean needsPlayer() {
        return needsPlayer;
    }

    public boolean requiresCustomization() {
        return requiresCustomization;
    }

    public float chance() {
        return chance;
    }

    public float lootingMultiplier() {
        return lootingMultiplier;
    }

    @SuppressWarnings("unchecked")
    public <E extends LivingEntity> boolean test(@NotNull E entity) {
        if (this.predicate == null) {
            return true;
        }
        return ((Predicate<E>) this.predicate).test(entity);
    }

    public boolean chance(int lootingLevel) {
        return ThreadLocalRandom.current().nextDouble() < chance + (lootingLevel * this.lootingMultiplier);
    }

    public void predicate(@NotNull Predicate<? extends LivingEntity> predicate) {
        this.predicate = predicate;
    }

    private static final ObjectMapper MAPPER = Mixins.registerMixins(new ObjectMapper().registerModule(new ParameterNamesModule()));

    @SuppressWarnings("unchecked")
    static Multimap<Class<? extends LivingEntity>, MobHead> createMobHeadMap(@NotNull ClassLoader loader) {
        List<MobHead> heads;
        try {
            heads = MAPPER.readValue(loader.getResourceAsStream("data/more_mob_heads.json"), new TypeReference<List<MobHead>>() {});
        } catch (IOException e) {
            MoreMobHeads.LOGGER.error("Could not load mob heads from data/more_mob_heads.json. This module will not work properly", e);
            return Multimaps.unmodifiableMultimap(ArrayListMultimap.create());
        }
        Multimap<Class<? extends LivingEntity>, MobHead> mobHeadMap = ArrayListMultimap.create();
        for (MobHead head : heads) {
            String key = head.lootTable.startsWith("sheep") ? "sheep" : head.lootTable.split("\\.")[0];
            EntityType type = Registry.ENTITY_TYPE.get(NamespacedKey.minecraft(key));
            if (type == null || type.getEntityClass() == null) {
                MoreMobHeads.LOGGER.warn(head.lootTable + " could not be turned into an EntityType");
                continue;
            }
            if (!LivingEntity.class.isAssignableFrom(type.getEntityClass())) {
                MoreMobHeads.LOGGER.warn(type + " is not a living entity");
                continue;
            }
            MobHeadCustomizations.addCustomizations(head);
            mobHeadMap.put((Class<? extends LivingEntity>) type.getEntityClass(), head);
        }

        return Multimaps.unmodifiableMultimap(mobHeadMap);
    }
}
