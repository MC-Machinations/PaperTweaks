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
package me.machinemaker.vanillatweaks.moonshine.module;

import io.leangen.geantyref.TypeToken;
import net.kyori.moonshine.Moonshine;
import net.kyori.moonshine.MoonshineBuilder;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MoonshineBuilderAdapter<T, R, I, O, F> {

    @Contract(pure = true)
    @Nullable Class<T> messageService();

    MoonshineBuilder.@NotNull Sourced<T, R, I> sourced(final MoonshineBuilder.@NotNull Receivers<T, R> receivers);

    MoonshineBuilder.@NotNull Rendered<T, R, I, O, F> rendered(final MoonshineBuilder.@NotNull Sourced<T, R, I> sourced);

    MoonshineBuilder.@NotNull Sent<T, R, I, O, F> sent(final MoonshineBuilder.@NotNull Rendered<T, R, I, O, F> rendered);

    MoonshineBuilder.@NotNull Resolved<T, R, I, O, F> resolved(final MoonshineBuilder.@NotNull Sent<T, R, I, O, F> sent);

    default void placeholderStrategies(final MoonshineBuilder.@NotNull Resolved<T, R, I, O, F> resolved) {
    }

    default @NotNull T create() throws UnscannableMethodException {
        return this.create(Thread.currentThread().getContextClassLoader());
    }

    default @NotNull T create(final @NotNull ClassLoader classLoader) throws UnscannableMethodException {
        if (this.messageService() == null) {
            throw new IllegalStateException("No messageService configured");
        }
        final MoonshineBuilder.Resolved<T, R, I, O, F> resolved = this.resolved(
                this.sent(
                            this.rendered(
                                    this.sourced(
                                            Moonshine.builder(TypeToken.get(this.messageService()))
                                    )
                            )
                    )
            );
        this.placeholderStrategies(resolved);
        return resolved.create(classLoader);
    }
}
