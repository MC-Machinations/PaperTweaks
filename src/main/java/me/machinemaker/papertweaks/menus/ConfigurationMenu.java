/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.menus;

import org.incendo.cloud.context.CommandContext;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;

public interface ConfigurationMenu<S> extends BuildablePart<S> {

    ComponentLike[] buildHeader(S object);

    Iterable<? extends ComponentLike> buildParts(S object);

    ComponentLike[] buildFooter(S object);

    default void send(final CommandContext<CommandDispatcher> context, final S object) {
        this.send(context.sender(), object);
    }

    void send(Audience audience, S object);

}
