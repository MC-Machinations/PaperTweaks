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
package me.machinemaker.vanillatweaks.modules;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import me.machinemaker.lectern.ConfigurationNode;
import me.machinemaker.lectern.YamlConfiguration;
import me.machinemaker.vanillatweaks.LoggerFactory;
import me.machinemaker.vanillatweaks.config.LoggingInvalidKeyHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public class ModuleRegistry extends AbstractModule {

    private static final Logger LOGGER = LoggerFactory.getLogger();
    private static final int MODULE_FILE_VERSION = 1;
    private static final String MODULE_PKG = "me.machinemaker.vanillatweaks.modules";
    private static final String MODULE_INFO_ANNOTATION = "me.machinemaker.vanillatweaks.annotations.ModuleInfo";

    private final ConfigurationNode moduleConfig;
    private final Map<String, Class<? extends ModuleBase>> modules = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    public ModuleRegistry(final JavaPlugin plugin, final Path dataPath) {
        this.moduleConfig = YamlConfiguration.builder(dataPath.resolve("modules.yml")).withInvalidKeyHandler(new LoggingInvalidKeyHandler(LOGGER)).build();
        this.moduleConfig.set("version", MODULE_FILE_VERSION);
        try (final ScanResult scanResult = new ClassGraph().enableAnnotationInfo().acceptPackages(MODULE_PKG).scan()) {
            for (final ClassInfo classInfo : scanResult.getClassesWithAnnotation(MODULE_INFO_ANNOTATION)) {
                final AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(MODULE_INFO_ANNOTATION);
                final String name = (String) annotationInfo.getParameterValues().getValue("name");
                final String configPath = (String) annotationInfo.getParameterValues().getValue("configPath");
                this.modules.put(name.toLowerCase(Locale.US), (Class<? extends ModuleBase>) classInfo.loadClass());
                this.moduleConfig.set(configPath, false);
            }
        }
        this.moduleConfig.reloadAndSave();
    }

    @Override
    protected void configure() {
        final MapBinder<String, ModuleBase> moduleMapBinder = MapBinder.newMapBinder(this.binder(), String.class, ModuleBase.class);
        this.modules.forEach((name, moduleClass) -> moduleMapBinder.addBinding(name).to(moduleClass).in(Scopes.SINGLETON));
        this.modules.forEach((name, moduleClass) -> this.bind(moduleClass).in(Scopes.SINGLETON));
        this.bind(ConfigurationNode.class).annotatedWith(Names.named("modules")).toInstance(this.moduleConfig);
        this.bind(ModuleManager.class).in(Scopes.SINGLETON);
    }
}
