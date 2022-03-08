/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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
package me.machinemaker.vanillatweaks.config;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.VanillaTweaksConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTables;

import java.io.IOException;

public final class Mixins {

    private Mixins() {
    }

    public static ObjectMapper registerMixins(ObjectMapper mapper) {
        mapper.addMixIn(NamespacedKey.class, NamespaceKey.class);
        mapper.addMixIn(Material.class, MaterialMixIn.class);
        mapper.addMixIn(EntityType.class, EntityTypeMixIn.class);
        mapper.addMixIn(World.class, WorldMixIn.class);
        mapper.addMixIn(LootTables.class, Keyed.class);
        return mapper;
    }

    public abstract static class Keyed {

        @JsonValue
        public abstract NamespacedKey getKey();

    }
    public abstract static class KeyedField {

        @JsonValue
        private NamespacedKey key;

    }
    public abstract static class NamespaceKey {

        @JsonValue
        public abstract String toString();

    }

    @JsonDeserialize(using = MaterialMixIn.Deserializer.class)
    public abstract static class MaterialMixIn extends KeyedField {

        static class Deserializer extends JsonDeserializer<Material> {
            @Override
            public Material deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                final Material material = Material.matchMaterial(value);
                if (material == null) {
                    throw ctxt.weirdStringException(value, Material.class, "not one of the values accepted for Material enum class");
                }
                return material;
            }
        }
    }

    @JsonDeserialize(using = EntityTypeMixIn.Deserializer.class)
    public abstract static class EntityTypeMixIn extends KeyedField {

        static class Deserializer extends JsonDeserializer<EntityType> {
            @Override
            public EntityType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                NamespacedKey key = ctxt.readValue(p, NamespacedKey.class);
                if (key != null) {
                    final EntityType type = Registry.ENTITY_TYPE.get(key);
                    if (type != null) {
                        return type;
                    }
                }
                throw ctxt.weirdStringException(value, EntityType.class, "not one of the values accepted for EntityType enum class");
            }
        }
    }

    @JsonDeserialize(using = WorldMixIn.Deserializer.class)
    public abstract static class WorldMixIn {

        @JsonValue
        public abstract String getName();

        static class Deserializer extends JsonDeserializer<World> {
            @Override
            public World deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                if (VanillaTweaks.RAN_CONFIG_MIGRATIONS && value.equals("world") && !Bukkit.getWorlds().get(0).getName().equals("world")) {
                    value = Bukkit.getWorlds().get(0).getName();
                }
                World world = Bukkit.getWorld(value);
                if (world != null) {
                    return world;
                }
                throw ctxt.weirdStringException(value, World.class, "not a valid, loaded world");
            }
        }
    }
}
