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
package me.machinemaker.vanillatweaks.adventure.translations;

import com.google.common.collect.Maps;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public final class TranslationRegistry {

    private static final Key LANG_KEY = Key.key("vanillatweaks", "lang");
    private static final Map<String, Translation> TRANSLATIONS = Maps.newConcurrentMap();
    private static final net.kyori.adventure.translation.TranslationRegistry ADVENTURE_REGISTRY = net.kyori.adventure.translation.TranslationRegistry.create(LANG_KEY);
    static {
        GlobalTranslator.get().addSource(ADVENTURE_REGISTRY);
    }

    private TranslationRegistry() {
    }

    public static void registerAll(Locale locale, ResourceBundle bundle) {
        ADVENTURE_REGISTRY.registerAll(locale, bundle, false);
        bundle.keySet().forEach(key -> {
            TRANSLATIONS.computeIfAbsent(key, Translation::new).register(bundle.getLocale(), bundle.getString(key));
        });
    }

    public static @Nullable String translate(@NotNull String key, @NotNull Locale locale) {
        Translation translation = TRANSLATIONS.get(key);
        if (translation == null) {
            return null;
        }
        return translation.translate(locale);
    }

    static class Translation {

        private final String key;
        private final Map<Locale, String> messages = Maps.newHashMap();

        private Translation(String key) {
            this.key = key;
        }

        void register(@NotNull Locale locale, @NotNull String message) {
            this.messages.put(locale, message);
        }

        @Nullable String translate(@NotNull Locale locale) {
            String message = this.messages.get(locale);
            if (message == null) {
                message = this.messages.get(new Locale(locale.getLanguage()));
                if (message == null) {
                    message = this.messages.get(Locale.ENGLISH);
                }
            }
            return message;
        }
    }
}
