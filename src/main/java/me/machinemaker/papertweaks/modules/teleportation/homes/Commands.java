/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.machinemaker.papertweaks.modules.teleportation.homes;

import com.google.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import me.machinemaker.papertweaks.cloud.cooldown.CommandCooldown;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.db.dao.teleportation.homes.HomesDAO;
import me.machinemaker.papertweaks.db.model.teleportation.homes.Home;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.teleportation.back.Back;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.Command;
import org.incendo.cloud.key.CloudKey;

import static me.machinemaker.papertweaks.cloud.parsers.ParserFactory.homeDescriptor;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static org.incendo.cloud.component.DefaultValue.constant;
import static org.incendo.cloud.component.DefaultValue.parsed;
import static org.incendo.cloud.key.CloudKey.cloudKey;
import static org.incendo.cloud.parser.standard.StringParser.quotedStringParser;

@ModuleCommand.Info(value = "homes", i18n = "homes", perm = "homes")
class Commands extends ConfiguredModuleCommand {

    static final CloudKey<Void> HOME_COMMAND_COOLDOWN_KEY = cloudKey("papertweaks:home_cmd_cooldown");

    private final HomesDAO homesDAO;
    private final Config config;

    @Inject
    Commands(final HomesDAO homesDAO, final Config config) {
        this.homesDAO = homesDAO;
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        final CommandCooldown<CommandDispatcher> homeCooldown = CommandCooldown.<CommandDispatcher>builder(context -> Duration.ofSeconds(this.config.sethomeCooldown))
            .key(HOME_COMMAND_COOLDOWN_KEY)
            .notifier((context, cooldown, secondsLeft) -> context.commandContext().sender().sendMessage(translatable("modules.homes.commands.home.cooldown", RED, text(secondsLeft))))
            .build();

        this.register(
            this.literal(builder, "sethome")
                .optional("homeName", quotedStringParser(), constant("home"))
                .handler(context -> {
                    final Player player = PlayerCommandDispatcher.from(context);
                    final Map<String, Home> homes = this.homesDAO.getHomesForPlayer(player.getUniqueId());
                    if (homes.size() + 1 > this.config.defaultSetHomeLimit) {
                        context.sender().sendMessage(translatable("modules.homes.commands.sethome.too-many-homes", RED, text(this.config.defaultSetHomeLimit, YELLOW)));
                        return;
                    }
                    final String homeName = context.get("homeName");
                    if (homes.containsKey(homeName)) {
                        context.sender().sendMessage(translatable("modules.homes.commands.sethome.duplicate-name", TextColor.color(249, 104, 3), text(homeName, YELLOW)));
                        return;
                    }
                    this.homesDAO.insertHome(new Home(player.getUniqueId(), homeName, player.getLocation()));
                    context.sender().sendMessage(translatable("modules.homes.commands.sethome.success", GOLD, text(homeName, YELLOW)));
                })
        );
        this.register(
            this.literal(builder, "delhome")
                .optional("home", homeDescriptor(this.argumentFactory), parsed("home"))
                .handler(context -> {
                    final Home home = context.get("home");
                    this.homesDAO.deleteHome(home);
                    context.sender().sendMessage(translatable("modules.homes.commands.delhome.success", GOLD, text(home.getName(), YELLOW)));
                })
        );
        this.register(
            this.literal(builder, "rename")
                .required("home", homeDescriptor(this.argumentFactory))
                .required("newName", quotedStringParser())
                .handler(context -> {
                    final Player player = PlayerCommandDispatcher.from(context);
                    final Home home = context.get("home");
                    final String newName = context.get("newName");
                    final String oldName = home.getName();
                    final @Nullable Home possibleExisting = this.homesDAO.getPlayerHome(player.getUniqueId(), newName);
                    if (possibleExisting != null) {
                        context.sender().sendMessage(translatable("modules.homes.commands.rename.duplicate-name", TextColor.color(249, 104, 3), text(newName, YELLOW)));
                        return;
                    }
                    home.setName(newName);
                    this.homesDAO.updateHome(home);
                    context.sender().sendMessage(translatable("modules.homes.commands.rename.success", GOLD, text(oldName, YELLOW), text(newName, YELLOW)));
                })
        );
        this.register(
            this.literal(builder, "list")
                .senderType(PlayerCommandDispatcher.class)
                .handler(context -> {
                    final Map<String, Home> homes = this.homesDAO.getHomesForPlayer(context.sender().getUUID());
                    if (homes.isEmpty()) {
                        context.sender().sendMessage(translatable("modules.homes.commands.list.no-homes", RED));
                        return;
                    }
                    final List<String> names = List.copyOf(homes.keySet());
                    final TextComponent.Builder component = text();
                    for (int i = 0; i < names.size(); i++) {
                        final Home home = homes.get(names.get(i));
                        final @Nullable Location loc = home.getLocation();
                        if (loc == null) {
                            this.homesDAO.deleteHome(home);
                        } else {
                            if (i != 0) {
                                component.append(newline());
                            }
                            component.append(translatable("modules.homes.commands.list.success", GOLD, text(i + 1), text(names.get(i), YELLOW), text(loc.getBlockX()), text(loc.getBlockY()), text(loc.getBlockZ()), text(loc.getWorld().getName())));
                        }
                    }
                    context.sender().sendMessage(component);
                })
        );
        this.register(
            this.player("home")
                .apply(homeCooldown)
                .optional("home", homeDescriptor(this.argumentFactory), parsed("home"))
                .handler(this.sync((context, player) -> {
                    if (HomeTeleportRunnable.AWAITING_TELEPORT.containsKey(player.getUniqueId())) {
                        return;
                    }
                    final Home home = context.get("home");
                    if (home.getLocation() == null) {
                        this.homesDAO.deleteHome(home);
                        context.sender().sendMessage(translatable("modules.homes.commands.arguments.home.invalid", RED));
                        return;
                    }
                    if (!this.config.allowAcrossDimension && home.getLocation().getWorld() != player.getWorld()) {
                        context.sender().sendMessage(translatable("modules.homes.commands.home.fail.across-dimensions", RED));
                        return;
                    }
                    context.sender().sendMessage(translatable("modules.homes.commands.home.success", GOLD, text(home.getName(), YELLOW)));
                    if (this.config.sethomeDelay > 0) {
                        new HomeTeleportRunnable(player, home.getLocation(), this.config.sethomeDelay * 20, context.sender()).start();
                    } else {
                        Back.setBackLocation(player, player.getLocation()); // Store back location
                        if (home.getLocation().getChunk().isLoaded()) {
                            player.teleport(home.getLocation());
                        } else {
                            player.teleportAsync(home.getLocation());
                        }
                    }
                }))
        );
    }
}
