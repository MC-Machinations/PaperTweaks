package me.machinemaker.vanillatweaks.trackrawstats;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import com.google.common.collect.Sets;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.trackrawstats.stats.IStat;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class TrackRawStats extends BaseModule {

    Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
    private final Set<IStat> statTypes = Sets.newHashSet();
    private Commands commands;

    public TrackRawStats(VanillaTweaks plugin) {
        super(plugin, config -> config.trackRawStats);
        CommandCompletions<BukkitCommandCompletionContext> completions = plugin.commandManager.getCommandCompletions();
        plugin.commandManager.getCommandContexts().registerContext(Objective.class, context -> {
            StatType type = context.getResolvedArg("type", StatType.class);
            String text = context.popFirstArg();
            Objective objective = type.objectiveMap.get(text);
            if (objective == null) throw new InvalidCommandArgument(text + " is not a valid argument!");
            return objective;
        });

        completions.registerStaticCompletion("trs/stattypes", Arrays.stream(StatType.values()).map(StatType::name).map(String::toLowerCase).collect(Collectors.toSet()));
        completions.registerCompletion("trs/objective", context -> {
            StatType type = context.getContextValue(StatType.class);
            if (type == null) return Sets.newHashSet();
            return type.objectiveMap.keySet();
        });
        for (StatType type : StatType.values()) {
            statTypes.addAll(type.stats);
            type.stats.forEach(stat -> {
                try {
                    Objective obj = board.getObjective(stat.getName());
                    if (obj == null) obj = board.registerNewObjective(stat.getName(), stat.getCriteria(), stat.getDisplayName());
                    type.addToMap(stat, obj);
                } catch (IllegalArgumentException exception) {
                    this.plugin.getLogger().severe("Error loading objectives for TrackRawStats");
                    this.plugin.getLogger().severe("Name: " + stat.getName());
                    this.plugin.getLogger().severe("Criteria: " + stat.getCriteria());
                    this.plugin.getLogger().severe("DisplayName: " + stat.getDisplayName());
                }
            });
        }
    }

    @Override
    public void register() {
        this.commands = new Commands(this);
        this.registerCommands(commands);
    }

    @Override
    public void unregister() {
        this.unregisterCommands(commands);
    }
}
