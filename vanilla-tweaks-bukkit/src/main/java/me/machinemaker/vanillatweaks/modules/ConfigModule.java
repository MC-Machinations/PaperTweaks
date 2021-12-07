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
package me.machinemaker.vanillatweaks.modules;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

@DefaultQualifier(NonNull.class)
public abstract class ConfigModule extends AbstractModule {

    protected Collection<Class<? extends ModuleConfig>> configs() {
        return Collections.emptySet();
    }

    @Override
    @MustBeInvokedByOverriders
    protected void configure() {
        Multibinder<ModuleConfig> configsBinder = Multibinder.newSetBinder(binder(), ModuleConfig.class);
        configs().forEach(configClass -> this.bindConfig(configsBinder, configClass));
    }

    private static <C extends ModuleConfig> C createInstanceWithoutInitialization(Class<C> configClass) {
        try {
            Constructor<C> ctor = configClass.getDeclaredConstructor();
            ctor.trySetAccessible();
            return ctor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(String.format("Unable to create a new instance of %s", configClass.getSimpleName()), e);
        }
    }

    private <C extends ModuleConfig> void bindConfig(Multibinder<ModuleConfig> binder, Class<C> configClass) {
        C config = createInstanceWithoutInitialization(configClass);
        bind(configClass).toInstance(config);
        binder.addBinding().toInstance(config);
    }
}
