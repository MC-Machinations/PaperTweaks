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
package me.machinemaker.vanillatweaks.moonshine.receivers;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import me.machinemaker.vanillatweaks.moonshine.annotation.Receiver;
import net.kyori.adventure.audience.Audience;
import net.kyori.moonshine.receiver.IReceiverLocator;
import net.kyori.moonshine.receiver.IReceiverLocatorResolver;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

public class AudienceReceiverResolver implements IReceiverLocatorResolver<Audience> {

    @Override
    public @Nullable IReceiverLocator<Audience> resolve(final Method method, final Type proxy) {
        return new Resolver();
    }

    private static final class Resolver implements IReceiverLocator<Audience> {

        @Override
        public Audience locate(final Method method, final Object proxy, final @Nullable Object[] parameters) {
            for (int i = 0; i < method.getParameters().length; i++) {
                final Parameter param = method.getParameters()[i];
                if (param.isAnnotationPresent(Receiver.class)) {
                    return requireNonNull((Audience) parameters[i]);
                }
            }
            for (final Object param : parameters) {
                if (param instanceof Audience audience) {
                    return audience;
                }
            }
            return Audience.empty();
        }
    }
}
