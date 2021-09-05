package me.machinemaker.vanillatweaks;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.Injector;
import io.papermc.lib.PaperLib;
import me.machinemaker.configmanager.configs.YamlConfig;
import me.machinemaker.configmanager.managers.YamlConfigManager;
import me.machinemaker.vanillatweaks._managers.InjectionManager;
import me.machinemaker.vanillatweaks._managers.ModuleManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class VanillaTweaks extends JavaPlugin {

    public YamlConfigManager configManager;
    public BukkitCommandManager commandManager;
    ModuleManager moduleManager;
    InjectionManager injectionManager;

    public VanillaTweaksModules modules;
    public VanillaTweaksConfig config;
    public YamlConfig lang;

    private final int PLUGIN_ID = 8141;

    @Override
    public void onEnable() {
        configManager = new YamlConfigManager(this);
        commandManager = new BukkitCommandManager(this);
        commandManager.enableUnstableAPI("help");
        moduleManager = new ModuleManager();
        modules = new VanillaTweaksModules();
        configManager.createConfig("lang", "lang.yml");
        lang = configManager.getConfig("lang");
        Lang.init(lang);

        config = new VanillaTweaksConfig();
        config.init(this);
        if (config.enabled) {
            Metrics metrics = new Metrics(this, PLUGIN_ID);
        }

        injectionManager = new InjectionManager(this, modules);

        injectDependencies();
        modules.init(this);

        commandManager.registerDependency(VanillaTweaksModules.class, modules);
        commandManager.registerDependency(ModuleManager.class, moduleManager);
        commandManager.registerCommand(new VanillaTweaksCommand());

        moduleManager.load();
        PaperLib.suggestPaper(this);
    }

    @Override
    public void onDisable() {
        moduleManager.enabled.forEach(BaseModule::unregister);
    }

    private void injectDependencies() {
        Injector injector = injectionManager.createInjector();
        injector.injectMembers(moduleManager);
        moduleManager.allModules().forEach(injector::injectMembers);
    }
}
