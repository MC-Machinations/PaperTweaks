/*
 * GNU General Public License v3
 *
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
import me.machinemaker.vanillatweaks.settings.Setting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

import static me.machinemaker.vanillatweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class IntegerMenuOption<S> extends MenuOption<Integer, S> implements EditableOption<Integer> {

    protected IntegerMenuOption(@NotNull Component label, @NotNull Function<S, Integer> typeMapper, Setting<Integer, ?> setting, @NotNull Component extendedDescription, @Nullable Function<Integer, ClickEvent> previewAction) {
        super(label, typeMapper, setting, extendedDescription, previewAction);
    }

    @Override
    public @NotNull Component build(@NotNull S object, @NotNull String commandPrefix) {
        final List<Component> components = Lists.newArrayList(createClickComponent(this.selected(object), commandPrefix), space());

        this.previewAction().ifPresent(previewAction -> {
            components.add(Previewable.createPreviewComponent(this.label(), previewAction.apply(this.selected(object))));
            components.add(space());
        });

        components.addAll(List.of(
                this.label(),
                space(),
                translatable("commands.config.current-value", GRAY, text(this.selected(object))),
                newline()
        ));

        return join(components.toArray(new ComponentLike[0]));
    }

    @Override
    public @NotNull Component label() {
        return super.label();
    }

    @Override
    public @NotNull Component defaultValueDescription() {
        return text(this.setting().defaultValue());
    }

    @Override
    public Component validations() {
        return this.setting().validations();
    }

    public static <S> @NotNull IntegerMenuOption<S> of(@NotNull String labelKey, @NotNull Function<S, Integer> typeMapper, @NotNull Setting<Integer, ?> setting) {
        return new Builder<>(translatable(labelKey), typeMapper, setting).build();
    }

    public static <S> @NotNull Builder<S> newBuilder(@NotNull String labelKey, @NotNull Function<S, Integer> typeMapper, @NotNull Setting<Integer, ?> setting) {
        return new Builder<>(translatable(labelKey), typeMapper, setting);
    }

    public static class Builder<S> extends MenuOption.Builder<Integer, IntegerMenuOption<S>, S, Builder<S>> {

        protected Builder(@NotNull Component label, @NotNull Function<S, Integer> typeMapper, @NotNull Setting<Integer, ?> setting) {
            super(label, typeMapper, setting);
        }

        @Override
        public @NotNull IntegerMenuOption<S> build() {
            return new IntegerMenuOption<>(
                    this.getLabel(),
                    this.getTypeMapper(),
                    this.getSetting(),
                    this.getExtendedDescription(),
                    this.getPreviewAction()
            );
        }
    }
}
