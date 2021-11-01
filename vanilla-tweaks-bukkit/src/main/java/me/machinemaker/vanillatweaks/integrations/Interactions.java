/*
 * GNU General Public License v3
 *
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
package me.machinemaker.vanillatweaks.integrations;

import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Interactions {

    private static final List<Handler> HANDLERS = new ArrayList<>();

    private Interactions() {
    }

    public static void registerHandler(Handler handler) {
        HANDLERS.add(handler);
    }

    public static boolean checkInteraction(PlayerInteractEvent event) {
        return HANDLERS.stream().allMatch(handler -> handler.test(event));
    }

    @FunctionalInterface
    public interface Handler extends Predicate<PlayerInteractEvent> {

    }
}
