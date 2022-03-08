/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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

import cloud.commandframework.context.CommandContext;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public interface ConfigurationMenu<S> extends BuildablePart<S> {

    @NotNull ComponentLike[] buildHeader(@NotNull S object);

    @NotNull Iterable<? extends ComponentLike> buildParts(@NotNull S object);

    @NotNull ComponentLike[] buildFooter(@NotNull S object);

    default void send(@NotNull CommandContext<CommandDispatcher> context, @NotNull S object) {
        this.send(context.getSender(), object);
    }

    void send(@NotNull Audience audience, @NotNull S object);

}
