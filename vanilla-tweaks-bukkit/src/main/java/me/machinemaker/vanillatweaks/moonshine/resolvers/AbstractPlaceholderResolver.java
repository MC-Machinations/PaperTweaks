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
package me.machinemaker.vanillatweaks.moonshine.resolvers;

import java.util.Map;
import me.machinemaker.vanillatweaks.moonshine.module.AnnotationBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.moonshine.placeholder.ConclusionValue;
import net.kyori.moonshine.placeholder.ContinuanceValue;
import net.kyori.moonshine.placeholder.IPlaceholderResolver;
import net.kyori.moonshine.util.Either;

public abstract class AbstractPlaceholderResolver<P> extends AnnotationBase implements IPlaceholderResolver<Audience, P, Component> {

    protected final Map<String, Either<ConclusionValue<? extends Component>, ContinuanceValue<?>>> constant(final String placeholderName, final Component value) {
        return Map.of(placeholderName, Either.left(ConclusionValue.conclusionValue(value)));
    }
}
