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
package me.machinemaker.vanillatweaks.adventure;

import me.machinemaker.vanillatweaks.adventure.translations.MappedTranslatableComponent;
import me.machinemaker.vanillatweaks.adventure.translations.MappedTranslatableComponentRenderer;
import me.machinemaker.vanillatweaks.adventure.translations.TranslationRegistry;
import net.kyori.adventure.text.Component;
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

    static {
        try {
            Class<?> globalTranslatorImplType = Class.forName("net.kyori.adventure.translation.GlobalTranslatorImpl");
            Field rendererField = globalTranslatorImplType.getDeclaredField("renderer");
            rendererField.trySetAccessible();
            rendererField.set(GlobalTranslator.get(), MappedTranslatableComponentRenderer.GLOBAL_INSTANCE);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        TranslationRegistry.registerAll(Locale.ENGLISH, ResourceBundle.getBundle("test_lang", Locale.ENGLISH, UTF8ResourceBundleControl.get()));
    }

    @Test
    void testMappedComponent() {
        assertEquals(
                text().append(text("").append(text("Hey there, ")).append(text("BUDDY", NamedTextColor.YELLOW))).build(),
                GlobalTranslator.render(MappedTranslatableComponent.mapped("test.key", Map.of("arg1", text("BUDDY", NamedTextColor.YELLOW))), Locale.ENGLISH)
                );
    }

    @Test
    void test() {
        Component component = MM.parse("Hey <there>", Template.of("there", text("THERE", NamedTextColor.YELLOW)));

        System.out.println(component);
    }

}
