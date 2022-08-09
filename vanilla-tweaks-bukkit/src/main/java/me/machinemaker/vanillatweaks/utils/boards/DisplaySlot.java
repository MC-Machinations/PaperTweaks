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
package me.machinemaker.vanillatweaks.utils.boards;

import org.bukkit.scoreboard.Objective;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum DisplaySlot {

    BELOW_NAME(org.bukkit.scoreboard.DisplaySlot.BELOW_NAME),
    PLAYER_LIST(org.bukkit.scoreboard.DisplaySlot.PLAYER_LIST),
    SIDEBAR(org.bukkit.scoreboard.DisplaySlot.SIDEBAR),
    NONE(null);

    private final org.bukkit.scoreboard.@Nullable DisplaySlot bukkitDisplaySlot;

    DisplaySlot(final org.bukkit.scoreboard.@Nullable DisplaySlot bukkitDisplaySlot) {
        this.bukkitDisplaySlot = bukkitDisplaySlot;
    }

    public void changeFor(final Objective objective) {
        objective.setDisplaySlot(this.bukkitDisplaySlot);
    }

    public boolean isDisplayedOn(final Objective objective) {
        return objective.getDisplaySlot() == this.bukkitDisplaySlot;
    }
}
