package me.machinemaker.vanillatweaks;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class GlobalListener implements Listener {

    private final ModuleManager moduleManager;

    @Inject
    public GlobalListener(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBukkitReload(ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD) {
           moduleManager.reloadModules();
        }
    }

}
