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
package me.machinemaker.papertweaks.pdc.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ComponentListDataType implements PersistentDataType<String, List<Component>> {

    private static final TypeToken<List<Component>> COMPONENT_LIST = new TypeToken<List<Component>>() {};

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull Class<List<Component>> getComplexType() {
        return (Class<List<Component>>) GenericTypeReflector.erase(COMPONENT_LIST.getType());
    }

    @Override
    public @NotNull String toPrimitive(@NotNull final List<Component> complex, @NotNull final PersistentDataAdapterContext context) {
        final JsonArray array = new JsonArray();
        for (final Component component : complex) {
            array.add(GsonComponentSerializer.gson().serializeToTree(component));
        }
        return ComponentDataType.GSON.toJson(array);
    }

    @Override
    public @NotNull List<Component> fromPrimitive(@NotNull final String primitive, @NotNull final PersistentDataAdapterContext context) {
        final JsonArray array = ComponentDataType.GSON.fromJson(primitive, JsonArray.class);
        final List<Component> components = new ArrayList<>();
        for (final JsonElement element : array) {
            try {
                components.add(GsonComponentSerializer.gson().deserializeFromTree(element));
            } catch (final JsonParseException ex) {
                // fallback on legacy
                components.add(LegacyComponentSerializer.legacySection().deserialize(element.getAsString()));
            }
        }
        return components;
    }
}
