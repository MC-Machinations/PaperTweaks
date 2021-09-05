package me.machinemaker.vanillatweaks;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.PreCommand;
import org.bukkit.command.CommandSender;

public abstract class BaseModuleCommand<M extends BaseModule> extends BaseCommand {

    protected M module;

    protected BaseModuleCommand(M module) {
        this.module = module;
    }

    @PreCommand
    public boolean checkConfig(CommandSender sender) {
        if (!module.shouldEnable()) {
            sender.sendMessage(Lang.NOT_ENABLED.err());
            return true;
        }
        return false;
    }
}
