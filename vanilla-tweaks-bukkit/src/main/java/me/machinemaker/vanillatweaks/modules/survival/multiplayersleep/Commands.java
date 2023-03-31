/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.vanillatweaks.modules.survival.multiplayersleep;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.EnumArgument;
import com.google.inject.Inject;
import java.util.List;
import me.machinemaker.vanillatweaks.adventure.Components;
import me.machinemaker.vanillatweaks.cloud.arguments.SettingArgument;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.menus.PlayerConfigurationMenu;
import me.machinemaker.vanillatweaks.menus.options.SelectableEnumMenuOption;
import me.machinemaker.vanillatweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.settings.types.PlayerSetting;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@ModuleCommand.Info(value = "multiplayersleep", aliases = {"mpsleep", "mps"}, i18n = "multiplayer-sleep", perm = "multiplayersleep")
class Commands extends ConfiguredModuleCommand {


    private final Config config;
    private final Settings settings;
    private final PlayerConfigurationMenu menu;

    @Inject
    Commands(final Config config, final Settings settings) {
        this.config = config;
        this.settings = settings;
        this.menu = new PlayerConfigurationMenu(
            Components.join(text(" ".repeat(16) + "MultiplayerSleep"), text(" / ", GRAY), text("Personal Settings" + " ".repeat(16) + "\n")),
            "/multiplayersleep config",
            List.of(SelectableEnumMenuOption.of(Settings.DisplaySetting.class, "modules.multiplayer-sleep.settings.display", this.settings.getSetting(Settings.DISPLAY)))
        );
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();
        final Command.Builder<CommandDispatcher> configBuilder = this.literal(builder, "config");

        this.manager
            .command(this.literal(builder, "sleeping")
                .handler(context -> {
                    final World world = PlayerCommandDispatcher.from(context).getWorld();
                    final SleepContext sleepContext = MultiplayerSleep.SLEEP_CONTEXT_MAP.computeIfAbsent(world.getUID(), uuid -> SleepContext.from(world));
                    final Component fullyAsleep;
                    if (!sleepContext.sleepingPlayers().isEmpty()) {
                        fullyAsleep = join(separator(text(", ", WHITE)), sleepContext.sleepingPlayers().stream().map(p -> text(p.getDisplayName())).toList());
                    } else {
                        fullyAsleep = translatable("modules.multiplayer-sleep.commands.sleeping.fully-asleep.empty", RED);
                    }
                    final Component almostAsleep;
                    if (!sleepContext.almostSleepingPlayers().isEmpty()) {
                        almostAsleep = join(separator(text(", ", WHITE)), sleepContext.almostSleepingPlayers().stream().map(p -> text(p.getDisplayName())).toList());
                    } else {
                        almostAsleep = translatable("modules.multiplayer-sleep.commands.sleeping.almost-asleep.empty", RED);
                    }
                    context.getSender().sendMessage(translatable("modules.multiplayer-sleep.commands.sleeping.fully-asleep", GREEN, fullyAsleep));
                    context.getSender().sendMessage(translatable("modules.multiplayer-sleep.commands.sleeping.almost-asleep", YELLOW, almostAsleep));
                })
            ).command(configBuilder
                .handler(this.menu::send)
            ).command(configBuilder.hidden()
                .literal("preview_display")
                .argument(EnumArgument.of(Settings.DisplaySetting.class, "displaySetting"))
                .handler(context -> {
                    context.<Settings.DisplaySetting>get("displaySetting").preview(PlayerCommandDispatcher.from(context));
                })
            ).command(configBuilder.hidden()
                .argument(SettingArgument.playerSettings(this.settings.index()))
                .handler(context -> {
                    final SettingArgument.SettingChange<Player, PlayerSetting<?>> change = context.get(SettingArgument.PLAYER_SETTING_CHANGE_KEY);
                    final Player player = PlayerCommandDispatcher.from(context);
                    change.apply(player);
                    this.menu.send(context);
                })
            ).command(SettingArgument.resetPlayerSettings(configBuilder, "modules.multiplayer-sleep.commands.config.reset", this.settings));
        // TODO if set to action bar or boss bar, don't wait for SleepContext#recalculate to send notifications

        this.config.createCommands(this, builder);
    }
}
