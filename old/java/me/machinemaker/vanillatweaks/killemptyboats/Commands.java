package me.machinemaker.vanillatweaks.killemptyboats;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;

import java.util.concurrent.atomic.AtomicInteger;

class Commands extends BaseModuleCommand<KillEmptyBoats> {

    public Commands(KillEmptyBoats module) {
        super(module);
    }

    @CommandAlias("killboats")
    @CommandPermission("vanillatweaks.killboats")
    public void onKillBoats(CommandSender sender) {
        AtomicInteger boatCount = new AtomicInteger();
        Bukkit.getWorlds().forEach(world -> world.getEntitiesByClass(Boat.class).forEach(boat -> {
            if (boat.getPassengers().isEmpty()) {
                boatCount.getAndIncrement();
                boat.remove();
            }
        }));
        sender.sendMessage(Lang.KILL_BOATS.p().replace("%num%", String.valueOf(boatCount.get())));
    }
}
