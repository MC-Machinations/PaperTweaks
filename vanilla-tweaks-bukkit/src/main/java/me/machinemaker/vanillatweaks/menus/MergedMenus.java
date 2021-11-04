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
package me.machinemaker.vanillatweaks.menus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public final class MergedMenus {

    private MergedMenus() {
    }

    public static class Menu1<S1, S2> extends DelegateConfigurationMenu<S1> {

        private final BuildablePart<S2> extra1;

        public Menu1(@NotNull ConfigurationMenu<S1> menu1, @NotNull BuildablePart<S2> extra1) {
            super(menu1);
            this.extra1 = extra1;
        }

        public @NotNull ComponentLike build(@NotNull S1 object1, @NotNull S2 object2) {
            final var builder = text()
                    .append(this.buildHeader(object1))
                    .append(this.buildParts(object1))
                    .append(this.buildFooter(object1));
            return addExtras(builder, this.extra1.build(object2));
        }

        public @NotNull ComponentLike addExtras(@NotNull ComponentLike builder, @NotNull ComponentLike @NotNull... components) {
            final List<Component> children = new ArrayList<>(builder.asComponent().children());
            final int offset = children.size() - 1;
            for (int i = 0; i < components.length; i++) {
                children.add(i + offset, components[i].asComponent());
            }
            return text().append(children);
        }

        @Override
        public @NotNull ComponentLike build(@NotNull S1 object) {
            throw new UnsupportedOperationException("Not supported on MergedConfigurationMenus");
        }
    }

    public static class Menu2<S1, S2, S3> extends Menu1<S1, S2> {

        private final BuildablePart<S3> extra2;

        public Menu2(@NotNull AbstractConfigurationMenu<S1> menu1, @NotNull BuildablePart<S2> extra1, @NotNull BuildablePart<S3> extra2) {
            super(menu1, extra1);
            this.extra2 = extra2;
        }

        protected @NotNull ComponentLike build(@NotNull S1 object1, @NotNull S2 object2, @NotNull S3 object3) {
            return this.addExtras(super.build(object1, object2), this.extra2.build(object3));
        }
    }

}
