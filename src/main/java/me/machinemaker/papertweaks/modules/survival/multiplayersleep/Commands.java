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
package me.machinemaker.papertweaks.modules.survival.multiplayersleep;

import com.google.inject.Inject;
import java.util.List;
import me.machinemaker.papertweaks.adventure.Components;
import me.machinemaker.papertweaks.cloud.MetaKeys;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.cloud.parsers.setting.SettingArgumentFactory;
import me.machinemaker.papertweaks.menus.PlayerConfigurationMenu;
import me.machinemaker.papertweaks.menus.options.SelectableEnumMenuOption;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.settings.types.PlayerSetting;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;

import static me.machinemaker.papertweaks.cloud.parsers.setting.SettingArgumentFactory.playerSettings;
import static me.machinemaker.papertweaks.cloud.parsers.setting.SettingArgumentFactory.resetPlayerSettings;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;

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

        this.register(
            this.literal(builder, "sleeping")
                .handler(context -> {
                    final World world = PlayerCommandDispatcher.from(context).getWorld();
                    final SleepContext sleepContext = MultiplayerSleep.SLEEP_CONTEXT_MAP.computeIfAbsent(world.getUID(), uuid -> SleepContext.from(world));
                    final Component fullyAsleep;
                    if (!sleepContext.sleepingPlayers().isEmpty()) {
                        fullyAsleep = join(separator(text(", ", WHITE)), sleepContext.sleepingPlayers().stream().map(Player::displayName).toList());
                    } else {
                        fullyAsleep = translatable("modules.multiplayer-sleep.commands.sleeping.fully-asleep.empty", RED);
                    }
                    final Component almostAsleep;
                    if (!sleepContext.almostSleepingPlayers().isEmpty()) {
                        almostAsleep = join(separator(text(", ", WHITE)), sleepContext.almostSleepingPlayers().stream().map(Player::displayName).toList());
                    } else {
                        almostAsleep = translatable("modules.multiplayer-sleep.commands.sleeping.almost-asleep.empty", RED);
                    }
                    context.sender().sendMessage(translatable("modules.multiplayer-sleep.commands.sleeping.fully-asleep", GREEN, fullyAsleep));
                    context.sender().sendMessage(translatable("modules.multiplayer-sleep.commands.sleeping.almost-asleep", YELLOW, almostAsleep));
                })
        );
        this.register(configBuilder.handler(this.menu::send));
        this.register(configBuilder
            .apply(MetaKeys.hiddenCommand())
            .literal("preview_display")
            .required("displaySetting", enumParser(Settings.DisplaySetting.class))
            .handler(context -> {
                context.<Settings.DisplaySetting>get("displaySetting").preview(PlayerCommandDispatcher.from(context));
            })
        );
        this.register(configBuilder
            .apply(MetaKeys.hiddenCommand())
            .required(SettingArgumentFactory.PLAYER_SETTING_CHANGE_KEY, playerSettings(this.settings.index()))
            .handler(context -> {
                final SettingArgumentFactory.SettingChange<Player, PlayerSetting<?>> change = context.get(SettingArgumentFactory.PLAYER_SETTING_CHANGE_KEY);
                final Player player = PlayerCommandDispatcher.from(context);
                change.apply(player);
                this.menu.send(context);
            })
        );
        this.register(resetPlayerSettings(configBuilder, "modules.multiplayer-sleep.commands.config.reset", this.settings));
        // TODO if set to action bar or boss bar, don't wait for SleepContext#recalculate to send notifications

        this.config.createCommands(this, builder);
    }
}
