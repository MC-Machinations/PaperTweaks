package me.machinemaker.vanillatweaks.modules.teleportation.spawn;

import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import io.papermc.lib.PaperLib;
import me.machinemaker.vanillatweaks.cloud.CommandCooldownManager;
import me.machinemaker.vanillatweaks.cloud.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.UUID;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ModuleCommand {

    private final JavaPlugin plugin;
    private final CommandCooldownManager<CommandDispatcher, UUID> commandCooldownManager;
    private final Config config;

    @Inject
    Commands(JavaPlugin plugin, CommandCooldownManager<CommandDispatcher, UUID> commandCooldownManager, Config config) {
        this.plugin = plugin;
        this.commandCooldownManager = commandCooldownManager;
        this.config = config;
    }

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        var builder = manager.commandBuilder("spawn", RichDescription.translatable("modules.spawn.commands.root"))
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.spawn.current"))
                .senderType(PlayerCommandDispatcher.class);

        manager.command(builder.meta(commandCooldownManager.META_COOLDOWN_SUPPLIER_LENGTH_KEY, context -> Duration.ofSeconds(config.cooldown))
                .argument(WorldArgument.<CommandDispatcher>newBuilder("world").withDefaultDescription(RichDescription.translatable("Teleport to the spawnpoint of specified world")).asOptional())
                .handler(commandContext -> {
                    manager.taskRecipe().begin(commandContext).synchronous((context) -> {
                        Player player = PlayerCommandDispatcher.from(context);
                        World world = context.getOrDefault("world", player.getWorld());
                        context.getSender().sendMessage(translatable("modules.spawn.teleporting", GOLD));
                        if (this.config.delay > 0) {
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                PaperLib.teleportAsync(player, world.getSpawnLocation());
                            }, this.config.delay * 20);
                        } else {
                            PaperLib.teleportAsync(player, world.getSpawnLocation());
                        }
                    }).execute();
                })
        );
    }
}
