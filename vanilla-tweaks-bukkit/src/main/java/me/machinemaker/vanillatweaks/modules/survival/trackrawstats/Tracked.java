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
package me.machinemaker.vanillatweaks.modules.survival.trackrawstats;

import cloud.commandframework.types.tuples.Pair;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static me.machinemaker.vanillatweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

abstract class Tracked implements ComponentLike {

    private final String name;
    private final String criteria;
    private final String displayName;
    private Objective objective;

    protected Tracked(String name, String stat, String displayName) {
        Preconditions.checkArgument(name.length() <= 16, name + " must be 16 characters of less");
        this.name = name;
        this.criteria = stat;
        this.displayName = displayName;
    }

    boolean register(@NotNull Scoreboard board) {
        this.objective = board.getObjective(this.name);
        if (this.objective == null) {
            this.objective = board.registerNewObjective(this.name, this.criteria, this.displayName);
            return true;
        }
        return false;
    }

    public String name() {
        return name;
    }

    public String criteria() {
        return criteria;
    }

    public String displayName() {
        return displayName;
    }

    public @NotNull Objective objective() {
        if (this.objective == null) {
            throw new IllegalArgumentException("unregistered");
        }
        return objective;
    }

    abstract int constructValue(JsonObject object);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tracked that = (Tracked) o;
        return name.equals(that.name) && criteria.equals(that.criteria) && displayName.equals(that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, criteria, displayName);
    }

    abstract static class Criteria extends Tracked {

        private final String translationKey;

        protected Criteria(String name, String criteria, String displayName, String translationKey) {
            super(name, criteria, displayName);
            this.translationKey = translationKey;
        }

        @Override
        public @NotNull Component asComponent() {
            return translatable(this.translationKey, GOLD);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            Criteria criteria = (Criteria) o;
            return translationKey.equals(criteria.translationKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), translationKey);
        }

    }

    static class Statistic extends Tracked {

        private final String type;
        private final String value;

        public Statistic(String name, String stat, String displayName) {
            super(name, stat, displayName);
            Preconditions.checkArgument(stat.contains(":"), stat + " is not a statistic");
            this.type = stat.split(":")[0];
            this.value = stat.split(":")[1];
        }

        public String type() {
            return type;
        }

        public String value() {
            return value;
        }

        public boolean isCustom() {
            return this.type.equals("minecraft.custom");
        }

        @Override
        public @NotNull Component asComponent() {
            if (this.isCustom()) {
                return translatable("stat." + this.value, GOLD);
            } else {
                Pair<String, String> typeKey = switch (this.type.split("\\.")[1]) {
                    case "crafted", "used", "broken" -> Pair.of("stat_type." + this.type, "item.");
                    case "killed" -> Pair.of("modules.track-raw-stats.stat-type.minecraft.killed", "entity.");
                    case "killed_by" -> Pair.of("modules.track-raw-stats.stat-type.minecraft.killed-by", "entity.");
                    case "mined" -> Pair.of("modules.track-raw-stats.stat-type.minecraft.mined", "block.");
                    default -> throw new IllegalArgumentException();
                };

                return join(
                        translatable(typeKey.getFirst()),
                        text(": "),
                        translatable(typeKey.getSecond() + this.value)
                ).color(GOLD);
            }
        }

        @Override
        int constructValue(JsonObject object) {
            var typeElement = object.get(this.type.replace('.', ':'));
            if (typeElement instanceof JsonObject typeObj) {
                JsonElement valueElement = typeObj.get(this.value.replace('.', ':'));
                if (valueElement instanceof JsonPrimitive valuePrimitive && valuePrimitive.isNumber()) {
                    return valuePrimitive.getAsInt();
                }
            }
            return -1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            Statistic statistic = (Statistic) o;
            return type.equals(statistic.type) && value.equals(statistic.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), type, value);
        }
    }
}
