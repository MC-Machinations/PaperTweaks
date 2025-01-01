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
package me.machinemaker.papertweaks.menus;

import org.incendo.cloud.context.CommandContext;
import java.util.List;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.menus.parts.MenuPartLike;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PlayerConfigurationMenu extends AbstractConfigurationMenu<Player> {

    public PlayerConfigurationMenu(final Component title, final String commandPrefix, final List<MenuPartLike<Player>> parts) {
        super(title, commandPrefix, parts);
    }

    public void send(final CommandContext<CommandDispatcher> context) {
        this.send(context.sender(), PlayerCommandDispatcher.from(context));
    }

    @Override
    public void send(final Audience audience, final Player player) {
        if (audience instanceof Player || audience instanceof PlayerCommandDispatcher) {
            audience.sendMessage(this.build(player));
        } else {
            throw new IllegalArgumentException(audience + " isn't a valid audience for sending a configuration menu");
        }
    }
}
