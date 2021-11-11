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
package me.machinemaker.vanillatweaks.moonshine.receivers;

import me.machinemaker.vanillatweaks.moonshine.annotation.Receiver;
import net.kyori.adventure.audience.Audience;
import net.kyori.moonshine.receiver.IReceiverLocator;
import net.kyori.moonshine.receiver.IReceiverLocatorResolver;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class AudienceReceiverResolver implements IReceiverLocatorResolver<Audience> {

    @Override
    public @Nullable IReceiverLocator<Audience> resolve(Method method, Type proxy) {
        return new Resolver();
    }

    private static final class Resolver implements IReceiverLocator<Audience> {

        @Override
        public Audience locate(Method method, Object proxy, @Nullable Object[] parameters) {
            for (int i = 0; i < method.getParameters().length; i++) {
                final var param = method.getParameters()[i];
                if (param.isAnnotationPresent(Receiver.class)) {
                    return (Audience) parameters[i];
                }
            }
            for (Object param : parameters) {
                if (param instanceof Audience audience) {
                    return audience;
                }
            }
            return Audience.empty();
        }
    }
}
