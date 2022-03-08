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
package me.machinemaker.vanillatweaks.adventure;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class MiniMessageComponentRenderer extends TranslatableComponentRenderer<Locale> {

    @Override
    protected @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale context) {
        return GlobalTranslator.get().translate(key, context);
    }

    @Override
    public @NotNull Component render(@NotNull Component component, @NotNull Locale locale) {
        if (component instanceof MiniComponent miniComponent) {
            return this.renderMiniComponent(miniComponent, locale);
        }
        return super.render(component, locale);
    }

    private @NotNull Component renderMiniComponent(@NotNull MiniComponent component, @NotNull Locale locale) {
        final var message = TranslationRegistry.translate(component.key(), locale);
        if (message.isEmpty()) {
            final var builder = translatable().key(component.key());
            if (component.args().isEmpty()) {
                final var args = new HashMap<>(component.args());
                args.replaceAll((key, arg) -> this.render(arg, locale));
                builder.args(List.copyOf(args.values()));
            }
            return this.mergeStyleAndOptionallyDeepRender(component, builder, locale);
        }
        final var builder = text();
        this.mergeStyle(component, builder, locale);
        builder.append(MiniMessage.get().parse(message.get(), this.createTemplates(component, locale)));
        return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, locale);
    }

    private @NotNull List<Template> createTemplates(@NotNull MiniComponent component, @NotNull Locale locale) {
        final var builder = ImmutableList.<Template>builder();
        for (final var entry : component.args().entrySet()) {
            if (entry.getValue() instanceof MiniComponent) {
                builder.add(Template.of(entry.getKey(), this.render(entry.getValue(), locale)));
            } else {
                builder.add(Template.of(entry.getKey(), entry.getValue()));
            }
        }
        return builder.build();
    }
}
