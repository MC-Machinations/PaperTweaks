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
package me.machinemaker.vanillatweaks.menus.options;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import me.machinemaker.vanillatweaks.adventure.Components;
import me.machinemaker.vanillatweaks.menus.parts.enums.MenuEnum;
import me.machinemaker.vanillatweaks.settings.Setting;
import net.kyori.adventure.text.Component;

public class SelectableEnumMenuOption<E extends Enum<E> & MenuEnum<E>, S> extends MenuOption<E, S> {

    private final String labelKey;
    private final List<E> options;

    private SelectableEnumMenuOption(final Function<S, E> typeMapper, final Setting<E, ?> setting, final String labelKey, final Class<E> classOfE) {
        super(Component.empty(), typeMapper, setting, Component.empty(), null);
        this.labelKey = labelKey;
        this.options = Arrays.stream(classOfE.getEnumConstants()).toList();
    }

    public static <E extends Enum<E> & MenuEnum<E>, S> Builder<E, S> builder(final Class<E> classOfE, final String labelKey, final Function<S, E> typeMapper, final Setting<E, ?> setting) {
        return new Builder<>(classOfE, labelKey, typeMapper, setting);
    }

    public static <E extends Enum<E> & MenuEnum<E>, S> Builder<E, S> builder(final Class<E> classOfE, final String labelKey, final Setting<E, S> setting) {
        return builder(classOfE, labelKey, setting::getOrDefault, setting);
    }

    public static <E extends Enum<E> & MenuEnum<E>, S> SelectableEnumMenuOption<E, S> of(final Class<E> classOfE, final String labelKey, final Function<S, E> typeMapper, final Setting<E, ?> setting) {
        return builder(classOfE, labelKey, typeMapper, setting).build();
    }

    public static <E extends Enum<E> & MenuEnum<E>, S> SelectableEnumMenuOption<E, S> of(final Class<E> classOfE, final String labelKey, final Setting<E, S> setting) {
        return of(classOfE, labelKey, setting::getOrDefault, setting);
    }

    @Override
    public Component build(final S object, final String commandPrefix) {
        final Component[] components = new Component[this.options.size()];
        for (int i = 0; i < this.options.size(); i++) {
            components[i] = this.options.get(i).build(this.selected(object), this.labelKey, commandPrefix, this.optionKey());
        }
        return Components.join(components);
    }

    public static class Builder<E extends Enum<E> & MenuEnum<E>, S> extends MenuOption.Builder<E, SelectableEnumMenuOption<E, S>, S, Builder<E, S>> {

        private final Class<E> classOfE;
        private String labelKey;

        private Builder(final Class<E> classOfE, final String labelKey, final Function<S, E> typeMapper, final Setting<E, ?> setting) {
            super(Component.empty(), typeMapper, setting);
            this.classOfE = classOfE;
            this.labelKey = labelKey;
        }

        public Class<E> getClassOfE() {
            return this.classOfE;
        }

        public String getLabelKey() {
            return this.labelKey;
        }

        public Builder<E, S> labelKey(final String labelKey) {
            this.labelKey = labelKey;
            return this;
        }

        @Override
        public SelectableEnumMenuOption<E, S> build() {
            return new SelectableEnumMenuOption<>(
                    this.getTypeMapper(),
                    this.getSetting(),
                    this.getLabelKey(),
                    this.getClassOfE()
            );
        }
    }
}
