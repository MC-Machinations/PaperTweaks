package me.machinemaker.vanillatweaks.mobcounting;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("mobdeathcount|mdcount|mdc")
class Commands extends BaseModuleCommand<MobCounting> {

    public Commands(MobCounting mobCounting) {
        super(mobCounting);
    }

    private final HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to run").color(ChatColor.GRAY).create());

    @Subcommand("help")
    @Default
    @CommandPermission("vanillatweaks.mobdeathcount.help")
    public void help(Player player) {
        player.spigot().sendMessage(new ComponentBuilder("Mob Death Counter help:").color(ChatColor.YELLOW).bold(true).create());
        player.spigot().sendMessage(new ComponentBuilder("1. ").color(ChatColor.GREEN).append("Start counting mobs").event(new ClickEvent(Action.RUN_COMMAND, "/mdc start")).event(hover).create());
        player.spigot().sendMessage(new ComponentBuilder("2. ").color(ChatColor.GREEN).append("Stop counting mobs").event(new ClickEvent(Action.RUN_COMMAND, "/mdc stop")).event(hover).create());
        player.spigot().sendMessage(new ComponentBuilder("3. ").color(ChatColor.GREEN).append("Reset mob counter").event(new ClickEvent(Action.RUN_COMMAND, "/mdc reset")).event(hover).create());
        player.spigot().sendMessage(new ComponentBuilder("4. ").color(ChatColor.GREEN).append("Toggle scoreboard").event(new ClickEvent(Action.RUN_COMMAND, "/mdc toggle")).event(hover).create());
    }

    @Subcommand("start")
    @CommandPermission("vanillatweaks.mobdeathcount.start")
    public void start(Player player) {
        module.isCounting = true;
        if (!player.getScoreboard().equals(module.board)) {
            player.setScoreboard(module.board);
        }
        player.sendMessage(Lang.STARTED_COUNT.p());
    }

    @Subcommand("stop")
    @CommandPermission("vanillatweaks.mobdeathcount.stop")
    public void stop(Player player) {
        module.isCounting = false;
        player.sendMessage(Lang.STOPPED_COUNT.p());
    }

    @Subcommand("reset")
    @CommandPermission("vanillatweaks.mobdeathcount.reset")
    public void reset(Player player) {
        module.board.getEntries().forEach(module.board::resetScores);
        player.sendMessage(Lang.RESET_COUNT.p());
    }

    @Subcommand("toggle")
    @CommandPermission("vanillatweaks.mobdeathcount.toggle")
    public void toggle(Player player) {
        if (player.getScoreboard().equals(module.board))
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        else player.setScoreboard(module.board);
    }
}
