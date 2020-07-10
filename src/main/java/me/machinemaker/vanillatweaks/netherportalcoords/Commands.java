package me.machinemaker.vanillatweaks.netherportalcoords;

import co.aikar.commands.annotation.CommandAlias;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class Commands extends BaseModuleCommand<NetherPortalCoords> {
    public Commands(NetherPortalCoords module) {
        super(module);
    }

    @CommandAlias("portalcoords|pcoords")
    public void portalCoords(Player player) {
        if (this.module.config.overworlds.contains(player.getWorld().getName())) {
            player.sendMessage(ChatColor.YELLOW + "Nether:   X:" + player.getLocation().getBlockX()/8 + " | Y:" + player.getLocation().getBlockY() + " | Z:" + player.getLocation().getBlockZ()/8);
        } else if (this.module.config.netherWorlds.contains(player.getWorld().getName())) {
            player.sendMessage(ChatColor.YELLOW + "Nether:   X:" + player.getLocation().getBlockX()*8 + " | Y:" + player.getLocation().getBlockY() + " | Z:" + player.getLocation().getBlockZ()*8);
        } else {
            player.sendMessage("This world is not configured as an overworld or netherworld");
        }
    }
}
