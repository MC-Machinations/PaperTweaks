/*
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
package me.machinemaker.vanillatweaks.menus.options;

import com.google.common.collect.Lists;
import me.machinemaker.vanillatweaks.menus.parts.Previewable;
import me.machinemaker.vanillatweaks.menus.parts.clicks.ToggleOption;
import me.machinemaker.vanillatweaks.settings.Setting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.TextComponent.ofChildren;

public class BooleanMenuOption<S> extends MenuOption<Boolean, S> implements ToggleOption<Boolean> {

    private BooleanMenuOption(@NotNull Component label, @NotNull Function<S, Boolean> typeMapper, @NotNull Setting<Boolean, ?> setting, @NotNull Component extendedDescription, @Nullable Function<Boolean, ClickEvent> previewAction) {
        super(label, typeMapper, setting, extendedDescription, previewAction);
    }

    @Override
    public @NotNull Component build(@NotNull S object, @NotNull String commandPrefix) {
        final List<Component> components = Lists.newArrayList(
                createClickComponent(Boolean.TRUE.equals(this.selected(object)), commandPrefix),
                text(' ')
        );

        this.previewAction().ifPresent(previewAction -> {
            components.add(Previewable.createPreviewComponent(this.label(), previewAction.apply(this.selected(object))));
            components.add(text(' '));
        });

        components.add(this.label());
        components.add(newline());
        return ofChildren(components.toArray(new ComponentLike[0]));
    }

    @Override
    public @NotNull Component label() {
        return super.label();
    }

    @Override
    public @NotNull String clickCommandValue(@NotNull Boolean selected) {
        return Boolean.toString(!selected);
    }

    @Override
    public boolean isSelected(@NotNull Boolean selected) {
        return selected;
    }

    public static <S> BooleanMenuOption<S> of(@NotNull String labelKey, @NotNull Function<S, Boolean> typeMapper, @NotNull Setting<Boolean, ?> setting) {
        return new Builder<>(translatable(labelKey), typeMapper, setting).build();
    }

    public static <S> Builder<S> newBuilder(@NotNull Component label, @NotNull Function<S, Boolean> typeMapper, @NotNull Setting<Boolean, ?> setting) {
        return new Builder<>(label, typeMapper, setting);
    }

    public static <S> Builder<S> newBuilder(@NotNull String labelKey, @NotNull Function<S, Boolean> typeMapper, @NotNull Setting<Boolean, ?> setting) {
        return new Builder<>(translatable(labelKey), typeMapper, setting);
    }

    public static class Builder<S> extends MenuOption.Builder<Boolean, BooleanMenuOption<S>, S, Builder<S>> {

        protected Builder(@NotNull Component label, @NotNull Function<S, Boolean> typeMapper, @NotNull Setting<Boolean, ?> setting) {
            super(label, typeMapper, setting);
        }

        @Override
        public @NotNull BooleanMenuOption<S> build() {
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
