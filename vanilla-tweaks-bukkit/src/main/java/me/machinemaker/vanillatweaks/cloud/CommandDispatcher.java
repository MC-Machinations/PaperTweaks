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
package me.machinemaker.vanillatweaks.cloud;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for representing command senders.
 */
public abstract class CommandDispatcher implements Audience, ForwardingAudience.Single {

    private final CommandSender bukkitCommandSender;
    private final Audience audience;

    protected CommandDispatcher(CommandSender bukkitCommandSender, Audience audience) {
        this.bukkitCommandSender = bukkitCommandSender;
        this.audience = audience;
    }

    /**
     * The Bukkit command sender.
     *
     * @return the Bukkit command sender
     */
    public CommandSender sender() {
        return bukkitCommandSender;
    }

    /**
     * The adventure-api audience for sending messages.
     *
     * @return the adventure-api audience
     */
    @NotNull
    @Override
    public final Audience audience() {
        return audience;
    }

}
