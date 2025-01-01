/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2024-2025 Machine_Maker
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

import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.Locale;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.Nullable;

public class EntityCommandDispatcher extends CommandDispatcher {

    protected EntityCommandDispatcher(final CommandSourceStack sourceStack) {
        super(sourceStack);
    }

    @Override
    public @Nullable UUID getUUID() {
        return this.sender().getUniqueId();
    }

    @Override
    public Locale locale() {
        return Locale.US;
    }

    @Override
    public Entity sender() {
        return (Entity) super.sender();
    }
}
