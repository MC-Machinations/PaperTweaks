package me.machinemaker.vanillatweaks;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import me.machinemaker.vanillatweaks._managers.ModuleManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("vanillatweaks|vtweaks|vt")
public class VanillaTweaksCommand extends BaseCommand {

    @Dependency
    VanillaTweaksModules config;

    @Dependency
    ModuleManager manager;

    @Subcommand("reload")
    public void reload(CommandSender sender) {
        config.reload();
        manager.reload();
        Lang.reload();
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        sender.sendMessage(Lang.RELOAD.p());
    }
}
