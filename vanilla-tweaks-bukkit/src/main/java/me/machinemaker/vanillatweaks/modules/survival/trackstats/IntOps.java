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
package me.machinemaker.vanillatweaks.modules.survival.trackstats;

import java.util.function.IntUnaryOperator;

final class IntOps {

    private IntOps() {
    }

    static final IntUnaryOperator DIVIDE_BY_TEN = i -> i / 10;
    static final IntUnaryOperator CM_TO_KM = i -> i / 100 / 1000;
    static final IntUnaryOperator CM_TO_M = i -> i / 100;
    static final IntUnaryOperator TICKS_TO_HOURS = i -> i / 20 / 60 / 60;
    static final IntUnaryOperator TICKS_TO_MINUTES = i -> i / 20 / 60;
}
