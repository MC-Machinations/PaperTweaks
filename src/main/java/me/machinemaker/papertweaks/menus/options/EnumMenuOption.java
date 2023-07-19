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

import static me.machinemaker.papertweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class EnumMenuOption<E extends Enum<E>, S> extends MenuOption<E, S> implements EditableOption<E> {

    private final Function<E, Component> optionLabelFunction;

    protected EnumMenuOption(final Component label, final Function<S, E> typeMapper, final Setting<E, ?> setting, final Component extendedDescription, final Function<E, Component> optionLabelFunction) {
        super(label, typeMapper, setting, extendedDescription, null);
        this.optionLabelFunction = optionLabelFunction;
    }

    public static <E extends Enum<E>, S> Builder<E, S> builder(final String labelKey, final Function<S, E> typeMapper, final Setting<E, ?> setting) {
        return new Builder<>(translatable(labelKey), typeMapper, setting);
    }

    public static <E extends Enum<E>, S> Builder<E, S> builder(final String labelKey, final Setting<E, ? super S> setting) {
        return new Builder<>(translatable(labelKey), setting::getOrDefault, setting);
    }

    public static <E extends Enum<E>, S> EnumMenuOption<E, S> of(final String labelKey, final Function<S, E> typeMapper, final Setting<E, ?> setting) {
        return builder(labelKey, typeMapper, setting).build();
    }

    public static <E extends Enum<E>, S> EnumMenuOption<E, S> of(final String labelKey, final Setting<E, S> setting) {
        return builder(labelKey, setting::getOrDefault, setting).build();
    }

    @Override
    public Component defaultValueDescription() {
        return this.optionLabelFunction.apply(this.setting().defaultValue());
    }

    @Override
    public Component label() {
        return super.label();
    }

    @Override
    public Component build(final S object, final String commandPrefix) {
        return join(
                this.createClickComponent(this.selected(object), commandPrefix),
                space(),
                this.label(),
                space(),
                translatable("commands.config.current-value", GRAY, this.optionLabelFunction.apply(this.selected(object))),
                newline()
        );
    }

    public static class Builder<E extends Enum<E>, S> extends MenuOption.Builder<E, EnumMenuOption<E, S>, S, Builder<E, S>> {

        private Function<E, Component> optionLabelFunction = e -> text(e.name());

        private Builder(final Component label, final Function<S, E> typeMapper, final Setting<E, ?> setting) {
            super(label, typeMapper, setting);
        }

        public Builder<E, S> optionLabelFunction(final Function<E, Component> currentLabelFunction) {
            this.optionLabelFunction = currentLabelFunction;
            return this;
        }

        public Function<E, Component> getOptionLabelFunction() {
            return this.optionLabelFunction;
        }

        @Override
        public EnumMenuOption<E, S> build() {
            return new EnumMenuOption<>(
                    this.getLabel(),
                    this.getTypeMapper(),
                    this.getSetting(),
                    this.getExtendedDescription(),
                    this.getOptionLabelFunction());
        }
    }
}
