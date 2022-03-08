/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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
package me.machinemaker.vanillatweaks.modules.survival.durabilityping;

import cloud.commandframework.arguments.standard.EnumArgument;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.arguments.SettingArgument;
import me.machinemaker.vanillatweaks.menus.PlayerConfigurationMenu;
import me.machinemaker.vanillatweaks.menus.options.BooleanMenuOption;
import me.machinemaker.vanillatweaks.menus.options.SelectableEnumMenuOption;
import me.machinemaker.vanillatweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Material;

import java.util.List;

import static me.machinemaker.vanillatweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@ModuleCommand.Info(value = "durabilityping", aliases = {"dping", "dp"}, i18n = "durability-ping", perm = "durabilityping")
class Commands extends ConfiguredModuleCommand {

    private static final PlayerConfigurationMenu MENU = new PlayerConfigurationMenu(
            join(text(" ".repeat(18) + "DurabilityPing"), text(" / ", GRAY), text("Personal Settings" + " ".repeat(18) + "\n")),
            "/durabilityping config",
            List.of(
                    BooleanMenuOption
                            .newBuilder("modules.durability-ping.config.hand-items", Settings.HAND_PING)
                            .extendedDescription("modules.durability-ping.config.hand-items.extended"),
                    BooleanMenuOption.newBuilder("modules.durability-ping.config.armor-items", Settings.ARMOR_PING)
                            .extendedDescription("modules.durability-ping.config.armor-items.extended"),
                    BooleanMenuOption
                            .newBuilder(text("Ping with Sound"), Settings.SOUND)
                            .previewAction(bool -> ClickEvent.runCommand("/durabilityping config preview_sound")),
                    SelectableEnumMenuOption.of(Settings.DisplaySetting.class, "modules.durability-ping.config.display", Settings.DISPLAY)
            )
    );
    private final PlayerListener listener;
    private final Settings settings;
    private final Config config;

    @Inject
    Commands(PlayerListener listener, Settings settings, Config config) {
        this.listener = listener;
        this.settings = settings;
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        var builder = this.player();
        final var configBuilder = literal(builder, "config");

        manager.command(configBuilder
                .handler(MENU::send)
        ).command(configBuilder.hidden()
                .literal("preview_display")
                .argument(EnumArgument.of(Settings.DisplaySetting.class, "displaySetting"))
                .handler(context -> context.<Settings.DisplaySetting>get("displaySetting").sendMessage(context.getSender(), this.listener.createNotification(Material.ELYTRA, Material.ELYTRA.getMaxDurability() / 2)))
        ).command(configBuilder.hidden()
                .literal("preview_sound")
                .handler((context -> context.getSender().playSound(DurabilityPing.SOUND, Sound.Emitter.self())))
        ).command(configBuilder.hidden()
                .argument(SettingArgument.playerSettings(this.settings.index()))
                .handler(sync((context, player) -> {
                    var change = context.get(SettingArgument.PLAYER_SETTING_CHANGE_KEY);
                    change.apply(player);
                    this.listener.settingsCache.invalidate(player.getUniqueId());
                    MENU.send(context);
                }))
        ).command(SettingArgument.resetPlayerSettings(configBuilder, "modules.durability-ping.commands.config.reset", this.settings));

        this.config.createCommands(this, builder);
    }
}
