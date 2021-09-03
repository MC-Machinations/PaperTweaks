package me.machinemaker.vanillatweaks.modules.teleportation.spawn;

import cloud.commandframework.Command;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import io.papermc.lib.PaperLib;
import me.machinemaker.vanillatweaks.cloud.cooldown.CommandCooldownManager;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.function.Consumer;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class TeleportRunnable extends BukkitRunnable {

    @Inject
    private static CommandCooldownManager<CommandDispatcher, UUID> cooldownManager;

    private final Command<CommandDispatcher> command;
    private final Player player;
    private final Audience audience;
    private final Location playerLoc;
    private final Location teleportLoc;
    private long tickDelay;
    private final Consumer<Player> callback;

    public TeleportRunnable(Command<CommandDispatcher> command, Player player, Audience audience, Location teleportLoc, long tickDelay, Consumer<Player> callback) {
        Preconditions.checkNotNull(command, "command cannot be null");
        this.command = command;
        this.player = player;
        this.playerLoc = player.getLocation();
        this.audience = audience;
        this.teleportLoc = teleportLoc;
        this.tickDelay = tickDelay;
        this.callback = callback;
    }

    @Override
    public void run() {
        if (player.isDead()) {
            this.cancel();
            this.callback.accept(this.player);
            return;
        }
        if (tickDelay <= 0) {
            PaperLib.teleportAsync(this.player, this.teleportLoc);
            this.cancel();
            this.callback.accept(this.player);
            return;
        }
        if (playerLoc.distanceSquared(player.getLocation()) >= 0.01) {
            audience.sendMessage(translatable("modules.spawn.teleporting.moved", RED));
            cooldownManager.invalidate(player.getUniqueId(), this.command);
            this.callback.accept(this.player);
            this.cancel();
            return;
        }

        tickDelay--;
    }
}
