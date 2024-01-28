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

import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.EnumArgument;
import java.util.function.Supplier;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.pdc.DataTypes;
import me.machinemaker.papertweaks.pdc.types.EnumDataType;
import me.machinemaker.papertweaks.settings.ModuleSetting;
import me.machinemaker.papertweaks.settings.SettingKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public record PlayerSetting<T>(SettingKey<T> settingKey, PersistentDataType<?, T> dataType, Supplier<T> defaultSupplier, ArgumentParser<CommandDispatcher, T> argumentParser) implements ModuleSetting<T, Player> {

    public static PlayerSetting<Boolean> ofBoolean(final SettingKey<Boolean> key, final Supplier<Boolean> supplier) {
        return of(key, DataTypes.BOOLEAN, supplier, new BooleanArgument.BooleanParser<>(false));
    }

    public static <E extends Enum<E>> PlayerSetting<E> ofEnum(final SettingKey<E> key, final Class<E> classOfE, final Supplier<E> defaultSupplier) {
        return of(key, EnumDataType.of(classOfE), defaultSupplier, new EnumArgument.EnumParser<>(classOfE));
    }

    private static <S> PlayerSetting<S> of(final SettingKey<S> key, final PersistentDataType<?, S> dataType, final Supplier<S> defaultSupplier, final ArgumentParser<CommandDispatcher, S> argumentParser) {
        return new PlayerSetting<>(key, dataType, defaultSupplier, argumentParser);
    }

    @Override
    public @Nullable T get(final Player holder) {
        final PersistentDataContainer pdc = holder.getPersistentDataContainer();
        return pdc.get(this.settingKey.key(), this.dataType);
    }

    @Override
    public void set(final Player holder, final T value) {
        holder.getPersistentDataContainer().set(this.settingKey.key(), this.dataType, value);
    }

    @Override
    public Class<T> valueType() {
        return this.dataType.getComplexType();
    }

    @Override
    public T defaultValue() {
        return this.defaultSupplier().get();
    }
}
