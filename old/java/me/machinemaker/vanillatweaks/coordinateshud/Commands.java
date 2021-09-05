package me.machinemaker.vanillatweaks.coordinateshud;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

class Commands extends BaseModuleCommand<CoordinatesHUD> {

    public Commands(CoordinatesHUD module) {
        super(module);
    }

    @CommandAlias("togglehud|thud")
    @CommandPermission("vanillatweaks.coordinateshud.togglehud")
    public void toggleHUD(Player player) {
        if (this.module.enabled.remove(player)) {
            player.sendMessage(Lang.HUD_OFF.p());
            player.getPersistentDataContainer().remove(this.module.coordinatesKey);
        } else {
            this.module.enabled.add(player);
            player.getPersistentDataContainer().set(this.module.coordinatesKey, PersistentDataType.BYTE, (byte) 1);
            player.sendMessage(Lang.HUD_ON.p());
        }
    }
}
