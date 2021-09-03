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
package me.machinemaker.vanillatweaks.modules.survival.durabilityping;

import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.arguments.SettingArgument;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.menus.PlayerConfigurationMenu;
import me.machinemaker.vanillatweaks.menus.options.BooleanMenuOption;
import me.machinemaker.vanillatweaks.menus.options.EnumMenuOption;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.TextComponent.ofChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ModuleCommand {

    private final PlayerListener listener;
    private final Settings settings;
    private static final PlayerConfigurationMenu<Settings.Instance> MENU = new PlayerConfigurationMenu<>(
            ofChildren(text(" ".repeat(18) + "DurabilityPing"), text(" / ", GRAY), text("Personal Settings" + " ".repeat(18) + "\n")),
            "/durabilityping config",
            List.of(
                    BooleanMenuOption
                            .newBuilder("modules.durability-ping.config.hand-items", Settings.Instance::handPing, Settings.HAND_PING)
                            .extendedDescription("modules.durability-ping.config.hand-items.extended"),
                    BooleanMenuOption.newBuilder("modules.durability-ping.config.armor-items", Settings.Instance::armorPing, Settings.ARMOR_PING)
                            .extendedDescription("modules.durability-ping.config.armor-items.extended"),
                    BooleanMenuOption
                            .newBuilder(text("Ping with Sound"), Settings.Instance::sound, Settings.SOUND)
                            .previewAction(bool -> ClickEvent.runCommand("/durabilityping config preview_sound")),
                    EnumMenuOption.of(Settings.DisplaySetting.class, Settings.Instance::displaySetting, Settings.DISPLAY)
            ),
            Settings::from
    );

    @Inject
    Commands(PlayerListener listener, Settings settings) {
        this.listener = listener;
        this.settings = settings;
    }

    @Override
    protected void registerCommands(ModuleLifecycle lifecycle) {
        final var builder = manager.commandBuilder("durabilityping", RichDescription.translatable("modules.durability-ping.commands.root"), "dping", "dp");
        final var configBuilder = builder
                .senderType(PlayerCommandDispatcher.class)
                .permission(ModulePermission.of(lifecycle, "vanillatweaks.durabilityping.configure"))
                .literal("config", RichDescription.translatable("modules.durability-ping.commands.config"));

        manager
                .command(configBuilder
                        .handler(context -> {
                            Settings.Instance instance = Settings.from(PlayerCommandDispatcher.from(context));
                            context.getSender().sendMessage(MENU.build(instance));
                        })
                ).command(configBuilder.hidden()
                        .literal("preview_display")
                        .argument(EnumArgument.of(Settings.DisplaySetting.class, "displaySetting"))
                        .handler(context -> {
                            context.<Settings.DisplaySetting>get("displaySetting").sendMessage(context.getSender(), this.listener.createNotification(Material.ELYTRA, Material.ELYTRA.getMaxDurability() / 2));
                        })
                ).command(configBuilder.hidden()
                        .literal("preview_sound")
                        .handler((context -> context.getSender().playSound(DurabilityPing.SOUND, Sound.Emitter.self())))
                ).command(configBuilder.hidden()
                        .argument(SettingArgument.playerSettings(this.settings.index()))
                        .handler(context -> {
                            var change = context.get(SettingArgument.PLAYER_SETTING_CHANGE_KEY);
                            Player player = PlayerCommandDispatcher.from(context);
                            change.apply(player);
                            MENU.send(context);
                        })
                ).command(SettingArgument.reset(configBuilder, "modules.durability-ping.commands.config.reset", this.settings));
    }
}
