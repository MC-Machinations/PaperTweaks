package me.machinemaker.vanillatweaks;

import co.aikar.commands.BaseCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.function.Predicate;

public abstract class BaseModule {

    public final VanillaTweaks plugin;
    private final Predicate<VanillaTweaksModules> shouldEnable;
    public boolean registered;

    public BaseModule(VanillaTweaks plugin, Predicate<VanillaTweaksModules> shouldEnable) {
        this.plugin = plugin;
        this.shouldEnable = shouldEnable;
    }

    public boolean shouldEnable() {
        return shouldEnable.test(this.plugin.modules);
    }

    protected void registerEvents(Listener listener) {
        this.plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    protected void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    protected void registerCommands(BaseModuleCommand<?> command) {
        this.plugin.commandManager.registerCommand(command);
    }

    protected void unregisterCommands(BaseCommand command) {
        this.plugin.commandManager.unregisterCommand(command);
    }

    abstract public void register();

    abstract public void unregister();

    public void reload() { }
}
