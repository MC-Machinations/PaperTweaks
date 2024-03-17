/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.durabilityping;

import com.google.inject.Inject;
import java.util.List;
import me.machinemaker.papertweaks.cloud.MetaKeys;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.parsers.setting.SettingArgumentPair;
import me.machinemaker.papertweaks.menus.PlayerConfigurationMenu;
import me.machinemaker.papertweaks.menus.options.BooleanMenuOption;
import me.machinemaker.papertweaks.menus.options.SelectableEnumMenuOption;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.settings.types.PlayerSetting;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;

import static me.machinemaker.papertweaks.adventure.Components.join;
import static me.machinemaker.papertweaks.cloud.parsers.setting.SettingArgumentPair.playerSettings;
import static me.machinemaker.papertweaks.cloud.parsers.setting.SettingArgumentPair.resetPlayerSettings;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;

@ModuleCommand.Info(value = "durabilityping", aliases = {"dping", "dp"}, i18n = "durability-ping", perm = "durabilityping")
class Commands extends ConfiguredModuleCommand {

    private final PlayerListener listener;
    private final Settings settings;
    private final Config config;
    private final PlayerConfigurationMenu menu;

    @Inject
    Commands(final PlayerListener listener, final Settings settings, final Config config) {
        this.listener = listener;
        this.settings = settings;
        this.config = config;
        this.menu = new PlayerConfigurationMenu(
            join(text(" ".repeat(18) + "DurabilityPing"), text(" / ", GRAY), text("Personal Settings" + " ".repeat(18) + "\n")),
            "/durabilityping config",
            List.of(
                BooleanMenuOption
                    .newBuilder("modules.durability-ping.config.hand-items", this.settings.getSetting(Settings.HAND_PING))
                    .extendedDescription("modules.durability-ping.config.hand-items.extended"),
                BooleanMenuOption.newBuilder("modules.durability-ping.config.armor-items", this.settings.getSetting(Settings.ARMOR_PING))
                    .extendedDescription("modules.durability-ping.config.armor-items.extended"),
                BooleanMenuOption
                    .newBuilder(text("Ping with Sound"), this.settings.getSetting(Settings.SOUND))
                    .previewAction(bool -> ClickEvent.runCommand("/durabilityping config preview_sound")),
                SelectableEnumMenuOption.of(Settings.DisplaySetting.class, "modules.durability-ping.config.display", this.settings.getSetting(Settings.DISPLAY))
            )
        );
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();
        final Command.Builder<CommandDispatcher> configBuilder = this.literal(builder, "config");

        this.register(configBuilder.handler(this.menu::send));
        this.register(configBuilder
            .apply(MetaKeys.hiddenCommand())
            .literal("preview_display")
            .required("displaySetting", enumParser(Settings.DisplaySetting.class))
            .handler(context -> context.<Settings.DisplaySetting>get("displaySetting").sendMessage(context.sender(), this.listener.createNotification(Material.ELYTRA, Material.ELYTRA.getMaxDurability() / 2)))
        );
        this.register(configBuilder
            .apply(MetaKeys.hiddenCommand())
            .literal("preview_sound")
            .handler(context -> context.sender().playSound(DurabilityPing.SOUND, Sound.Emitter.self()))
        );
        this.register(configBuilder
            .apply(MetaKeys.hiddenCommand())
            .required(SettingArgumentPair.PLAYER_SETTING_CHANGE_KEY, playerSettings(this.settings.index()))
            .handler(this.sync((context, player) -> {
                final SettingArgumentPair.SettingChange<Player, PlayerSetting<?>> change = context.get(SettingArgumentPair.PLAYER_SETTING_CHANGE_KEY);
                change.apply(player);
                this.listener.settingsCache.invalidate(player.getUniqueId());
                this.menu.send(context);
            }))
        );
        this.register(resetPlayerSettings(configBuilder, "modules.durability-ping.commands.config.reset", this.settings));

        this.config.createCommands(this, builder);
    }
}
