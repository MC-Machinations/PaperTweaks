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

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public abstract class ModuleBase extends ConfigModule {

    private final ModuleInfo moduleInfo = getClass().getAnnotation(ModuleInfo.class);

    protected @NotNull Collection<Class<? extends ModuleListener>> listeners() {
        return Collections.emptySet();
    }

    protected @NotNull Collection<Class<? extends ModuleCommand>> commands() {
        return Collections.emptySet();
    }

    protected abstract @NotNull Class<? extends ModuleLifecycle> lifecycle();

    protected @NotNull Collection<ModuleRecipe<?>> recipes() {
        return Collections.emptySet();
    }

    @Override
    @MustBeInvokedByOverriders
    protected void configure() {
        super.configure();
        bind(ModuleBase.class).toInstance(this);
        bind(ModuleLifecycle.class).to(lifecycle()).asEagerSingleton();
        bind(ModuleInfo.class).toInstance(moduleInfo);

        bindModuleParts(listeners(), ModuleListener.class);
        bindModuleParts(commands(), ModuleCommand.class);

        final Multibinder<ModuleRecipe<?>> binder = Multibinder.newSetBinder(binder(), new TypeLiteral<ModuleRecipe<?>>() {});
        recipes().forEach(r -> bindRecipe(binder, r));
    }

    private <T> void bindModuleParts(Collection<Class<? extends T>> parts, Class<T> partClass) {
        final Multibinder<T> binder = Multibinder.newSetBinder(binder(), partClass);
        for (Class<? extends T> part : parts) {
            bind(part).in(Scopes.SINGLETON);
            binder.addBinding().to(part).in(Scopes.SINGLETON);
        }
    }

    private <R extends Recipe & Keyed> void bindRecipe(Multibinder<ModuleRecipe<?>> binder, ModuleRecipe<R> moduleRecipe) {
        binder.addBinding().toInstance(moduleRecipe);
        this.bind(moduleRecipe.recipeType()).annotatedWith(Names.named(moduleRecipe.key().getKey())).toInstance(moduleRecipe.recipe());
    }

    public String getName() {
        return this.moduleInfo.name();
    }

    public String getDescription() {
        return this.moduleInfo.description();
    }

    String getConfigPath() {
        return this.moduleInfo.configPath();
    }

    @Override
    protected @NotNull String getConfigDataFolder() {
        return this.moduleInfo.name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String toString() {
        return "Module{name=" + moduleInfo.name() + "}";
    }
}
