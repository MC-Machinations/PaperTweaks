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
package me.machinemaker.vanillatweaks.menus;

import cloud.commandframework.context.CommandContext;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.menus.parts.MenuPartLike;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerConfigurationMenu extends AbstractConfigurationMenu<Player> {

    public PlayerConfigurationMenu(@NotNull Component title, @NotNull String commandPrefix, @NotNull List<MenuPartLike<Player>> parts) {
        super(title, commandPrefix, parts);
    }

    public void send(@NotNull CommandContext<CommandDispatcher> context) {
        this.send(context.getSender(), PlayerCommandDispatcher.from(context));
    }

    @Override
    public void send(@NotNull Audience audience, @NotNull Player player) {
        if (audience instanceof Player || audience instanceof PlayerCommandDispatcher) {
            audience.sendMessage(this.build(player));
        } else {
            throw new IllegalArgumentException(audience + " isn't a valid audience for sending a configuration menu");
        }
    }
}
