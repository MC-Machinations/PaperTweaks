package me.machinemaker.vanillatweaks.mobcounting;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;

public class MobCounting extends BaseModule implements Listener {

    final Scoreboard board;
    final Objective objective;
    boolean isCounting = false;
    private Commands commands;

    public MobCounting(VanillaTweaks vanillaTweaks) {
        super(vanillaTweaks, config -> config.countMobDeaths);
        board = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        objective = board.registerNewObjective("mobDeathCount", "dummy", ChatColor.GOLD + "No. Mob Deaths");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.PLAYER || event.getEntity().getCustomName() != null || !isCounting) return;
        String entry = ChatColor.YELLOW + event.getEntity().getName();
        int currentScore = objective.getScore(entry).getScore();
        objective.getScore(entry).setScore(currentScore + 1);
    }

    @Override
    public void register() {
        this.commands = new Commands(this);
        this.registerCommands(commands);
        this.registerEvents(this);
    }

    @Override
    public void unregister() {
        this.unregisterCommands(commands);
        this.unregisterEvents(this);
    }
}
