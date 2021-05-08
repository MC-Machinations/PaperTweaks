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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.MappedTranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static net.kyori.adventure.text.Component.text;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MappedTranslationTest {

    static final MiniMessage MM = MiniMessage.get();
    static final TranslatableComponentRenderer<Locale> RENDERER = new MappedTranslatableComponentRenderer(GlobalTranslator.get(), Map.of(Locale.ENGLISH, ResourceBundle.getBundle("test_lang", Locale.ENGLISH, UTF8ResourceBundleControl.get())));

    static {
        try {
            Class<?> globalTranslatorImplType = Class.forName("net.kyori.adventure.translation.GlobalTranslatorImpl");
            Field rendererField = globalTranslatorImplType.getDeclaredField("renderer");
            rendererField.trySetAccessible();
            rendererField.set(GlobalTranslator.get(), RENDERER);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testMappedComponent() {
        assertEquals(
                text("").append(text("Hey there, ")).append(text("BUDDY", NamedTextColor.YELLOW)),
                GlobalTranslator.render(MappedTranslatableComponent.mapped("test.key", Map.of("arg1", text("BUDDY", NamedTextColor.YELLOW))), Locale.ENGLISH)
                );
    }

    @Test
    void test() {
        Component component = MM.parse("Hey <there>", Template.of("there", text("THERE", NamedTextColor.YELLOW)));

        System.out.println(component);
    }

}
