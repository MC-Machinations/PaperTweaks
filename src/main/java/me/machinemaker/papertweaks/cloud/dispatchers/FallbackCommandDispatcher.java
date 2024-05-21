/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2024 Machine_Maker
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
package me.machinemaker.papertweaks.cloud.dispatchers;

import java.util.Locale;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class FallbackCommandDispatcher extends CommandDispatcher {

    private final UUID uuid;

    protected FallbackCommandDispatcher(final CommandSender bukkitCommandSender) {
        super(bukkitCommandSender);
        if (bukkitCommandSender instanceof final Entity entity) {
            this.uuid = entity.getUniqueId();
        } else {
            this.uuid = UUID.randomUUID();
        }
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public Locale locale() {
        return Locale.US;
    }

    @Override
    public Audience audience() {
        return this.sender();
    }
}
