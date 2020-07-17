package me.machinemaker.vanillatweaks.coordinateshud;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import org.bukkit.entity.Player;

class Commands extends BaseModuleCommand<CoordinatesHUD> {

    public Commands(CoordinatesHUD module) {
        super(module);
    }

    @CommandAlias("togglehud|thud")
    @CommandPermission("vanillatweaks.coordinateshud.togglehud")
    public void toggleHUD(Player player) {
        if (this.module.enabled.remove(player)) {
            player.sendMessage(Lang.HUD_OFF.p());
        } else {
            this.module.enabled.add(player);
            player.sendMessage(Lang.HUD_ON.p());
        }
    }
}
