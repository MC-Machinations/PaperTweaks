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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleMessageService;
import me.machinemaker.vanillatweaks.moonshine.AdventureMessageSender;
import me.machinemaker.vanillatweaks.moonshine.TranslatableMessageSource;
import me.machinemaker.vanillatweaks.moonshine.receivers.AudienceReceiverResolver;
import me.machinemaker.vanillatweaks.moonshine.renderers.MiniMessageMessageRenderer;
import me.machinemaker.vanillatweaks.moonshine.resolvers.ComponentPlaceholderResolver;
import me.machinemaker.vanillatweaks.moonshine.resolvers.simple.NumberPlaceholderResolver;
import me.machinemaker.vanillatweaks.moonshine.resolvers.simple.StringPlaceholderResolver;
import me.machinemaker.vanillatweaks.moonshine.resolvers.simple.WorldPlaceholderResolver;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.moonshine.MoonshineBuilder;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import net.kyori.moonshine.strategy.StandardPlaceholderResolverStrategy;
import net.kyori.moonshine.strategy.supertype.StandardSupertypeThenInterfaceSupertypeStrategy;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

@DefaultQualifier(NonNull.class)
public abstract class MoonshineModuleBase<T extends ModuleMessageService> extends ModuleBase implements MoonshineBuilderAdapter<T, Audience, String, Component, Component> {

    private @Inject @Named("plugin") ClassLoader classLoader;

    @Override
    @MustBeInvokedByOverriders
    protected void configure() {
        super.configure();
        try {
            final T messageService = this.create(this.classLoader);
            bind(ModuleMessageService.class).toInstance(messageService);
            bind(this.messageService()).toInstance(messageService);
        } catch (UnscannableMethodException exception) {
            throw new IllegalStateException("Couldn't create message service for " + this.getName(), exception);
        }
    }

    @Override
    public MoonshineBuilder.Sourced<T, Audience, String> sourced(MoonshineBuilder.Receivers<T, Audience> receivers) {
        receivers.receiverLocatorResolver(new AudienceReceiverResolver(), 0);
        return receivers.sourced(new TranslatableMessageSource());
    }

    @Override
    public MoonshineBuilder.Rendered<T, Audience, String, Component, Component> rendered(MoonshineBuilder.Sourced<T, Audience, String> sourced) {
        return sourced.rendered(new MiniMessageMessageRenderer());
    }

    @Override
    public MoonshineBuilder.Sent<T, Audience, String, Component, Component> sent(MoonshineBuilder.Rendered<T, Audience, String, Component, Component> rendered) {
        return rendered.sent(new AdventureMessageSender());
    }

    @Override
    public MoonshineBuilder.Resolved<T, Audience, String, Component, Component> resolved(MoonshineBuilder.Sent<T, Audience, String, Component, Component> sent) {
        return sent.resolvingWithStrategy(new StandardPlaceholderResolverStrategy<>(new StandardSupertypeThenInterfaceSupertypeStrategy(false)));
    }

    @Override
    @MustBeInvokedByOverriders
    public void placeholderStrategies(MoonshineBuilder.Resolved<T, Audience, String, Component, Component> resolved) {
        this.registerNumberPlaceholderStrategies(resolved);
        resolved
                .weightedPlaceholderResolver(String.class, new StringPlaceholderResolver(), 0)
                .weightedPlaceholderResolver(World.class, new WorldPlaceholderResolver(), 0)
                .weightedPlaceholderResolver(Component.class, new ComponentPlaceholderResolver(), 0);
    }

    private void registerNumberPlaceholderStrategies(MoonshineBuilder.Resolved<T, Audience, String, Component, Component> resolved) {
        resolved
                .weightedPlaceholderResolver(int.class, new NumberPlaceholderResolver<>(String::valueOf), 0)
                .weightedPlaceholderResolver(Integer.class, new NumberPlaceholderResolver<>(String::valueOf), 0)
                .weightedPlaceholderResolver(long.class, new NumberPlaceholderResolver<>(String::valueOf), 0)
                .weightedPlaceholderResolver(Long.class, new NumberPlaceholderResolver<>(String::valueOf), 0);
    }
}
