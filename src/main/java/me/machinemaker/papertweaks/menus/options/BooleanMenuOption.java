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

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Function;
import me.machinemaker.papertweaks.adventure.Components;
import me.machinemaker.papertweaks.menus.parts.Previewable;
import me.machinemaker.papertweaks.menus.parts.clicks.ToggleOption;
import me.machinemaker.papertweaks.settings.Setting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class BooleanMenuOption<S> extends MenuOption<Boolean, S> implements ToggleOption<Boolean> {

    private BooleanMenuOption(final Component label, final Function<S, Boolean> typeMapper, final Setting<Boolean, ?> setting, final Component extendedDescription, final @Nullable Function<Boolean, ClickEvent> previewAction) {
        super(label, typeMapper, setting, extendedDescription, previewAction);
    }

    public static <S> BooleanMenuOption<S> of(final String labelKey, final Function<S, Boolean> typeMapper, final Setting<Boolean, ?> setting) {
        return new Builder<>(translatable(labelKey), typeMapper, setting).build();
    }

    public static <S> Builder<S> newBuilder(final Component label, final Function<S, Boolean> typeMapper, final Setting<Boolean, ?> setting) {
        return new Builder<>(label, typeMapper, setting);
    }

    public static <S> Builder<S> newBuilder(final String labelKey, final Function<S, Boolean> typeMapper, final Setting<Boolean, ?> setting) {
        return new Builder<>(translatable(labelKey), typeMapper, setting);
    }

    public static <S> BooleanMenuOption<S> of(final String labelKey, final Setting<Boolean, S> setting) {
        return of(labelKey, setting::getOrDefault, setting);
    }

    public static <S> Builder<S> newBuilder(final Component label, final Setting<Boolean, S> setting) {
        return newBuilder(label, setting::getOrDefault, setting);
    }

    public static <S> Builder<S> newBuilder(final String labelKey, final Setting<Boolean, S> setting) {
        return newBuilder(translatable(labelKey), setting::getOrDefault, setting);
    }

    @Override
    public Component build(final S object, final String commandPrefix) {
        final List<Component> components = Lists.newArrayList(
                this.createClickComponent(Boolean.TRUE.equals(this.selected(object)), commandPrefix),
                text(' ')
        );

        this.previewAction().ifPresent(previewAction -> {
            components.add(Previewable.createPreviewComponent(this.label(), previewAction.apply(this.selected(object))));
            components.add(text(' '));
        });

        components.add(this.label());
        components.add(newline());
        return Components.join(components.toArray(new ComponentLike[0]));
    }

    @Override
    public Component label() {
        return super.label();
    }

    @Override
    public String clickCommandValue(final Boolean selected) {
        return Boolean.toString(!selected);
    }

    @Override
    public boolean isSelected(final Boolean selected) {
        return selected;
    }

    @Override
    public Component defaultValueDescription() {
        return translatable("commands.config.default-value.bool." + this.setting().defaultValue());
    }

    public static class Builder<S> extends MenuOption.Builder<Boolean, BooleanMenuOption<S>, S, Builder<S>> {

        protected Builder(final Component label, final Function<S, Boolean> typeMapper, final Setting<Boolean, ?> setting) {
            super(label, typeMapper, setting);
        }

        @Override
        public BooleanMenuOption<S> build() {
            return new BooleanMenuOption<>(
                    this.getLabel(),
                    this.getTypeMapper(),
                    this.getSetting(),
                    this.getExtendedDescription(),
                    this.getPreviewAction()
            );
        }
    }
}
