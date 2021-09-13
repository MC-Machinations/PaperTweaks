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
package me.machinemaker.vanillatweaks.modules.survival.trackstats;

import com.google.common.base.Preconditions;
import net.kyori.adventure.translation.Translatable;
import net.kyori.adventure.util.Index;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntUnaryOperator;

enum Stat implements Translatable {

    DAMAGE_DEALT(Statistic.DAMAGE_DEALT, IntOps.DIVIDE_BY_TEN, "Damage Dealt", "tas_DmgDealt"),
    DAMAGE_TAKEN(Statistic.DAMAGE_TAKEN, IntOps.DIVIDE_BY_TEN, "Damage Taken", "tas_DmgTaken"),
    WALK_KM(Statistic.WALK_ONE_CM, IntOps.CM_TO_KM, "Walk (km)", "tas_WalkKm"),
    CROUCH_M(Statistic.CROUCH_ONE_CM, IntOps.CM_TO_M, "Crouch (m)", "tas_CrouchM"),
    SPRINT_KM(Statistic.SPRINT_ONE_CM, IntOps.CM_TO_KM, "Spring (km)", "tas_SprintKm"),
    WALK_ON_WATER_KM(Statistic.WALK_ON_WATER_ONE_CM, IntOps.CM_TO_KM, "Walk on water (km)", "tas_OnWtrKm"),
    FALL_M(Statistic.FALL_ONE_CM, IntOps.CM_TO_M, "Fall (m)", "tas_FallM"),
    CLIMB_M(Statistic.CLIMB_ONE_CM, IntOps.CM_TO_M, "Climb (im)", "tas_ClimbM"),
    FLY_KM(Statistic.FLY_ONE_CM, IntOps.CM_TO_KM, "Fly (km)", "tas_FlyKm"),
    WALK_UNDER_WATER_KM(Statistic.WALK_UNDER_WATER_ONE_CM, IntOps.CM_TO_KM, "Walk under water (km)", "tas_UndrWtrKm"),
    MINECART_KM(Statistic.MINECART_ONE_CM, IntOps.CM_TO_KM, "Minecart (km)", "tas_MinecartKm"),
    BOAT_KM(Statistic.BOAT_ONE_CM, IntOps.CM_TO_KM, "Boat (km)", "tas_BoatKm"),
    PIG_M(Statistic.PIG_ONE_CM, IntOps.CM_TO_M, "Pig (m)", "tas_PigM"),
    HORSE_KM(Statistic.HORSE_ONE_CM, IntOps.CM_TO_KM, "Horse (km)", "tas_HorseKm"),
    AVIATE_KM(Statistic.AVIATE_ONE_CM, IntOps.CM_TO_KM, "Elytra (km)", "tas_ElytraKm"),
    SWIM_KM(Statistic.SWIM_ONE_CM, IntOps.CM_TO_KM, "Swim (km)", "tas_SwimKm"),
    STRIDER_KM(Statistic.STRIDER_ONE_CM, IntOps.CM_TO_KM, "Strider (km)", "tas_StriderKm"),
    PLAY_TIME_HRS(Statistic.PLAY_ONE_MINUTE, IntOps.TICKS_TO_HOURS, "Play time (hrs)", "tas_PlayTimeHrs"),
    TOTAL_WORLD_TIME_HRS(Statistic.TOTAL_WORLD_TIME, IntOps.TICKS_TO_HOURS, "Total world time (hrs)", "tas_TotalTimeHrs"),
    TIME_SINCE_REST_MINS(Statistic.TIME_SINCE_REST, IntOps.TICKS_TO_MINUTES, "Time since sleep (mins)", "tas_SinceRstMins"),
    CROUCH_TIME_MINS(Statistic.SNEAK_TIME, IntOps.TICKS_TO_MINUTES, "Crouch time (mins)", "tas_CrouchMins");

    static final Index<String, Stat> OBJ_NAMES = Index.create(Stat.class, Stat::objName);

    private final Statistic stat;
    private final IntUnaryOperator operator;
    private final String name;
    private final String objName;

    Stat(Statistic stat, IntUnaryOperator operator, String name, String objName) {
        Preconditions.checkArgument(objName.length() <= 16, objName + " is greater than max 16 characters");
        this.stat = stat;
        this.operator = operator;
        this.name = name;
        this.objName = objName;
    }

    public @NotNull Statistic stat() {
        return this.stat;
    }

    public @NotNull String objName() {
        return this.objName;
    }

    public @NotNull String displayName() {
        return this.name;
    }

    public int operate(int i) {
        return this.operator.applyAsInt(i);
    }

    @Override
    public @NotNull String translationKey() {
        String key = "stat.minecraft.";
        if (this.stat == Statistic.PLAY_ONE_MINUTE) {
            key += "play_time";
        } else {
            key += this.stat.getKey().getKey();
        }
        return key;
    }
}
