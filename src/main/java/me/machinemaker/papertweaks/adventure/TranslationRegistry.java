/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.adventure;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class TranslationRegistry {

    private static final Key LANG_KEY = Key.key("papertweaks", "lang");
    private static final Map<String, Translation> TRANSLATIONS = Maps.newConcurrentMap();
    private static final net.kyori.adventure.translation.TranslationRegistry ADVENTURE_REGISTRY = net.kyori.adventure.translation.TranslationRegistry.create(LANG_KEY);

    static {
        GlobalTranslator.translator().addSource(ADVENTURE_REGISTRY);
    }

    private TranslationRegistry() {
    }

    public static void registerAll(final Locale locale, final ResourceBundle bundle) {
        ADVENTURE_REGISTRY.registerAll(locale, bundle, true);
        bundle.keySet().forEach(key -> {
            TRANSLATIONS.computeIfAbsent(key, Translation::new).register(locale, bundle.getString(key));
        });
    }

    public static Optional<String> translate(final String key, final Locale locale) {
        final Translation translation = TRANSLATIONS.get(key);
        if (translation == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(translation.translate(locale));
    }

    static class Translation {

        private final String key;
        private final Map<Locale, String> messages = Maps.newHashMap();

        private Translation(final String key) {
            this.key = key;
        }

        void register(final Locale locale, final String message) {
            this.messages.put(locale, message);
        }

        @Nullable String translate(final Locale locale) {
            String message = this.messages.get(locale);
            if (message == null) {
                message = this.messages.get(Locale.of(locale.getLanguage()));
                if (message == null) {
                    message = this.messages.get(Locale.ENGLISH);
                }
            }
            return message;
        }
    }
}
