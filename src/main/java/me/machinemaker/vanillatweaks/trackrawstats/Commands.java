package me.machinemaker.vanillatweaks.trackrawstats;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

@CommandAlias("trackrawstats|trawstats|trs")
class Commands extends BaseModuleCommand<TrackRawStats> {
    public Commands(TrackRawStats module) {
        super(module);
    }

    @HelpCommand
    @CommandPermission("vanillatweaks.trackrawstats.help")
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("clear")
    @Description("Clears the sidebar or tablist")
    @CommandCompletion("*")
    @CommandPermission("vanillatweaks.trackrawstats.clear")
    public void toggle(Player player, @Default("SIDEBAR") Slot slot) {
        if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            switch (slot) {
                case TABLIST:
                    player.getScoreboard().clearSlot(DisplaySlot.PLAYER_LIST);
                    player.sendMessage(Lang.SCOREBOARD_OFF.p().replace("%board%", "TABLIST"));
                    break;
                case SIDEBAR:
                    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                    player.sendMessage(Lang.SCOREBOARD_OFF.p().replace("%board%", "SIDEBAR"));
                    break;
            }
        }
    }

    @Subcommand("show")
    @CommandCompletion("@trs/stattypes @trs/objective *")
    @Description("Show a specific stat on the scoreboard")
    @CommandPermission("vanillatweaks.trackrawstats.show")
    public void show(Player player, StatType type, Objective objective, @Default("SIDEBAR") Slot slot) {
        switch (slot) {
            case SIDEBAR:
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                break;
            case TABLIST:
                objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                break;
        }
    }

    private enum Slot {
        SIDEBAR,
        TABLIST
    }
}
