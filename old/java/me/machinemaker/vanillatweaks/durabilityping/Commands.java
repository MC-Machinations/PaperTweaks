package me.machinemaker.vanillatweaks.durabilityping;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

class Commands extends BaseModuleCommand<DurabilityPing> {
    public Commands(DurabilityPing module) {
        super(module);
    }

    @CommandAlias("durabilityping|dping")
    @CommandPermission("vanillatweaks.durabilityping.toggle")
    public void toggle(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (!container.has(this.module.PING, PersistentDataType.INTEGER)) {
            container.set(this.module.PING, PersistentDataType.INTEGER, 1);
        }
        boolean playPing = container.get(this.module.PING, PersistentDataType.INTEGER) == 1;
        if (playPing) {
            player.sendMessage(Lang.DP_TOGGLED_OFF.p());
            container.set(this.module.PING, PersistentDataType.INTEGER, 0);
        } else {
            player.sendMessage(Lang.DP_TOGGLED_ON.p());
            container.set(this.module.PING, PersistentDataType.INTEGER, 1);
        }
    }
}
