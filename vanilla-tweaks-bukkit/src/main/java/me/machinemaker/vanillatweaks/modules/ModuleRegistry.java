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
import me.machinemaker.lectern.Lectern;
import me.machinemaker.lectern.LecternConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

public class ModuleRegistry extends AbstractModule {

    private static final String MODULE_PKG = "me.machinemaker.vanillatweaks.modules";
    private static final String MODULE_INFO_ANNOTATION = "me.machinemaker.vanillatweaks.annotations.ModuleInfo";

    private final LecternConfig moduleConfig;
    private final Map<String, Class<? extends ModuleBase>> modules = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    public ModuleRegistry(JavaPlugin plugin) {
        this.moduleConfig = Lectern.createConfig(new File(plugin.getDataFolder(), "modules.yml"));
        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().acceptPackages(MODULE_PKG).scan()) {
            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(MODULE_INFO_ANNOTATION)) {
                AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(MODULE_INFO_ANNOTATION);
                String name = (String) annotationInfo.getParameterValues().getValue("name");
                String configPath = (String) annotationInfo.getParameterValues().getValue("configPath");
                modules.put(name, (Class<? extends ModuleBase>) classInfo.loadClass());
                moduleConfig.set(configPath, false);
            }
        }
        moduleConfig.reloadOrSave();
        moduleConfig.save();
    }

    @Override
    protected void configure() {
        MapBinder<String, ModuleBase> moduleMapBinder = MapBinder.newMapBinder(binder(), String.class, ModuleBase.class);
        modules.forEach((name, moduleClass) -> moduleMapBinder.addBinding(name).to(moduleClass).in(Scopes.SINGLETON));
        bind(LecternConfig.class).annotatedWith(Names.named("modules")).toInstance(moduleConfig);
        bind(ModuleManager.class).in(Scopes.SINGLETON);
    }
}
