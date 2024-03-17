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
package me.machinemaker.papertweaks.settings.types;

import java.util.Objects;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.standard.BooleanParser;
import org.incendo.cloud.parser.standard.IntegerParser;

public record GameRuleSetting<T>(GameRule<T> gameRule, ArgumentParser<CommandDispatcher, T> argumentParser) implements Setting<T, World> {

    private static final World OVERWORLD = Objects.requireNonNull(Bukkit.getWorlds().get(0), "no overworld found");

    public static GameRuleSetting<Boolean> ofBoolean(final GameRule<Boolean> gameRule) {
        return new GameRuleSetting<>(gameRule, new BooleanParser<>(false));
    }

    public static GameRuleSetting<Integer> ofInt(final GameRule<Integer> gameRule, final int min, final int max) {
        return new GameRuleSetting<>(gameRule, new IntegerParser<>(min, max));
    }

    public static GameRuleSetting<Integer> ofInt(final GameRule<Integer> gameRule, final int min) {
        return ofInt(gameRule, min, Integer.MAX_VALUE);
    }

    public static GameRuleSetting<Integer> ofInt(final GameRule<Integer> gameRule) {
        return ofInt(gameRule, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public @Nullable T get(final World world) {
        return world.getGameRuleValue(this.gameRule);
    }

    @Override
    public void set(final World world, final T value) {
        world.setGameRule(this.gameRule, value);
    }

    @Override
    public Class<T> valueType() {
        return this.gameRule.getType();
    }

    @Override
    public T defaultValue() {
        return Objects.requireNonNull(OVERWORLD.getGameRuleDefault(this.gameRule), "how does a gamerule have a null default, stupid bukkit"); // All worlds have the same default values
    }

    @Override
    public String indexKey() {
        return this.gameRule.getName();
    }
}
