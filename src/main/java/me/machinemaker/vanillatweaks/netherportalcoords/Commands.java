package me.machinemaker.vanillatweaks.netherportalcoords;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import org.bukkit.entity.Player;

class Commands extends BaseModuleCommand<NetherPortalCoords> {
    public Commands(NetherPortalCoords module) {
        super(module);
    }

    @CommandAlias("portalcoords|pcoords")
    @CommandPermission("vanillatweaks.netherportalcoords")
    public void portalCoords(Player player) {
        if (this.module.config.overworlds.contains(player.getWorld().getName())) {
            player.sendMessage(replaceLocation(Lang.PLAYER_IN_OVERWORLD.toString(), player.getLocation().getBlockX()/8, player.getLocation().getBlockY(), player.getLocation().getBlockZ()/8));
        } else if (this.module.config.netherWorlds.contains(player.getWorld().getName())) {
            player.sendMessage(replaceLocation(Lang.PLAYER_IN_NETHER.toString(), player.getLocation().getBlockX()*8, player.getLocation().getBlockY(), player.getLocation().getBlockZ()*8));
        } else {
            player.sendMessage(Lang.INVALID_WORLD.p());
        }
    }

    private String replaceLocation(String s, int x, int y, int z) {
        return s.replace("%x%", String.valueOf(x)).replace("%y%", String.valueOf(y)).replace("%z%", String.valueOf(z));
    }
}
