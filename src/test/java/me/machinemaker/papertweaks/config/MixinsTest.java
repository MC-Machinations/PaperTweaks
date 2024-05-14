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
package me.machinemaker.papertweaks.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTables;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MixinsTest {

    static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR));

    @BeforeAll
    static void beforeEach() {
        Mixins.registerMixins(mapper);
        final World mockWorld = mock(World.class);
        when(mockWorld.getName()).thenReturn("world");
        final Server mockServer = mock(Server.class);
        when(mockServer.getWorld("world")).thenReturn(mockWorld);
        when(mockServer.getLogger()).thenReturn(Logger.getLogger("MockServer"));
        when(mockServer.getRegistry(any())).thenAnswer(invocation -> {
            return new Registry<>() {
                @Override
                public @Nullable Keyed get(final NamespacedKey key) {
                    return null;
                }

                @Override
                public Stream<Keyed> stream() {
                    return Stream.empty();
                }

                @Override
                public Iterator<Keyed> iterator() {
                    return Collections.emptyIterator();
                }
            };
        });
        Bukkit.setServer(mockServer);
    }

    @Test
    void testMaterialMixin() throws JsonProcessingException {
        assertEquals(Material.GRINDSTONE, mapper.readValue("minecraft:grindstone", Material.class));
        assertEquals(Material.GRINDSTONE, mapper.readValue("GRINDstone", Material.class));
        assertEquals("\"minecraft:grindstone\"\n", mapper.writeValueAsString(Material.GRINDSTONE));
        assertThrows(InvalidFormatException.class, () -> mapper.readValue("not_a_material", Material.class));
    }

    @Test
    @Disabled // TODO No RegistryAccess found (probably solution is to use paperweight-userdev for testing)
    void testEntityTypeMixin() throws JsonProcessingException {
        assertEquals(EntityType.BAT, mapper.readValue("minecraft:bat", EntityType.class));
        assertEquals(EntityType.BAT, mapper.readValue("bat", EntityType.class));
        assertEquals("\"minecraft:bat\"\n", mapper.writeValueAsString(EntityType.BAT));
        assertThrows(InvalidFormatException.class, () -> mapper.readValue("not_an_entity", EntityType.class));
    }

    @Test
    void testNamespacedKeyMixin() throws JsonProcessingException {
        assertEquals(NamespacedKey.fromString("test_key:test_path"), mapper.readValue("test_key:test_path", NamespacedKey.class));
        assertEquals(NamespacedKey.fromString("stone"), mapper.readValue("minecraft:stone", NamespacedKey.class));
    }

    @Test
    void testWorldMixin() throws JsonProcessingException {
        assertEquals(Bukkit.getWorld("world"), mapper.readValue("world", World.class));
        assertThrows(InvalidFormatException.class, () -> mapper.readValue("not_a_world", World.class));
    }

    @Test
    void testLootTablesMixin() throws JsonProcessingException {
        assertEquals(LootTables.ABANDONED_MINESHAFT, mapper.readValue("minecraft:chests/abandoned_mineshaft", LootTables.class));
        assertThrows(InvalidFormatException.class, () -> mapper.readValue("not_a_loot_table", LootTables.class));
    }
}
