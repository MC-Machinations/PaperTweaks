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
package me.machinemaker.papertweaks.menus.parts;

import me.machinemaker.papertweaks.menus.BuildablePart;
import me.machinemaker.papertweaks.menus.MergedMenus;
import net.kyori.adventure.text.Component;

public interface MenuPart<S> extends MenuPartLike<S> {

    Component build(S object, String commandPrefix);

    @Override
    default MenuPart<S> asMenuPart() {
        return this;
    }

    /**
     * For use with {@link MergedMenus.Menu1}s.
     *
     * @param <S>
     * @see MenuPart#configure(String)
     */
    class Configured<S> implements BuildablePart<S> {

        private final MenuPart<S> part;
        private final String commandPrefix;

        Configured(final MenuPart<S> part, final String commandPrefix) {
            this.part = part;
            this.commandPrefix = commandPrefix;
        }

        @Override
        public Component build(final S object) {
            return this.part.build(object, this.commandPrefix);
        }
    }
}
