/*
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
package me.machinemaker.vanillatweaks.adventure.translations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class MappedTranslatableComponentRenderer extends TranslatableComponentRenderer<Locale> {

    public static final MappedTranslatableComponentRenderer GLOBAL_INSTANCE = new MappedTranslatableComponentRenderer();

    private static final MiniMessage MM = MiniMessage.get();

    private final Translator translator;

    private MappedTranslatableComponentRenderer() {
        this(GlobalTranslator.get());
    }

    public MappedTranslatableComponentRenderer(Translator translator) {
        this.translator = translator;
    }

    @Override
    protected @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale context) {
        return translator.translate(key, context);
    }

    @Override
    public @NotNull Component render(@NotNull Component component, @NotNull Locale context) {
        if (component instanceof MappedTranslatableComponent mappedTranslatableComponent) {
            return this.renderMappedTranslatable(mappedTranslatableComponent, context);
        }
        return super.render(component, context);
    }

    protected @NotNull Component renderMappedTranslatable(@NotNull MappedTranslatableComponent component, @NotNull Locale context) {
        final String message = TranslationRegistry.translate(component.key(), context);
        if (message == null) {
            var builder =translatable().key(component.key());
            if (!component.args().isEmpty()) {
                final Map<String, Component> args = Maps.newHashMap(component.args());
                args.replaceAll((argKey, arg) -> this.render(arg, context));
                builder.args(ImmutableList.copyOf(args.values()));
            }
            return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
        }
        var builder = text();
        this.mergeStyle(component, builder, context);
        builder.append(MM.parse(message, component.templates(context)));
        return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
    }
}
