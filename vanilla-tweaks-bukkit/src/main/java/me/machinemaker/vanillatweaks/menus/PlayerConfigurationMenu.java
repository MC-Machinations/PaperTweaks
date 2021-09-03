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
package me.machinemaker.vanillatweaks.menus;

import cloud.commandframework.context.CommandContext;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.menus.parts.MenuPartLike;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class PlayerConfigurationMenu<S> extends ConfigurationMenu<S> {

    private final Function<Player, S> mapper;
    public PlayerConfigurationMenu(@NotNull Component title, @NotNull String commandPrefix, @NotNull List<MenuPartLike<S>> parts, @NotNull Function<Player, S> mapper) {
        super(title, commandPrefix, parts);
        this.mapper = mapper;
    }

    public void send(CommandContext<CommandDispatcher> context) {
        Player player = PlayerCommandDispatcher.from(context);
        context.getSender().sendMessage(build(this.mapper.apply(player)));
    }
}
