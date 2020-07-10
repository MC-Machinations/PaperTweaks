package me.machinemaker.vanillatweaks.spectatortoggle;

import co.aikar.commands.ConditionFailedException;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.GameMode;

public class SpectatorNightVision extends BaseModule {

    public SpectatorNightVision(VanillaTweaks plugin) {
        super(plugin, config -> config.spectatorConduitPower || config.spectatorNightVision);
        this.plugin.commandManager.getCommandConditions().addCondition("gamemode", c -> {
            if (!c.getIssuer().isPlayer()) throw new ConditionFailedException("Must be run by a player");
            if (c.getIssuer().getPlayer().getGameMode() != GameMode.valueOf(c.getConfig()))
                throw new ConditionFailedException("Command must be run in gamemode: " + c.getConfig());
        });
        this.registerCommands(new Commands(this));
    }

    @Override
    public void register() { }

    @Override
    public void unregister() { }
}
