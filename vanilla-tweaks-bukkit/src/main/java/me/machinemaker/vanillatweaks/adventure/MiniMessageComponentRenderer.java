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
package me.machinemaker.vanillatweaks.adventure;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class MiniMessageComponentRenderer extends TranslatableComponentRenderer<Locale> {

    @Override
    protected @Nullable MessageFormat translate(final String key, final Locale context) {
        return GlobalTranslator.translator().translate(key, context);
    }

    @Override
    public Component render(final Component component, final Locale locale) {
        if (component instanceof MiniComponent miniComponent) {
            return this.renderMiniComponent(miniComponent, locale);
        }
        return super.render(component, locale);
    }

    private Component renderMiniComponent(final MiniComponent component, final Locale locale) {
        final Optional<String> message = TranslationRegistry.translate(component.key(), locale);
        if (message.isEmpty()) {
            final TranslatableComponent.Builder builder = translatable().key(component.key());
            if (component.args().isEmpty()) {
                final Map<String, Component> args = new HashMap<>(component.args());
                args.replaceAll((key, arg) -> this.render(arg, locale));
                builder.args(List.copyOf(args.values()));
            }
            return this.mergeStyleAndOptionallyDeepRender(component, builder, locale);
        }
        final TextComponent.Builder builder = text();
        this.mergeStyle(component, builder, locale);
        builder.append(MiniMessage.miniMessage().deserialize(message.get(), this.createTagResolver(component, locale)));
        return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, locale);
    }

    private TagResolver createTagResolver(final MiniComponent component, final Locale locale) {
        final TagResolver.Builder builder = TagResolver.builder();
        for (final Map.Entry<String, Component> entry : component.args().entrySet()) {
            if (entry.getValue() instanceof MiniComponent) {
                builder.resolver(Placeholder.component(entry.getKey(), this.render(entry.getValue(), locale)));
            } else {
                builder.resolver(Placeholder.component(entry.getKey(), entry.getValue()));
            }
        }
        return builder.build();
    }
}
