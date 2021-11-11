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
package me.machinemaker.vanillatweaks.moonshine.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TextColor {

    String BLACK = "black";
    String DARK_BLUE = "dark_blue";
    String DARK_GREEN = "dark_green";
    String DARK_AQUA = "dark_aqua";
    String DARK_RED = "dark_red";
    String DARK_PURPLE = "dark_purple";
    String GOLD = "gold";
    String GRAY = "gray";
    String DARK_GRAY = "dark_gray";
    String BLUE = "blue";
    String GREEN = "green";
    String AQUA = "aqua";
    String RED = "red";
    String LIGHT_PURPLE = "light_purple";
    String YELLOW = "yellow";
    String WHITE = "white";

    String value();
}
