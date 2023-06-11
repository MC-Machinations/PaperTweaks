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
package me.machinemaker.papertweaks.menus.options;

import java.util.function.Function;
import me.machinemaker.papertweaks.settings.Setting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class DoubleMenuOption<S> extends NumberMenuOption<Double, S> implements EditableOption<Double> {

    protected DoubleMenuOption(final Component label, final Function<S, Double> typeMapper, final Setting<Double, ?> setting, final Component extendedDescription, final @Nullable Function<Double, ClickEvent> previewAction) {
        super(label, typeMapper, setting, extendedDescription, previewAction);
    }

    public static <S> DoubleMenuOption<S> of(final String labelKey, final Function<S, Double> typeMapper, final Setting<Double, ?> setting) {
        return new Builder<>(translatable(labelKey), typeMapper, setting).build();
    }

    public static <S> DoubleMenuOption<S> of(final String labelKey, final Setting<Double, S> setting) {
        return new Builder<>(translatable(labelKey), setting::getOrDefault, setting).build();
    }

    public static <S> Builder<S> builder(final String labelKey, final Function<S, Double> typeMapper, final Setting<Double, ?> setting) {
        return new Builder<>(translatable(labelKey), typeMapper, setting);
    }

    public static <S> Builder<S> builder(final String labelKey, final Setting<Double, S> setting) {
        return new Builder<>(translatable(labelKey), setting::getOrDefault, setting);
    }

    @Override
    protected Component convertToComponent(final Double value) {
        return text(value);
    }

    public static class Builder<S> extends MenuOption.Builder<Double, DoubleMenuOption<S>, S, Builder<S>> {

        protected Builder(final Component label, final Function<S, Double> typeMapper, final Setting<Double, ?> setting) {
            super(label, typeMapper, setting);
        }

        @Override
        public DoubleMenuOption<S> build() {
            return new DoubleMenuOption<>(
                    this.getLabel(),
                    this.getTypeMapper(),
                    this.getSetting(),
                    this.getExtendedDescription(),
                    this.getPreviewAction()
            );
        }
    }
}
