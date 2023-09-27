/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2022-2023 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.trackstats;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Scoreboard;

@SuppressWarnings("unused")
final class Stats {

    static final Map<String, CalculatedStat> REGISTRY = new LinkedHashMap<>();

    public static final CombinedStat ALL_COAL = createCombined("tas_MineCoal", "Mine All Coal").addMined(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE).build();
    public static final CombinedStat ALL_DIAMOND = createCombined("tas_MineDiamond", "Mine All Diamond").addMined(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE).build();
    public static final CombinedStat ALL_EMERALD = createCombined("tas_MineEmerald", "Mine All Emerald").addMined(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE).build();
    public static final CombinedStat ALL_REDSTONE = createCombined("tas_MineRedstone", "Mine All Redstone").addMined(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE).build();
    public static final CombinedStat ALL_LAPIS = createCombined("tas_MineLapis", "Mine All Lapis").addMined(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE).build();
    public static final CombinedStat ALL_IRON = createCombined("tas_MineIron", "Mine All Iron").addMined(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE).build();
    public static final CombinedStat ALL_GOLD = createCombined("tas_MineGold", "Mine All Gold").addMined(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE).build();
    public static final CombinedStat ALL_COPPER = createCombined("tas_MineCopper", "Mine All Copper").addMined(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE).build();

    public static final ScaledStat DAMAGE_DEALT = createScaled(Statistic.DAMAGE_DEALT, IntOps.DIVIDE_BY_TEN, "Damage Dealt", "tas_DmgDealt");
    public static final ScaledStat DAMAGE_TAKEN = createScaled(Statistic.DAMAGE_TAKEN, IntOps.DIVIDE_BY_TEN, "Damage Taken", "tas_DmgTaken");
    public static final ScaledStat WALK_KM = createScaled(Statistic.WALK_ONE_CM, IntOps.CM_TO_KM, "Walk (km)", "tas_WalkKm");
    public static final ScaledStat CROUCH_M = createScaled(Statistic.CROUCH_ONE_CM, IntOps.CM_TO_M, "Crouch (m)", "tas_CrouchM");
    public static final ScaledStat SPRINT_KM = createScaled(Statistic.SPRINT_ONE_CM, IntOps.CM_TO_KM, "Spring (km)", "tas_SprintKm");
    public static final ScaledStat WALK_ON_WATER_KM = createScaled(Statistic.WALK_ON_WATER_ONE_CM, IntOps.CM_TO_KM, "Walk on water (km)", "tas_OnWtrKm");
    public static final ScaledStat FALL_M = createScaled(Statistic.FALL_ONE_CM, IntOps.CM_TO_M, "Fall (m)", "tas_FallM");
    public static final ScaledStat CLIMB_M = createScaled(Statistic.CLIMB_ONE_CM, IntOps.CM_TO_M, "Climb (im)", "tas_ClimbM");
    public static final ScaledStat FLY_KM = createScaled(Statistic.FLY_ONE_CM, IntOps.CM_TO_KM, "Fly (km)", "tas_FlyKm");
    public static final ScaledStat WALK_UNDER_WATER_KM = createScaled(Statistic.WALK_UNDER_WATER_ONE_CM, IntOps.CM_TO_KM, "Walk under water (km)", "tas_UndrWtrKm");
    public static final ScaledStat MINECART_KM = createScaled(Statistic.MINECART_ONE_CM, IntOps.CM_TO_KM, "Minecart (km)", "tas_MinecartKm");
    public static final ScaledStat BOAT_KM = createScaled(Statistic.BOAT_ONE_CM, IntOps.CM_TO_KM, "Boat (km)", "tas_BoatKm");
    public static final ScaledStat PIG_M = createScaled(Statistic.PIG_ONE_CM, IntOps.CM_TO_M, "Pig (m)", "tas_PigM");
    public static final ScaledStat HORSE_KM = createScaled(Statistic.HORSE_ONE_CM, IntOps.CM_TO_KM, "Horse (km)", "tas_HorseKm");
    public static final ScaledStat AVIATE_KM = createScaled(Statistic.AVIATE_ONE_CM, IntOps.CM_TO_KM, "Elytra (km)", "tas_ElytraKm");
    public static final ScaledStat SWIM_KM = createScaled(Statistic.SWIM_ONE_CM, IntOps.CM_TO_KM, "Swim (km)", "tas_SwimKm");
    public static final ScaledStat STRIDER_KM = createScaled(Statistic.STRIDER_ONE_CM, IntOps.CM_TO_KM, "Strider (km)", "tas_StriderKm");
    public static final ScaledStat PLAY_TIME_HRS = createScaled(Statistic.PLAY_ONE_MINUTE, IntOps.TICKS_TO_HOURS, "Play time (hrs)", "tas_PlayTimeHrs");
    public static final ScaledStat TOTAL_WORLD_TIME_HRS = createScaled(Statistic.TOTAL_WORLD_TIME, IntOps.TICKS_TO_HOURS, "Total world time (hrs)", "tas_TotalTimeHrs");
    public static final ScaledStat TIME_SINCE_REST_MINS = createScaled(Statistic.TIME_SINCE_REST, IntOps.TICKS_TO_MINUTES, "Time since sleep (mins)", "tas_SinceRstMins");
    public static final ScaledStat CROUCH_TIME_MINS = createScaled(Statistic.SNEAK_TIME, IntOps.TICKS_TO_MINUTES, "Crouch time (mins)", "tas_CrouchMins");

    private Stats() {
    }

    private static ScaledStat createScaled(final Statistic stat, final IntUnaryOperator scaleFunction, final String displayName, final String objectiveName) {
        return new ScaledStat(stat, scaleFunction, displayName, objectiveName);
    }

    private static CombinedStat.Builder createCombined(final String objectiveName, final String displayName) {
        return new CombinedStat.Builder(objectiveName, displayName);
    }

    static void registerStats(final Scoreboard board) {
        for (final CalculatedStat stat : REGISTRY.values()) {
            if (board.getObjective(stat.objectiveName()) == null) {
                board.registerNewObjective(stat.objectiveName(), Criteria.DUMMY, stat.displayName());
            }
        }
    }
}
