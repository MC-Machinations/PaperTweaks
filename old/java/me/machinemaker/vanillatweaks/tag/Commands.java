package me.machinemaker.vanillatweaks.tag;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@CommandAlias("tag")
class Commands extends BaseModuleCommand<Tag> {

    final NamespacedKey afkKey = new NamespacedKey(this.module.plugin, "afk");

    public Commands(Tag module) {
        super(module);
    }

    @Subcommand("givetag")
    @CommandPermission("vanillatweaks.tag.givetag")
    @CommandCompletion("@players")
    public void giveTag(Player player, @Optional OnlinePlayer otherPlayer) {
        Player receive = player;
        if (otherPlayer != null) {
            receive = otherPlayer.player;
        }
        PersistentDataContainer container = receive.getPersistentDataContainer();
        if (container.has(afkKey, PersistentDataType.BYTE)) {
            player.sendMessage(Lang.PLAYER_IS_AFK.p().replace("%name%", receive.getDisplayName()));
            return;
        }
        this.module.setAsIt(receive);
        if (this.module.config.showMessages) {
            Bukkit.broadcastMessage(Lang.PLAYER_IS_IT.toString().replace("%name%", receive.getDisplayName()));
        }
    }

    @Subcommand("reset")
    @CommandPermission("vanillatweaks.tag.reset")
    @CommandCompletion("@players")
    public void resetPlayer(CommandSender sender, OnlinePlayer player) {
        if (player.getPlayer().getPersistentDataContainer().has(this.module.tagKey, PersistentDataType.BYTE)) {
            this.module.removeAsIt(player.getPlayer());
        }
    }
}
