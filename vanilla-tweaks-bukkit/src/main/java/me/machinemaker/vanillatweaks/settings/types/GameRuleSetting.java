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
package me.machinemaker.vanillatweaks.settings.types;

import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import java.util.Objects;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record GameRuleSetting<T>(@NotNull GameRule<T> gameRule, @NotNull ArgumentParser<CommandDispatcher, T> argumentParser) implements Setting<T, World> {

    private static final World OVERWORLD = Objects.requireNonNull(Bukkit.getWorlds().get(0), "no overworld found");

    public static @NotNull GameRuleSetting<Boolean> ofBoolean(@NotNull GameRule<Boolean> gameRule) {
        return new GameRuleSetting<>(gameRule, new BooleanArgument.BooleanParser<>(false));
    }

    public static @NotNull GameRuleSetting<Integer> ofInt(@NotNull GameRule<Integer> gameRule, int min, int max) {
        return new GameRuleSetting<>(gameRule, new IntegerArgument.IntegerParser<>(min, max));
    }

    public static @NotNull GameRuleSetting<Integer> ofInt(@NotNull GameRule<Integer> gameRule, int min) {
        return ofInt(gameRule, min, Integer.MAX_VALUE);
    }

    public static @NotNull GameRuleSetting<Integer> ofInt(@NotNull GameRule<Integer> gameRule) {
        return ofInt(gameRule, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public @Nullable T get(@NotNull World world) {
        return world.getGameRuleValue(this.gameRule);
    }

    @Override
    public void set(@NotNull World world, T value) {
        world.setGameRule(this.gameRule, value);
    }

    @Override
    public @NotNull Class<T> valueType() {
        return this.gameRule.getType();
    }

    @Override
    public @NotNull T defaultValue() {
        return Objects.requireNonNull(OVERWORLD.getGameRuleDefault(this.gameRule), "how does a gamerule have a null default, stupid bukkit"); // All worlds have the same default values
    }

    @Override
    public @NotNull String indexKey() {
        return this.gameRule.getName();
    }
}
