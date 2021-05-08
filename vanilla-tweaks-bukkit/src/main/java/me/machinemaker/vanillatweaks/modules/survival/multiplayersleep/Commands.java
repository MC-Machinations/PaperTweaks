/*
 * VanillaTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021 Machine_Maker
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

import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.minecraft.extras.RichDescription;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.arguments.SettingArgument;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.menus.PlayerConfigurationMenu;
import me.machinemaker.vanillatweaks.menus.options.EnumMenuOption;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.TextComponent.ofChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ModuleCommand {

    private static final PlayerConfigurationMenu<Settings.DisplaySetting> MENU = new PlayerConfigurationMenu<>(
            ofChildren(text(" ".repeat(16) + "MultiplayerSleep"), text(" / ", GRAY), text("Personal Settings" + " ".repeat(16) + "\n")),
            "/multiplayersleep config",
            List.of(EnumMenuOption.of(Settings.DisplaySetting.class, Function.identity(), Settings.DISPLAY)),
            Settings.DISPLAY::getOrDefault
    );

    private final Settings settings;

    @Inject
    Commands(Settings settings) {
        this.settings = settings;
    }

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        final var builder = manager.commandBuilder("multiplayersleep", RichDescription.translatable("modules.multiplayer-sleep.commands.root"), "mpsleep", "mps");
        final var configBuilder = builder
                .senderType(PlayerCommandDispatcher.class)
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.multiplayersleep.configure"))
                .literal("config", RichDescription.translatable("modules.multiplayer-sleep.commands.config"));

        manager
                .command(builder
                        .senderType(PlayerCommandDispatcher.class)
                        .permission(ModulePermission.of(lifecycle, "vanillatweaks.multiplayersleep.list-sleeping"))
                        .literal("sleeping", RichDescription.translatable("modules.multiplayer-sleep.commands.players-sleeping"))
                        .handler(context -> {
                            World world = PlayerCommandDispatcher.from(context).getWorld();
                            SleepContext sleepContext = MultiplayerSleep.SLEEP_CONTEXT_MAP.computeIfAbsent(world.getUID(), uuid -> SleepContext.from(world));
                            Component fullyAsleep;
                            if (!sleepContext.sleepingPlayers().isEmpty()) {
                                fullyAsleep = join(text(", ", WHITE), sleepContext.sleepingPlayers().stream().map(p -> text(p.getDisplayName())).toList());
                            } else {
                                fullyAsleep = translatable("modules.multiplayer-sleep.commands.players-sleeping.fully-asleep.empty", RED);
                            }
                            Component almostAsleep;
                            if (!sleepContext.almostSleepingPlayers().isEmpty()) {
                                almostAsleep = join(text(", ", WHITE), sleepContext.almostSleepingPlayers().stream().map(p -> text(p.getDisplayName())).toList());
                            } else {
                                almostAsleep = translatable("modules.multiplayer-sleep.commands.players-sleeping.almost-asleep.empty", RED);
                            }
                            context.getSender().sendMessage(translatable("modules.multiplayer-sleep.commands.players-sleeping.fully-asleep", GREEN, fullyAsleep));
                            context.getSender().sendMessage(translatable("modules.multiplayer-sleep.commands.players-sleeping.almost-asleep", YELLOW, almostAsleep));
                        })
                ).command(configBuilder
                        .handler(context -> {
                            Settings.DisplaySetting setting = Settings.DISPLAY.getOrDefault(PlayerCommandDispatcher.from(context));
                            context.getSender().sendMessage(MENU.build(setting));
                        })
                ).command(configBuilder.hidden()
                        .literal("preview_display")
                        .argument(EnumArgument.of(Settings.DisplaySetting.class, "displaySetting"))
                        .handler(context -> {
                            context.<Settings.DisplaySetting>get("displaySetting").preview(PlayerCommandDispatcher.from(context));
                        })
                ).command(configBuilder.hidden()
                        .argument(SettingArgument.playerSettings(this.settings.index()))
                        .handler(context -> {
                            var change = context.get(SettingArgument.PLAYER_SETTING_CHANGE_KEY);
                            Player player = PlayerCommandDispatcher.from(context);
                            change.apply(player);
                            MENU.send(context);
                        })
                ).command(SettingArgument.reset(configBuilder, "modules.multiplayer-sleep.commands.config.reset", this.settings));
        // TODO if set to action bar or boss bar, don't wait for SleepContext#recalculate to send notifications
    }
}
