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
package me.machinemaker.papertweaks.menus.options;

import java.util.Optional;
import java.util.function.Function;
import me.machinemaker.papertweaks.menus.parts.MenuPart;
import me.machinemaker.papertweaks.menus.parts.MenuPartLike;
import me.machinemaker.papertweaks.settings.Setting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.translatable;

public abstract class MenuOption<T, S> implements MenuPart<S>, Option {

    private final Component label;
    private final Function<S, T> typeMapper;
    private final Setting<T, ?> setting;
    private final Component extendedDescription;
    private final @Nullable Function<T, ClickEvent> previewAction;

    protected MenuOption(final Component label, final Function<S, T> typeMapper, final Setting<T, ?> setting, final Component extendedDescription, final @Nullable Function<T, ClickEvent> previewAction) {
        this.label = label;
        this.typeMapper = typeMapper;
        this.setting = setting;
        this.extendedDescription = extendedDescription;
        this.previewAction = previewAction;
    }

    protected Component label() {
        return this.label;
    }

    protected Setting<T, ?> setting() {
        return this.setting;
    }

    @Override
    public String optionKey() {
        return this.setting.indexKey();
    }

    protected T selected(final S object) {
        return this.typeMapper.apply(object);
    }

    public Component extendedDescription() {
        return this.extendedDescription;
    }

    protected Optional<Function<T, ClickEvent>> previewAction() {
        return Optional.ofNullable(this.previewAction);
    }

    public abstract static class Builder<T, O extends MenuOption<T, S>, S, B extends Builder<T, O, S, B>> implements MenuPartLike<S> {

        private final Component label;
        private final Function<S, T> typeMapper;
        private final Setting<T, ?> setting;
        private Component extendedDescription = Component.empty();
        private @Nullable Function<T, ClickEvent> previewAction;

        protected Builder(final Component label, final Function<S, T> typeMapper, final Setting<T, ?> setting) {
            this.label = label;
            this.typeMapper = typeMapper;
            this.setting = setting;
        }

        public Component getLabel() {
            return this.label;
        }

        public Function<S, T> getTypeMapper() {
            return this.typeMapper;
        }

        public Setting<T, ?> getSetting() {
            return this.setting;
        }

        public Component getExtendedDescription() {
            return this.extendedDescription;
        }

        public @Nullable Function<T, ClickEvent> getPreviewAction() {
            return this.previewAction;
        }

        @SuppressWarnings("unchecked")
        protected final B self() {
            return (B) this;
        }

        public B extendedDescription(final Component extendedDescription) {
            this.extendedDescription = extendedDescription;
            return this.self();
        }

        public B extendedDescription(final String extendedDescriptionKey) {
            return this.extendedDescription(translatable(extendedDescriptionKey));
        }

        public B previewAction(final Function<T, ClickEvent> previewAction) {
            this.previewAction = previewAction;
            return this.self();
        }

        public abstract O build();

        @Override
        public MenuPart<S> asMenuPart() {
            return this.build();
        }
    }
}
