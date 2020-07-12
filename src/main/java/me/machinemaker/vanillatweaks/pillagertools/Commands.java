package me.machinemaker.vanillatweaks.pillagertools;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import me.machinemaker.vanillatweaks.pillagertools.PillagerTools.ToggleOption;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("pillagertools|ptools")
class Commands extends BaseModuleCommand<PillagerTools> {
    public Commands(PillagerTools module) {
        super(module);
    }

    @Subcommand("toggle")
    @CommandPermission("vanillatweaks.pillagertools.toggle")
    @CommandCompletion("@pillagertools/toggles")
    public void onToggle(CommandSender sender, ToggleOption option) {
        String val;
        switch (option) {
            case PATROLS:
                this.module.config.patrols = !this.module.config.patrols;
                val = String.valueOf(this.module.config.patrols);
                break;
            case BAD_OMEN:
                this.module.config.badOmen = !this.module.config.badOmen;
                val = String.valueOf(this.module.config.badOmen);
                break;
            case PATROL_LEADERS:
                this.module.config.patrolLeaders = !this.module.config.patrolLeaders;
                val = String.valueOf(this.module.config.patrolLeaders);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + option);
        }
        this.module.config.save();
        sender.sendMessage(Lang.PILLAGER_TOGGLE.p().replace("%setting%", option.name()).replace("%val%", val));
    }

    @Subcommand("status")
    public void onStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Pillager Tools Status");
        sender.sendMessage(ChatColor.GOLD + ToggleOption.PATROLS.name() + ": " + module.config.patrols);
        sender.sendMessage(ChatColor.GOLD + ToggleOption.PATROL_LEADERS.name() + ": " + module.config.patrolLeaders);
        sender.sendMessage(ChatColor.GOLD + ToggleOption.BAD_OMEN.name() + ": " + module.config.badOmen);
    }
}
