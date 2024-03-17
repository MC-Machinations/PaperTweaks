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
package me.machinemaker.papertweaks.modules.survival.coordinateshud;

import com.google.inject.Inject;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.Style.style;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

@ModuleCommand.Info(value = "togglehud", aliases = "thud", descriptionKey = "modules.coordinates-hud.commands", infoOnRoot = false)
class Commands extends ModuleCommand {

    private final HUDRunnable hudRunnable;

    @Inject
    Commands(final HUDRunnable hudRunnable) {
        this.hudRunnable = hudRunnable;
    }

    @Override
    protected void registerCommands() {
        this.register(this.player()
            .permission(this.modulePermission("vanillatweaks.coordinateshud.togglehud"))
            .handler(this.sync((context, player) -> {
                if (this.hudRunnable.contains(player)) {
                    this.hudRunnable.setAndRemove(player);
                    context.sender().sendMessage(translatable("modules.coordinates-hud.hud-off", GREEN));
                    context.sender().sendActionBar(Component.empty());
                } else {
                    this.hudRunnable.setAndAdd(player);
                    context.sender().sendMessage(translatable("modules.coordinates-hud.hud-on", GREEN));
                }
            }))
        );

        this.register(this.builder()
            .literal("player")
            .required("players", MultiplePlayerSelectorParser.multiplePlayerSelectorParser(false))
            .permission(this.modulePermission("vanillatweaks.coordinateshud.togglehud.others"))
            .handler(this.sync(context -> {
                final MultiplePlayerSelector players = context.get("players");
                for (final Player player : players.values()) {
                    if (this.hudRunnable.contains(player)) {
                        this.hudRunnable.setAndRemove(player);
                        player.sendActionBar(Component.empty());
                    } else {
                        this.hudRunnable.setAndAdd(player);
                    }
                }
                context.sender().sendMessage(translatable("modules.coordinates-hud.hud-toggled-for", style(GRAY, ITALIC), text(players.values().size())));
            }))
        );
    }
}
