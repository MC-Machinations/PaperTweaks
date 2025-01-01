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
package me.machinemaker.papertweaks.cloud;

import org.bukkit.GameMode;
import org.incendo.cloud.Command;
import org.incendo.cloud.key.CloudKey;

import static org.incendo.cloud.key.CloudKey.cloudKey;

public final class MetaKeys {

    public static final CloudKey<Void> HIDDEN = cloudKey("papertweaks:hidden");

    public static <C> Command.Builder.Applicable<C> hiddenCommand() {
        return builder -> {
            builder.meta(HIDDEN, null);
            return builder;
        };
    }

    public static final CloudKey<GameMode> GAMEMODE_KEY = cloudKey("papertweaks:gamemode", GameMode.class);

    private MetaKeys() {
    }
}
