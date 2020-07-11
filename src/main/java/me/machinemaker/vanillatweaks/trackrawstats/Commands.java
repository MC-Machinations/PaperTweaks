package me.machinemaker.vanillatweaks.trackrawstats;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

@CommandAlias("trackrawstats|trawstats|trs")
class Commands extends BaseModuleCommand<TrackRawStats> {
    public Commands(TrackRawStats module) {
        super(module);
    }

    @Subcommand("toggle")
    public void toggle(Player player) {
        if (player.getScoreboard().equals(this.module.board)) {
            player.sendMessage(Lang.SCOREBOARD_OFF.p().replace("%board%", "TrackRawStats"));
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        } else {
            player.setScoreboard(this.module.board);
            player.sendMessage(Lang.SCOREBOARD_ON.p().replace("%board%", "TrackRawStats"));
        }

    }

    @Subcommand("show")
    @CommandCompletion("@trs/stattypes @trs/objective")
    public void show(Player player, StatType type, Objective objective) {
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
}
