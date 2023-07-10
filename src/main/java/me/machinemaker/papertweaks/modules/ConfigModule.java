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
package me.machinemaker.papertweaks.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import me.machinemaker.lectern.BaseConfig;
import me.machinemaker.papertweaks.annotations.ConfigureModuleConfig;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract class ConfigModule extends AbstractModule {

    @Inject
    @Named("modules")
    private Path modulesConfigFolder;

    protected Collection<Class<? extends ModuleConfig>> configs() {
        return Collections.emptySet();
    }

    @Override
    @MustBeInvokedByOverriders
    protected void configure() {
        final Multibinder<ModuleConfig> configsBinder = Multibinder.newSetBinder(this.binder(), ModuleConfig.class);
        this.configs().forEach(configClass -> this.bindConfig(configsBinder, configClass));
    }

    private <C extends ModuleConfig> void bindConfig(final Multibinder<ModuleConfig> binder, final Class<C> configClass) {
        final String folder;
        if (configClass.isAnnotationPresent(ConfigureModuleConfig.class)) {
            folder = configClass.getAnnotation(ConfigureModuleConfig.class).folder();
        } else {
            folder = this.getConfigDataFolder();
        }
        final C config = BaseConfig.create(configClass, this.modulesConfigFolder.resolve(folder));
        this.bind(configClass).toInstance(config);
        binder.addBinding().toInstance(config);
    }

    protected abstract String getConfigDataFolder();

}
