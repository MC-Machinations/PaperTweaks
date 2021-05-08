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
package me.machinemaker.vanillatweaks.pdc.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ComponentListDataType implements PersistentDataType<String, List<Component>> {

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull Class<List<Component>> getComplexType() {
        return (Class<List<Component>>) GenericTypeReflector.erase(new TypeToken<List<Component>>() {}.getType());
    }

    @Override
    public @NotNull String toPrimitive(@NotNull List<Component> complex, @NotNull PersistentDataAdapterContext context) {
        JsonArray array = new JsonArray();
        for (Component component : complex) {
            array.add(PaperComponents.gsonSerializer().serializeToTree(component));
        }
        return ComponentDataType.GSON.toJson(array);
    }

    @Override
    public @NotNull List<Component> fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        JsonArray array = ComponentDataType.GSON.fromJson(primitive, JsonArray.class);
        List<Component> components = new ArrayList<>();
        for (JsonElement element : array) {
            components.add(PaperComponents.gsonSerializer().deserializeFromTree(element));
        }
        return components;
    }
}
