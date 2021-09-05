package me.machinemaker.vanillatweaks.sethome;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class Commands extends BaseModuleCommand<SetHome> {

    public Commands(SetHome module) {
        super(module);
    }

    @CommandAlias("sethome")
    public void setHome(Player player) {
        SetHomeInfo info = this.module.homeMap.computeIfAbsent(player.getUniqueId(), uuid -> new SetHomeInfo(0, player.getLocation()));
        if (info.timesUsed >= module.config.defaultSetHomeLimit) {
            player.sendMessage(Lang.HOME_LIMIT.p());
            return;
        }
        info.location = player.getLocation();
        info.timesUsed++;
        this.module.save(player.getUniqueId(), info);
        player.sendMessage(Lang.HOME_SET.p());
    }

    @CommandAlias("home")
    public void home(Player player) {
        SetHomeInfo info = this.module.homeMap.get(player.getUniqueId());
        if (info == null) {
            player.sendMessage(Lang.NO_HOME_SET.p());
            return;
        }
        if (info.cooldown > System.currentTimeMillis()) {
            player.sendMessage(Lang.HOME_COOLDOWN.p().replace("%time%", String.valueOf((info.cooldown - System.currentTimeMillis()) / 1000)));
            return;
        }
        if (!this.module.config.allowAcrossDimension && !player.getWorld().getUID().equals(info.location.getWorld().getUID())) {
            player.sendMessage(Lang.NO_DIMENSIONAL.p());
            return;
        }
        player.teleport(info.location);
        player.sendMessage(Lang.GO_TO_HOME.p());
        info.cooldown = System.currentTimeMillis() + (this.module.config.sethomeCooldown * 1000L);
        this.module.save(player.getUniqueId(), info);
    }

    @CommandAlias("sethomeadmin|sha")
    @CommandPermission("vanillatweaks.sethome.admin")
    public class AdminCommands {

        @Subcommand("reset")
        public void resetLimit(CommandSender sender, OfflinePlayer player) {
            SetHomeInfo info = Commands.this.module.homeMap.get(player.getUniqueId());
            if (info == null) {
                sender.sendMessage(Lang.PLAYER_NOT_FOUND.p());
            } else {
                info.timesUsed = 0;
                Commands.this.module.save(player.getUniqueId(), info);
                sender.sendMessage(Lang.PLAYER_RESET.p());
            }
        }
    }
}
