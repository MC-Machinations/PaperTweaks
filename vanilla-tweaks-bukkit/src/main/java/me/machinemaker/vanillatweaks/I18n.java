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
package me.machinemaker.vanillatweaks;

import me.machinemaker.vanillatweaks.adventure.TranslationRegistry;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class I18n {

    private static final Configurations CONFIGS = new Configurations();

    private static final Logger LOGGER = LoggerFactory.getLogger("I18n");
    private final Path i18nPath;
    private final ClassLoader pluginClassLoader;
    private I18n(Path i18nPath, ClassLoader pluginClassLoader) {
        this.i18nPath = i18nPath;
        this.pluginClassLoader = pluginClassLoader;
    }

    private static @MonotonicNonNull I18n instance;

    static I18n create(@NotNull Path i18nPath, @NotNull ClassLoader pluginClassLoader) {
        if (instance == null) {
            instance = new I18n(i18nPath, pluginClassLoader);
        }
        return instance;
    }

    public void setupI18n() {
        ClassLoader previousLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.pluginClassLoader);
            VanillaTweaks.SUPPORTED_LOCALES.forEach(locale -> {
                this.updateI18nFile(locale);
                TranslationRegistry.registerAll(locale, createBundle(locale));
            });
        } finally {
            Thread.currentThread().setContextClassLoader(previousLoader);
        }
    }

    private void updateI18nFile(Locale locale) {
        Path localeFile = this.i18nPath.resolve(getI18nFolderFileName(locale));
        if (Files.notExists(localeFile)) {
            InputStream inputStream = this.pluginClassLoader.getResourceAsStream(getInJarResourceName(locale));
            if (inputStream == null) {
                throw new IllegalArgumentException("Couldn't find a resource for " + locale);
            }
            try {
                Files.createDirectories(localeFile.getParent());
                Files.copy(inputStream, localeFile);
            } catch (IOException exception) {
                LOGGER.error("Could not copy the locale file for {} to {}", locale, localeFile, exception);
            }
        } else {
            try {
                var localeFileBuilder = CONFIGS.propertiesBuilder(localeFile.toFile());
                PropertiesConfiguration localeConfig = localeFileBuilder.getConfiguration();
                PropertiesConfiguration inJarLocaleConfig = CONFIGS.properties(this.pluginClassLoader.getResource(getInJarResourceName(locale)));
                if (!inJarLocaleConfig.getLayout().getKeys().containsAll(localeConfig.getLayout().getKeys())) {
                    for (String key : localeConfig.getLayout().getKeys()) {
                        if (!inJarLocaleConfig.containsKey(key)) {
                            LOGGER.warn("{} is not a recognized key, it should be removed from {}", key, localeFile);
                        }
                    }
                }
                if (!localeConfig.getLayout().getKeys().containsAll(inJarLocaleConfig.getLayout().getKeys())) {
                    LOGGER.info("Found new additions to {}, updating that file with latest changes. This will not overwrite changes you have made", localeFile);
                    for (String key : inJarLocaleConfig.getLayout().getKeys()) {
                        if (!localeConfig.containsKey(key)) {
                            localeConfig.setProperty(key, inJarLocaleConfig.getProperty(key));
                        }
                    }
                    localeFileBuilder.save();
                }
            } catch (ConfigurationException exception) {
                LOGGER.error("Error reading/writing to {}", localeFile, exception);
            }
        }
    }

    private ResourceBundle createBundle(Locale locale) {
        Path localeFile = this.i18nPath.resolve(getI18nFolderFileName(locale));
        try (InputStream inputStream = Files.newInputStream(localeFile)) {
            return new PropertyResourceBundle(inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not load language from " + getInJarResourceName(locale), e);
        }
    }

    private static String getInJarResourceName(Locale locale) {
        return "i18n/lang_" + locale + ".properties";
    }

    private static String getI18nFolderFileName(Locale locale) {
        return locale + ".properties";
    }
}
