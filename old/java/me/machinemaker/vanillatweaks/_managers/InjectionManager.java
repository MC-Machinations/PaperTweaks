package me.machinemaker.vanillatweaks._managers;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.VanillaTweaksModules;

public class InjectionManager extends AbstractModule {

    private final VanillaTweaks plugin;
    private final VanillaTweaksModules config;

    public InjectionManager(VanillaTweaks plugin, VanillaTweaksModules config) {
        this.plugin = plugin;
        this.config = config;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(VanillaTweaks.class).toInstance(plugin);
        this.bind(VanillaTweaksModules.class).toInstance(config);
    }
}
