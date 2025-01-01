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
package me.machinemaker.papertweaks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import me.machinemaker.papertweaks.adventure.TranslationRegistry;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.slf4j.Logger;

@DefaultQualifier(NonNull.class)
public final class I18n {

    private static final Configurations CONFIGS = new Configurations();
    private static final Logger LOGGER = LoggerFactory.getLogger("I18n");

    private final Path i18nPath;
    private final ClassLoader pluginClassLoader;

    private I18n(final Path i18nPath, final ClassLoader pluginClassLoader) {
        this.i18nPath = i18nPath;
        this.pluginClassLoader = pluginClassLoader;
    }

    static I18n create(final Path i18nPath, final ClassLoader pluginClassLoader) {
         return new I18n(i18nPath, pluginClassLoader);
    }

    private static String inJarResourceName(final Locale locale) {
        return "i18n/lang_" + locale + ".properties";
    }

    private static String i18nFolderFileName(final Locale locale) {
        return locale + ".properties";
    }

    public void setupI18n() {
        final ClassLoader previousLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.pluginClassLoader);
            PaperTweaks.SUPPORTED_LOCALES.forEach(locale -> {
                this.updateI18nFile(locale);
                TranslationRegistry.registerAll(locale, this.createBundle(locale));
            });
            TranslationRegistry.registerAll(Locale.US, this.createBundle(Locale.ENGLISH));
        } finally {
            Thread.currentThread().setContextClassLoader(previousLoader);
        }
    }

    private void updateI18nFile(final Locale locale) {
        final Path localeFile = this.i18nPath.resolve(i18nFolderFileName(locale));
        if (Files.notExists(localeFile)) {
            final @Nullable InputStream inputStream = this.pluginClassLoader.getResourceAsStream(inJarResourceName(locale));
            if (inputStream == null) {
                throw new IllegalArgumentException("Couldn't find a resource for " + locale);
            }
            try {
                Files.createDirectories(localeFile.getParent());
                Files.copy(inputStream, localeFile);
            } catch (final IOException exception) {
                LOGGER.error("Could not copy the locale file for {} to {}", locale, localeFile, exception);
            }
        } else {
            try {
                final FileBasedConfigurationBuilder<PropertiesConfiguration> localeFileBuilder = CONFIGS.propertiesBuilder(localeFile.toFile());
                final PropertiesConfiguration localeConfig = localeFileBuilder.getConfiguration();
                final PropertiesConfiguration inJarLocaleConfig = CONFIGS.properties(this.pluginClassLoader.getResource(inJarResourceName(locale)));
                if (!inJarLocaleConfig.getLayout().getKeys().containsAll(localeConfig.getLayout().getKeys())) {
                    for (final String key : localeConfig.getLayout().getKeys()) {
                        if (!inJarLocaleConfig.containsKey(key)) {
                            LOGGER.warn("{} is not a recognized key, it should be removed from {}", key, localeFile);
                        }
                    }
                }
                if (!localeConfig.getLayout().getKeys().containsAll(inJarLocaleConfig.getLayout().getKeys())) {
                    LOGGER.info("Found new additions to {}, updating that file with latest changes. This will not overwrite changes you have made", localeFile);
                    for (final String key : inJarLocaleConfig.getLayout().getKeys()) {
                        if (!localeConfig.containsKey(key)) {
                            localeConfig.setProperty(key, inJarLocaleConfig.getProperty(key));
                        }
                    }
                    localeFileBuilder.save();
                }
            } catch (final ConfigurationException exception) {
                LOGGER.error("Error reading/writing to {}", localeFile, exception);
            }
        }
    }

    private ResourceBundle createBundle(final Locale locale) {
        final Path localeFile = this.i18nPath.resolve(i18nFolderFileName(locale));
        try (final InputStream inputStream = Files.newInputStream(localeFile)) {
            return new PropertyResourceBundle(inputStream);
        } catch (final IOException e) {
            throw new IllegalArgumentException("Could not load language from " + inJarResourceName(locale), e);
        }
    }
}
