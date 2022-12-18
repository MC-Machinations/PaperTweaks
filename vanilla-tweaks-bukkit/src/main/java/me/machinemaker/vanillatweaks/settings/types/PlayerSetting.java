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
import cloud.commandframework.arguments.standard.EnumArgument;
import java.util.function.Supplier;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.pdc.DataTypes;
import me.machinemaker.vanillatweaks.pdc.types.EnumDataType;
import me.machinemaker.vanillatweaks.settings.Setting;
import me.machinemaker.vanillatweaks.settings.SettingWrapper;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PlayerSetting<T>(@NotNull NamespacedKey settingKey, @NotNull PersistentDataType<?, T> dataType, @NotNull Supplier<@NotNull T> defaultSupplier, @NotNull ArgumentParser<CommandDispatcher, T> argumentParser) implements Setting<T, Player> {

    public static PlayerSetting<Boolean> ofBoolean(@NotNull SettingWrapper.PDC<Boolean> wrapper, @NotNull Supplier<Boolean> supplier) {
        return of(wrapper, DataTypes.BOOLEAN, supplier, new BooleanArgument.BooleanParser<>(false));
    }

    public static <E extends Enum<E>> PlayerSetting<E> ofEnum(@NotNull SettingWrapper.PDC<E> wrapper, @NotNull Class<E> classOfE, @NotNull Supplier<E> defaultSupplier) {
        return of(wrapper, EnumDataType.of(classOfE), defaultSupplier, new EnumArgument.EnumParser<>(classOfE));
    }

    private static <S> PlayerSetting<S> of(@NotNull SettingWrapper.PDC<S> wrapper, @NotNull PersistentDataType<?, S> dataType, @NotNull Supplier<S> defaultSupplier, @NotNull ArgumentParser<CommandDispatcher, S> argumentParser) {
        return new PlayerSetting<>(wrapper.key, dataType, defaultSupplier, argumentParser).loadWrapper(wrapper);
    }

    @Override
    public @Nullable T get(@NotNull Player holder) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        return pdc.get(this.settingKey, this.dataType);
    }

    @Override
    public void set(@NotNull Player holder, T value) {
        holder.getPersistentDataContainer().set(this.settingKey, this.dataType, value);
    }

    @Override
    public @NotNull Class<T> valueType() {
        return this.dataType.getComplexType();
    }

    @Override
    public @NotNull T defaultValue() {
        return this.defaultSupplier().get();
    }

    @Override
    public @NotNull String indexKey() {
        return this.settingKey.getKey();
    }
}
