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
package me.machinemaker.papertweaks.modules.survival.trackrawstats;

import cloud.commandframework.types.tuples.Pair;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static me.machinemaker.papertweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

abstract class Tracked implements ComponentLike {

    private final String name;
    private final Criteria criteria;
    private final String displayName;
    private @MonotonicNonNull Objective objective;

    protected Tracked(final String name, final String stat, final String displayName) {
        Preconditions.checkArgument(name.length() <= 16, name + " must be 16 characters of less");
        this.name = name;
        this.criteria = Criteria.create(stat);
        this.displayName = displayName;
    }

    boolean register(final Scoreboard board) {
        this.objective = board.getObjective(this.name);
        if (this.objective == null) {
            this.objective = board.registerNewObjective(this.name, this.criteria, this.displayName);
            return true;
        }
        return false;
    }

    public String name() {
        return this.name;
    }

    public Criteria criteria() {
        return this.criteria;
    }

    public String displayName() {
        return this.displayName;
    }

    public Objective objective() {
        if (this.objective == null) {
            throw new IllegalArgumentException("unregistered");
        }
        return this.objective;
    }

    abstract int constructValue(JsonObject object);

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Tracked that = (Tracked) o;
        return this.name.equals(that.name) && this.criteria.equals(that.criteria) && this.displayName.equals(that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.criteria, this.displayName);
    }

    abstract static class CriteriaType extends Tracked {

        private final String translationKey;

        protected CriteriaType(final String name, final String criteria, final String displayName, final String translationKey) {
            super(name, criteria, displayName);
            this.translationKey = translationKey;
        }

        @Override
        public Component asComponent() {
            return translatable(this.translationKey, GOLD);
        }

        @Override
        public boolean equals(final @Nullable Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final CriteriaType criteriaType = (CriteriaType) o;
            return this.translationKey.equals(criteriaType.translationKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), this.translationKey);
        }

    }

    static class StatisticType extends Tracked {

        private final String type;
        private final String value;

        public StatisticType(final String name, final String stat, final String displayName) {
            super(name, stat, displayName);
            Preconditions.checkArgument(stat.contains(":"), stat + " is not a statistic");
            this.type = stat.split(":")[0];
            this.value = stat.split(":")[1];
        }

        public String type() {
            return this.type;
        }

        public String value() {
            return this.value;
        }

        public boolean isCustom() {
            return this.type.equals("minecraft.custom");
        }

        @Override
        public Component asComponent() {
            if (this.isCustom()) {
                return translatable("stat." + this.value, GOLD);
            } else {
                final Pair<String, String> typeKey = switch (this.type.split("\\.")[1]) {
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
        int constructValue(final JsonObject object) {
            final JsonElement typeElement = object.get(this.type.replace('.', ':'));
            if (typeElement instanceof JsonObject typeObj) {
                final JsonElement valueElement = typeObj.get(this.value.replace('.', ':'));
                if (valueElement instanceof JsonPrimitive valuePrimitive && valuePrimitive.isNumber()) {
                    return valuePrimitive.getAsInt();
                }
            }
            return -1;
        }

        @Override
        public boolean equals(final @Nullable Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final StatisticType statisticType = (StatisticType) o;
            return this.type.equals(statisticType.type) && this.value.equals(statisticType.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), this.type, this.value);
        }
    }
}
