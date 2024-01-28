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
package me.machinemaker.papertweaks.moonshine.resolvers.simple;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Map;
import me.machinemaker.papertweaks.moonshine.resolvers.AbstractPlaceholderResolver;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.moonshine.placeholder.ConclusionValue;
import net.kyori.moonshine.placeholder.ContinuanceValue;
import net.kyori.moonshine.util.Either;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class SimplePlaceholderResolver<P> extends AbstractPlaceholderResolver<P> {

    public abstract TextComponent.Builder toComponent(P value);

    protected final @Nullable Parameter findParameter(final P value, final Method method, final @Nullable Object[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == value) {
                return method.getParameters()[i];
            }
        }
        return null;
    }

    @Override
    public final Map<String, Either<ConclusionValue<? extends Component>, ContinuanceValue<?>>> resolve(final String placeholderName, final P value, final Audience receiver, final Type owner, final Method method, final @Nullable Object[] parameters) {
        final TextComponent.Builder builder = this.toComponent(value);
        final @Nullable Parameter param = this.findParameter(value, method, parameters);

        if (param != null) {
            builder.colorIfAbsent(this.getTextColor(param));
        }

        return Map.of(placeholderName, Either.left(ConclusionValue.conclusionValue(builder.build())));
    }
}
