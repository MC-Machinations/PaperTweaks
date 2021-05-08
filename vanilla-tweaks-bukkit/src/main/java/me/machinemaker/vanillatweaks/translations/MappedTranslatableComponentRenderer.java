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
package me.machinemaker.vanillatweaks.translations;

import me.machinemaker.vanillatweaks.VanillaTweaks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.MappedTranslatableComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class MappedTranslatableComponentRenderer extends TranslatableComponentRenderer<Locale> {

    public static final MappedTranslatableComponentRenderer GLOBAL_INSTANCE = new MappedTranslatableComponentRenderer(VanillaTweaks.BUNDLE_MAP);

    private static final MiniMessage MM = MiniMessage.get();

    private final GlobalTranslator global;
    private final Map<Locale, ResourceBundle> bundles;

    private MappedTranslatableComponentRenderer(Map<Locale, ResourceBundle> bundles) {
        this(GlobalTranslator.get(), bundles);
    }

    public MappedTranslatableComponentRenderer(GlobalTranslator global, Map<Locale, ResourceBundle> bundles) {
        this.global = global;
        this.bundles = bundles;
    }

    @Override
    protected @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale context) {
        return global.translate(key, context);
    }

    @Override
    public @NotNull Component render(@NotNull Component component, @NotNull Locale context) {
        if (component instanceof MappedTranslatableComponent mappedTranslatableComponent) {
            return this.renderMappedTranslatable(mappedTranslatableComponent, context);
        }
        return super.render(component, context);
    }

    protected @NotNull Component renderMappedTranslatable(@NotNull MappedTranslatableComponent component, @NotNull Locale context) {
        var builder = Component
                .text()
                .append(MM.parse(this.bundles.get(context).getString(component.key()), component.templates(context)))
                .style(component.style());

        return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
    }
}
