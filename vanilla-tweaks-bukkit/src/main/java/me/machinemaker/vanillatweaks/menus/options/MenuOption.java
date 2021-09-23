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

import me.machinemaker.vanillatweaks.menus.parts.MenuPart;
import me.machinemaker.vanillatweaks.menus.parts.MenuPartLike;
import me.machinemaker.vanillatweaks.settings.Setting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.translatable;

public abstract class MenuOption<T, S> implements MenuPart<S>, Option {

    private final Component label;
    private final Function<S, T> typeMapper;
    private final Setting<T, ?> setting;
    private final Component extendedDescription;
    private final Function<T, ClickEvent> previewAction;

    protected MenuOption(@NotNull Component label, @NotNull Function<S, T> typeMapper, Setting<T, ?> setting, @NotNull Component extendedDescription, @Nullable Function<T, ClickEvent> previewAction) {
        this.label = label;
        this.typeMapper = typeMapper;
        this.setting = setting;
        this.extendedDescription = extendedDescription;
        this.previewAction = previewAction;
    }

    protected @NotNull Component label() {
        return this.label;
    }

    protected @NotNull Setting<T, ?> setting() {
        return this.setting;
    }

    @Override
    public @NotNull String optionKey() {
        return this.setting.indexKey();
    }

    protected @NotNull T selected(@NotNull S object) {
        return this.typeMapper.apply(object);
    }

    public @NotNull Component extendedDescription() {
        return this.extendedDescription;
    }

    protected @NotNull Optional<Function<T, ClickEvent>> previewAction() {
        return Optional.ofNullable(this.previewAction);
    }

    public abstract static class Builder<T, O extends MenuOption<T, S>, S, B extends Builder<T, O, S, B>> implements MenuPartLike<S> {

        private final Component label;
        private final Function<S, T> typeMapper;
        private final Setting<T, ?> setting;
        private Component extendedDescription = Component.empty();
        private Function<T, ClickEvent> previewAction;

        protected Builder(@NotNull Component label, @NotNull Function<S, T> typeMapper, @NotNull Setting<T, ?> setting) {
            this.label = label;
            this.typeMapper = typeMapper;
            this.setting = setting;
        }

        protected @NotNull Component getLabel() {
            return this.label;
        }

        protected @NotNull Function<S, T> getTypeMapper() {
            return this.typeMapper;
        }

        protected @NotNull Setting<T, ?> getSetting() {
            return this.setting;
        }

        protected @NotNull Component getExtendedDescription() {
            return this.extendedDescription;
        }

        protected @Nullable Function<T, ClickEvent> getPreviewAction() {
            return this.previewAction;
        }

        @SuppressWarnings("unchecked")
        protected final B self() {
            return (B) this;
        }

        public B extendedDescription(@NotNull Component extendedDescription) {
            this.extendedDescription = extendedDescription;
            return self();
        }

        public B extendedDescription(@NotNull String extendedDescriptionKey) {
            return extendedDescription(translatable(extendedDescriptionKey));
        }

        public B previewAction(@NotNull Function<T, ClickEvent> previewAction) {
            this.previewAction = previewAction;
            return self();
        }

        public abstract @NotNull O build();

        @Override
        public @NotNull MenuPart<S> asMenuPart() {
            return this.build();
        }
    }
}
