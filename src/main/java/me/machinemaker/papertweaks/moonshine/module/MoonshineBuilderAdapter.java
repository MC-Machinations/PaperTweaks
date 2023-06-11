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
package me.machinemaker.papertweaks.moonshine.module;

import io.leangen.geantyref.TypeToken;
import net.kyori.moonshine.Moonshine;
import net.kyori.moonshine.MoonshineBuilder;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

public interface MoonshineBuilderAdapter<T, R, I, O, F> {

    @Contract(pure = true)
    @Nullable Class<T> messageService();

    MoonshineBuilder.Sourced<T, R, I> sourced(final MoonshineBuilder.Receivers<T, R> receivers);

    MoonshineBuilder.Rendered<T, R, I, O, F> rendered(final MoonshineBuilder.Sourced<T, R, I> sourced);

    MoonshineBuilder.Sent<T, R, I, O, F> sent(final MoonshineBuilder.Rendered<T, R, I, O, F> rendered);

    MoonshineBuilder.Resolved<T, R, I, O, F> resolved(final MoonshineBuilder.Sent<T, R, I, O, F> sent);

    default void placeholderStrategies(final MoonshineBuilder.Resolved<T, R, I, O, F> resolved) {
    }

    default T create() throws UnscannableMethodException {
        return this.create(Thread.currentThread().getContextClassLoader());
    }

    default T create(final ClassLoader classLoader) throws UnscannableMethodException {
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
