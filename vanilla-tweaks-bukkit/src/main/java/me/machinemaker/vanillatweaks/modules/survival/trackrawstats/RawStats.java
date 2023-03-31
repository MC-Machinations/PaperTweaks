/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.vanillatweaks.modules.survival.trackrawstats;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.checkerframework.checker.nullness.qual.Nullable;

final class RawStats {

    static final Gson GSON = new Gson();
    static final Map<String, Tracked> OBJECTIVE_DATA = Stream.of(
        new Tracked.StatisticType("ts_Deaths", "minecraft.custom:minecraft.deaths", "Deaths"),
        new Tracked.StatisticType("ts_KillCount", "minecraft.custom:minecraft.player_kills", "Kill Count"),
        new Tracked.CriteriaType("ts_TotalKills", "totalKillCount", "Total Kills", "modules.track-raw-stats.stat.total-kill-count") {
            @Override
            int constructValue(final JsonObject object) {
                int playerKills = 0;
                int mobKills = 0;
                if (object.has("minecraft:custom")) {
                    final JsonObject mcCustom = object.getAsJsonObject("minecraft:custom");
                    if (mcCustom.has("minecraft:player_kills")) {
                        playerKills = mcCustom.getAsJsonPrimitive("minecraft:player_kills").getAsInt();
                    }
                    if (mcCustom.has("minecraft:mob_kills")) {
                        mobKills = mcCustom.getAsJsonPrimitive("minecraft:mob_kills").getAsInt();
                    }
                }
                return playerKills + mobKills == 0 ? -1 : playerKills + mobKills;
            }
        },
        new Tracked.StatisticType("ts_AnimalsBred", "minecraft.custom:minecraft.animals_bred", "Animals Bred"),
        new Tracked.StatisticType("ts_Aviate", "minecraft.custom:minecraft.aviate_one_cm", "Aviate"),
        new Tracked.StatisticType("ts_Brewing", "minecraft.custom:minecraft.interact_with_brewingstand", "Brewing"),
        new Tracked.StatisticType("ts_CakeEaten", "minecraft.custom:minecraft.eat_cake_slice", "Cake Eaten"),
        new Tracked.StatisticType("ts_ChestOpened", "minecraft.custom:minecraft.open_chest", "Chest Opened"),
        new Tracked.StatisticType("ts_Climb", "minecraft.custom:minecraft.climb_one_cm", "Climb"),
        new Tracked.StatisticType("ts_Crafts", "minecraft.custom:minecraft.interact_with_crafting_table", "Crafts"),
        new Tracked.StatisticType("ts_Crouch", "minecraft.custom:minecraft.crouch_one_cm", "Crouch"),
        new Tracked.StatisticType("ts_DamageDealt", "minecraft.custom:minecraft.damage_dealt", "Damage Dealt"),
        new Tracked.StatisticType("ts_DamageTaken", "minecraft.custom:minecraft.damage_taken", "Damage Taken"),
        new Tracked.StatisticType("ts_EnderChest", "minecraft.custom:minecraft.open_enderchest", "Ender Chest"),
        new Tracked.StatisticType("ts_Fall", "minecraft.custom:minecraft.fall_one_cm", "Fall"),
        new Tracked.StatisticType("ts_FishCaught", "minecraft.custom:minecraft.fish_caught", "Fish Caught"),
        new Tracked.StatisticType("ts_FlowerPotted", "minecraft.custom:minecraft.pot_flower", "Flower Potted"),
        new Tracked.StatisticType("ts_Fly", "minecraft.custom:minecraft.fly_one_cm", "Fly"),
        new Tracked.StatisticType("ts_FurnaceUsed", "minecraft.custom:minecraft.interact_with_furnace", "Furnace Used"),
        new Tracked.StatisticType("ts_HorseRode", "minecraft.custom:minecraft.horse_one_cm", "Horse Rode"),
        new Tracked.StatisticType("ts_EnchantItem", "minecraft.custom:minecraft.enchant_item", "Items Enchanted"),
        new Tracked.StatisticType("ts_Jump", "minecraft.custom:minecraft.jump", "Jump"),
        new Tracked.StatisticType("ts_MinecartRide", "minecraft.custom:minecraft.minecart_one_cm", "Minecart Ride"),
        new Tracked.StatisticType("ts_MobKills", "minecraft.custom:minecraft.mob_kills", "Mob Kills"),
        new Tracked.StatisticType("ts_PlayNoteblock", "minecraft.custom:minecraft.play_noteblock", "Play Noteblock"),
        new Tracked.StatisticType("ts_TuneNoteblock", "minecraft.custom:minecraft.tune_noteblock", "Tune Noteblock"),
        new Tracked.StatisticType("ts_PigRide", "minecraft.custom:minecraft.pig_one_cm", "Pig Ride"),
        new Tracked.StatisticType("ts_PlayTime", "minecraft.custom:minecraft.play_time", "Play Time"),
        new Tracked.StatisticType("ts_PlayerKills", "minecraft.custom:minecraft.player_kills", "Player Kills"),
        new Tracked.StatisticType("ts_RecordsPlayed", "minecraft.custom:minecraft.play_record", "Records Played"),
        new Tracked.StatisticType("ts_ShulkerBox", "minecraft.custom:minecraft.open_shulker_box", "Shulker Box"),
        new Tracked.StatisticType("ts_Sneak", "minecraft.custom:minecraft.sneak_time", "Sneak"),
        new Tracked.StatisticType("ts_Sprint", "minecraft.custom:minecraft.sprint_one_cm", "Sprint"),
        new Tracked.StatisticType("ts_Swim", "minecraft.custom:minecraft.swim_one_cm", "Swim"),
        new Tracked.StatisticType("ts_LastDeath", "minecraft.custom:minecraft.time_since_death", "Time Since Last Death"),
        new Tracked.StatisticType("ts_VillagerTrade", "minecraft.custom:minecraft.traded_with_villager", "Villager Trade"),
        new Tracked.StatisticType("ts_TrapChestOpen", "minecraft.custom:minecraft.trigger_trapped_chest", "Trap Chest Open"),
        new Tracked.StatisticType("ts_Walk", "minecraft.custom:minecraft.walk_one_cm", "Walk"),
        new Tracked.StatisticType("ts_CraftBeacon", "minecraft.crafted:minecraft.beacon", "Craft Beacon"),
        new Tracked.StatisticType("ts_CraftEndCryst", "minecraft.crafted:minecraft.end_crystal", "Craft End Crystal"),
        new Tracked.StatisticType("ts_CraftConduit", "minecraft.crafted:minecraft.conduit", "Craft Conduit"),
        new Tracked.StatisticType("ts_CraftShulkBox", "minecraft.crafted:minecraft.shulker_box", "Craft Shulker Box"),
        new Tracked.StatisticType("ts_UsePotion", "minecraft.used:minecraft.potion", "Use Potion"),
        new Tracked.StatisticType("ts_UseTotem", "minecraft.used:minecraft.totem_of_undying", "Use Totem"),
        new Tracked.StatisticType("ts_UseTorch", "minecraft.used:minecraft.torch", "Use Torch"),
        new Tracked.StatisticType("ts_UseGoldApple", "minecraft.used:minecraft.golden_apple", "Use Golden Apple"),
        new Tracked.StatisticType("ts_UseBonemeal", "minecraft.used:minecraft.bone_meal", "Use Bonemeal"),
        new Tracked.StatisticType("ts_UseBucket", "minecraft.used:minecraft.bucket", "Buckets Filled"),
        new Tracked.StatisticType("ts_UseWatrBucket", "minecraft.used:minecraft.water_bucket", "Water Buckets Emptied"),
        new Tracked.StatisticType("ts_UseLavaBucket", "minecraft.used:minecraft.lava_bucket", "Lava Buckets Emptied"),
        new Tracked.StatisticType("ts_UseSnowball", "minecraft.used:minecraft.snowball", "Thrown Snowball"),
        new Tracked.StatisticType("ts_UseEyeOfEnder", "minecraft.used:minecraft.ender_eye", "Thrown Eye Of Ender"),
        new Tracked.StatisticType("ts_UseEnderPearl", "minecraft.used:minecraft.ender_pearl", "Thrown Ender Pearl"),
        new Tracked.StatisticType("ts_UseTrident", "minecraft.used:minecraft.trident", "Thrown Trident"),
        new Tracked.StatisticType("ts_UseBottleEnch", "minecraft.used:minecraft.experience_bottle", "Thrown Bottle o' Enchanting"),
        new Tracked.StatisticType("ts_UseFishingRod", "minecraft.used:minecraft.fishing_rod", "Cast Fishing Rod"),
        new Tracked.StatisticType("ts_UseHoneyBottl", "minecraft.used:minecraft.honey_bottle", "Drink Honey Bottle"),
        new Tracked.StatisticType("ts_BreakPick", "minecraft.broken:minecraft.diamond_pickaxe", "Break Diamond Pickaxe"),
        new Tracked.StatisticType("ts_BreakAxe", "minecraft.broken:minecraft.diamond_axe", "Break Diamond Axe"),
        new Tracked.StatisticType("ts_BreakShovel", "minecraft.broken:minecraft.diamond_shovel", "Break Diamond Shovel"),
        new Tracked.StatisticType("ts_BreakSword", "minecraft.broken:minecraft.diamond_sword", "Break Diamond Sword"),
        new Tracked.StatisticType("ts_BreakHoe", "minecraft.broken:minecraft.diamond_hoe", "Break Diamond Hoe"),
        new Tracked.StatisticType("ts_BreakNPick", "minecraft.broken:minecraft.netherite_pickaxe", "Break Netherite Pickaxe"),
        new Tracked.StatisticType("ts_BreakNAxe", "minecraft.broken:minecraft.netherite_axe", "Break Netherite Axe"),
        new Tracked.StatisticType("ts_BreakNShovel", "minecraft.broken:minecraft.netherite_shovel", "Break Netherite Shovel"),
        new Tracked.StatisticType("ts_BreakNSword", "minecraft.broken:minecraft.netherite_sword", "Break Netherite Sword"),
        new Tracked.StatisticType("ts_BreakNHoe", "minecraft.broken:minecraft.netherite_hoe", "Break Netherite Hoe"),
        new Tracked.StatisticType("ts_BreakBow", "minecraft.broken:minecraft.bow", "Break Bow"),
        new Tracked.StatisticType("ts_BreakShears", "minecraft.broken:minecraft.shears", "Break Shears"),
        new Tracked.StatisticType("ts_MineCoal", "minecraft.mined:minecraft.coal_ore", "Mine Coal"),
        new Tracked.StatisticType("ts_MineDiamond", "minecraft.mined:minecraft.diamond_ore", "Mine Diamond"),
        new Tracked.StatisticType("ts_MineEmerald", "minecraft.mined:minecraft.emerald_ore", "Mine Emerald"),
        new Tracked.StatisticType("ts_MineQuartz", "minecraft.mined:minecraft.nether_quartz_ore", "Mine Quartz"),
        new Tracked.StatisticType("ts_MineRedstone", "minecraft.mined:minecraft.redstone_ore", "Mine Redstone"),
        new Tracked.StatisticType("ts_MineLapis", "minecraft.mined:minecraft.lapis_ore", "Mine Lapis"),
        new Tracked.StatisticType("ts_MineIron", "minecraft.mined:minecraft.iron_ore", "Mine Iron"),
        new Tracked.StatisticType("ts_MineGold", "minecraft.mined:minecraft.gold_ore", "Mine Gold"),
        new Tracked.StatisticType("ts_MineCopper", "minecraft.mined:minecraft.copper_ore", "Mine Copper"),
        new Tracked.StatisticType("ts_MineNetherite", "minecraft.mined:minecraft.ancient_debris", "Mine Ancient Debris"),
        new Tracked.StatisticType("ts_MineDCoal", "minecraft.mined:minecraft.deepslate_coal_ore", "Mine Deepslate Coal"),
        new Tracked.StatisticType("ts_MineDDiamond", "minecraft.mined:minecraft.deepslate_diamond_ore", "Mine Deepslate Diamond"),
        new Tracked.StatisticType("ts_MineDEmerald", "minecraft.mined:minecraft.deepslate_emerald_ore", "Mine Deepslate Emerald"),
        new Tracked.StatisticType("ts_MineDRedstone", "minecraft.mined:minecraft.deepslate_redstone_ore", "Mine Deepslate Redstone"),
        new Tracked.StatisticType("ts_MineDLapis", "minecraft.mined:minecraft.deepslate_lapis_ore", "Mine Deepslate Lapis"),
        new Tracked.StatisticType("ts_MineDIron", "minecraft.mined:minecraft.deepslate_iron_ore", "Mine Deepslate Iron"),
        new Tracked.StatisticType("ts_MineDGold", "minecraft.mined:minecraft.deepslate_gold_ore", "Mine Deepslate Gold"),
        new Tracked.StatisticType("ts_MineDCopper", "minecraft.mined:minecraft.deepslate_copper_ore", "Mine Deepslate Copper"),
        new Tracked.StatisticType("ts_KillHoglin", "minecraft.killed:minecraft.hoglin", "Kill Hoglin"),
        new Tracked.StatisticType("ts_KillPiglin", "minecraft.killed:minecraft.piglin", "Kill Piglin"),
        new Tracked.StatisticType("ts_KillStrider", "minecraft.killed:minecraft.strider", "Kill Strider"),
        new Tracked.StatisticType("ts_KillZoglin", "minecraft.killed:minecraft.zoglin", "Kill Zoglin"),
        new Tracked.StatisticType("ts_KillBee", "minecraft.killed:minecraft.bee", "Kill Bee"),
        new Tracked.StatisticType("ts_KillBat", "minecraft.killed:minecraft.bat", "Kill Bat"),
        new Tracked.StatisticType("ts_KillBlaze", "minecraft.killed:minecraft.blaze", "Kill Blaze"),
        new Tracked.StatisticType("ts_KillCveSpider", "minecraft.killed:minecraft.cave_spider", "Kill Cave Spider"),
        new Tracked.StatisticType("ts_KillChicken", "minecraft.killed:minecraft.chicken", "Kill Chicken"),
        new Tracked.StatisticType("ts_KillCod", "minecraft.killed:minecraft.cod", "Kill Cod"),
        new Tracked.StatisticType("ts_KillCow", "minecraft.killed:minecraft.cow", "Kill Cow"),
        new Tracked.StatisticType("ts_KillCreeper", "minecraft.killed:minecraft.creeper", "Kill Creeper"),
        new Tracked.StatisticType("ts_KillDolphin", "minecraft.killed:minecraft.dolphin", "Kill Dolphin"),
        new Tracked.StatisticType("ts_KillDonkey", "minecraft.killed:minecraft.donkey", "Kill Donkey"),
        new Tracked.StatisticType("ts_KillDrowned", "minecraft.killed:minecraft.drowned", "Kill Drowned"),
        new Tracked.StatisticType("ts_KillEGuardian", "minecraft.killed:minecraft.elder_guardian", "Kill Elder Guardian"),
        new Tracked.StatisticType("ts_KillEDragon", "minecraft.killed:minecraft.ender_dragon", "Kill Ender Dragon"),
        new Tracked.StatisticType("ts_KillEnderman", "minecraft.killed:minecraft.enderman", "Kill Enderman"),
        new Tracked.StatisticType("ts_KillEndermite", "minecraft.killed:minecraft.endermite", "Kill Endermite"),
        new Tracked.StatisticType("ts_KillEvoker", "minecraft.killed:minecraft.evoker", "Kill Evoker"),
        new Tracked.StatisticType("ts_KillGhast", "minecraft.killed:minecraft.ghast", "Kill Ghast"),
        new Tracked.StatisticType("ts_KillGuardian", "minecraft.killed:minecraft.guardian", "Kill Guardian"),
        new Tracked.StatisticType("ts_KillHorse", "minecraft.killed:minecraft.horse", "Kill Horse"),
        new Tracked.StatisticType("ts_KillHusk", "minecraft.killed:minecraft.husk", "Kill Husk"),
        new Tracked.StatisticType("ts_KillIllusion", "minecraft.killed:minecraft.illusioner", "Kill Illusioner"),
        new Tracked.StatisticType("ts_KillIronGolem", "minecraft.killed:minecraft.iron_golem", "Kill Iron Golem"),
        new Tracked.StatisticType("ts_KillLlama", "minecraft.killed:minecraft.llama", "Kill Llama"),
        new Tracked.StatisticType("ts_KillMagmaCube", "minecraft.killed:minecraft.magma_cube", "Kill Magma Cube"),
        new Tracked.StatisticType("ts_KillMooshroom", "minecraft.killed:minecraft.mooshroom", "Kill Mooshroom"),
        new Tracked.StatisticType("ts_KillMule", "minecraft.killed:minecraft.mule", "Kill Mule"),
        new Tracked.StatisticType("ts_KillOcelot", "minecraft.killed:minecraft.ocelot", "Kill Ocelot"),
        new Tracked.StatisticType("ts_KillParrot", "minecraft.killed:minecraft.parrot", "Kill Parrot"),
        new Tracked.StatisticType("ts_KillPhantom", "minecraft.killed:minecraft.phantom", "Kill Phantom"),
        new Tracked.StatisticType("ts_KillPig", "minecraft.killed:minecraft.pig", "Kill Pig"),
        new Tracked.StatisticType("ts_KillPigman", "minecraft.killed:minecraft.zombified_piglin", "Kill Zombified Piglin"),
        new Tracked.StatisticType("ts_KillPolarBear", "minecraft.killed:minecraft.polar_bear", "Kill Polar Bear"),
        new Tracked.StatisticType("ts_KillPuffish", "minecraft.killed:minecraft.pufferfish", "Kill Pufferfish"),
        new Tracked.StatisticType("ts_KillRabbit", "minecraft.killed:minecraft.rabbit", "Kill Rabbit"),
        new Tracked.StatisticType("ts_KillSalmon", "minecraft.killed:minecraft.salmon", "Kill Salmon"),
        new Tracked.StatisticType("ts_KillSheep", "minecraft.killed:minecraft.sheep", "Kill Sheep"),
        new Tracked.StatisticType("ts_KillShulker", "minecraft.killed:minecraft.shulker", "Kill Shulker"),
        new Tracked.StatisticType("ts_KillSilvfish", "minecraft.killed:minecraft.silverfish", "Kill Silverfish"),
        new Tracked.StatisticType("ts_KillSkeleton", "minecraft.killed:minecraft.skeleton", "Kill Skeleton"),
        new Tracked.StatisticType("ts_KillSkeletonH", "minecraft.killed:minecraft.skeleton_horse", "Kill Skeleton Horse"),
        new Tracked.StatisticType("ts_KillSlime", "minecraft.killed:minecraft.slime", "Kill Slime"),
        new Tracked.StatisticType("ts_KillSnowGolem", "minecraft.killed:minecraft.snow_golem", "Kill Snow Golem"),
        new Tracked.StatisticType("ts_KillSpider", "minecraft.killed:minecraft.spider", "Kill Spider"),
        new Tracked.StatisticType("ts_KillSquid", "minecraft.killed:minecraft.squid", "Kill Squid"),
        new Tracked.StatisticType("ts_KillStray", "minecraft.killed:minecraft.stray", "Kill Stray"),
        new Tracked.StatisticType("ts_KillTropifish", "minecraft.killed:minecraft.tropical_fish", "Kill Tropical Fish"),
        new Tracked.StatisticType("ts_KillTurtle", "minecraft.killed:minecraft.turtle", "Kill Turtle"),
        new Tracked.StatisticType("ts_KillVex", "minecraft.killed:minecraft.vex", "Kill Vex"),
        new Tracked.StatisticType("ts_KillVillager", "minecraft.killed:minecraft.villager", "Kill Villager"),
        new Tracked.StatisticType("ts_KillVindicatr", "minecraft.killed:minecraft.vindicator", "Kill Vindicator"),
        new Tracked.StatisticType("ts_KillWitch", "minecraft.killed:minecraft.witch", "Kill Witch"),
        new Tracked.StatisticType("ts_KillWither", "minecraft.killed:minecraft.wither", "Kill Wither"),
        new Tracked.StatisticType("ts_KillWSkeleton", "minecraft.killed:minecraft.wither_skeleton", "Kill Wither Skeleton"),
        new Tracked.StatisticType("ts_KillWolf", "minecraft.killed:minecraft.wolf", "Kill Wolf"),
        new Tracked.StatisticType("ts_KillZombie", "minecraft.killed:minecraft.zombie", "Kill Zombie"),
        new Tracked.StatisticType("ts_KillZombieH", "minecraft.killed:minecraft.zombie_horse", "Kill Zombie Horse"),
        new Tracked.StatisticType("ts_KillZombieV", "minecraft.killed:minecraft.zombie_villager", "Kill Zombie Villager"),
        new Tracked.StatisticType("ts_KillWandering", "minecraft.killed:minecraft.wandering_trader", "Kill Wandering Trader"),
        new Tracked.StatisticType("ts_KillTraderLla", "minecraft.killed:minecraft.trader_llama", "Kill Trader Llama"),
        new Tracked.StatisticType("ts_KillFox", "minecraft.killed:minecraft.fox", "Kill Fox"),
        new Tracked.StatisticType("ts_KillPanda", "minecraft.killed:minecraft.panda", "Kill Panda"),
        new Tracked.StatisticType("ts_KillPillager", "minecraft.killed:minecraft.pillager", "Kill Pillager"),
        new Tracked.StatisticType("ts_KillRavager", "minecraft.killed:minecraft.ravager", "Kill Ravager"),
        new Tracked.StatisticType("ts_KillGlowSquid", "minecraft.killed:minecraft.glow_squid", "Kill Glow Squid"),
        new Tracked.StatisticType("ts_KillAxolotl", "minecraft.killed:minecraft.axolotl", "Kill Axolotl"),
        new Tracked.StatisticType("ts_KillGoat", "minecraft.killed:minecraft.goat", "Kill Goat"),
        new Tracked.StatisticType("ts_DthHoglin", "minecraft.killed_by:minecraft.hoglin", "Killed by Hoglin"),
        new Tracked.StatisticType("ts_DthPiglin", "minecraft.killed_by:minecraft.piglin", "Killed by Piglin"),
        new Tracked.StatisticType("ts_DthStrider", "minecraft.killed_by:minecraft.strider", "Killed by Strider"),
        new Tracked.StatisticType("ts_DthZoglin", "minecraft.killed_by:minecraft.zoglin", "Killed by Zoglin"),
        new Tracked.StatisticType("ts_DthBee", "minecraft.killed_by:minecraft.bee", "Killed by Bee"),
        new Tracked.StatisticType("ts_DthBlaze", "minecraft.killed_by:minecraft.blaze", "Killed by Blaze"),
        new Tracked.StatisticType("ts_DthCveSpider", "minecraft.killed_by:minecraft.cave_spider", "Killed by Cave Spider"),
        new Tracked.StatisticType("ts_DthCreeper", "minecraft.killed_by:minecraft.creeper", "Killed by Creeper"),
        new Tracked.StatisticType("ts_DthDolphin", "minecraft.killed_by:minecraft.dolphin", "Killed by Dolphin"),
        new Tracked.StatisticType("ts_DthDrowned", "minecraft.killed_by:minecraft.drowned", "Killed by Drowned"),
        new Tracked.StatisticType("ts_DthEGuardian", "minecraft.killed_by:minecraft.elder_guardian", "Killed by Elder Guardian"),
        new Tracked.StatisticType("ts_DthEDragon", "minecraft.killed_by:minecraft.ender_dragon", "Killed by Ender Dragon"),
        new Tracked.StatisticType("ts_DthEnderman", "minecraft.killed_by:minecraft.enderman", "Killed by Enderman"),
        new Tracked.StatisticType("ts_DthEndermite", "minecraft.killed_by:minecraft.endermite", "Killed by Endermite"),
        new Tracked.StatisticType("ts_DthEvoker", "minecraft.killed_by:minecraft.evoker", "Killed by Evoker"),
        new Tracked.StatisticType("ts_DthGhast", "minecraft.killed_by:minecraft.ghast", "Killed by Ghast"),
        new Tracked.StatisticType("ts_DthGuardian", "minecraft.killed_by:minecraft.guardian", "Killed by Guardian"),
        new Tracked.StatisticType("ts_DthHusk", "minecraft.killed_by:minecraft.husk", "Killed by Husk"),
        new Tracked.StatisticType("ts_DthIllusion", "minecraft.killed_by:minecraft.illusioner", "Killed by Illusioner"),
        new Tracked.StatisticType("ts_DthIronGolem", "minecraft.killed_by:minecraft.iron_golem", "Killed by Iron Golem"),
        new Tracked.StatisticType("ts_DthLlama", "minecraft.killed_by:minecraft.llama", "Killed by Llama"),
        new Tracked.StatisticType("ts_DthMagmaCube", "minecraft.killed_by:minecraft.magma_cube", "Killed by Magma Cube"),
        new Tracked.StatisticType("ts_DthPhantom", "minecraft.killed_by:minecraft.phantom", "Killed by Phantom"),
        new Tracked.StatisticType("ts_DthPigman", "minecraft.killed_by:minecraft.zombified_piglin", "Killed by Zombified Piglin"),
        new Tracked.StatisticType("ts_DthPolarBear", "minecraft.killed_by:minecraft.polar_bear", "Killed by Polar Bear"),
        new Tracked.StatisticType("ts_DthPuffish", "minecraft.killed_by:minecraft.pufferfish", "Killed by Pufferfish"),
        new Tracked.StatisticType("ts_DthRabbit", "minecraft.killed_by:minecraft.rabbit", "Killed by Rabbit"),
        new Tracked.StatisticType("ts_DthShulker", "minecraft.killed_by:minecraft.shulker", "Killed by Shulker"),
        new Tracked.StatisticType("ts_DthSilvfish", "minecraft.killed_by:minecraft.silverfish", "Killed by Silverfish"),
        new Tracked.StatisticType("ts_DthSkeleton", "minecraft.killed_by:minecraft.skeleton", "Killed by Skeleton"),
        new Tracked.StatisticType("ts_DthSkeletonH", "minecraft.killed_by:minecraft.skeleton_horse", "Killed by Skeleton Horse"),
        new Tracked.StatisticType("ts_DthSlime", "minecraft.killed_by:minecraft.slime", "Killed by Slime"),
        new Tracked.StatisticType("ts_DthSnowGolem", "minecraft.killed_by:minecraft.snow_golem", "Killed by Snow Golem"),
        new Tracked.StatisticType("ts_DthSpider", "minecraft.killed_by:minecraft.spider", "Killed by Spider"),
        new Tracked.StatisticType("ts_DthStray", "minecraft.killed_by:minecraft.stray", "Killed by Stray"),
        new Tracked.StatisticType("ts_DthTurtle", "minecraft.killed_by:minecraft.turtle", "Killed by Turtle"),
        new Tracked.StatisticType("ts_DthVex", "minecraft.killed_by:minecraft.vex", "Killed by Vex"),
        new Tracked.StatisticType("ts_DthVillager", "minecraft.killed_by:minecraft.villager", "Killed by Villager"),
        new Tracked.StatisticType("ts_DthVindicatr", "minecraft.killed_by:minecraft.vindicator", "Killed by Vindicator"),
        new Tracked.StatisticType("ts_DthWitch", "minecraft.killed_by:minecraft.witch", "Killed by Witch"),
        new Tracked.StatisticType("ts_DthWither", "minecraft.killed_by:minecraft.wither", "Killed by Wither"),
        new Tracked.StatisticType("ts_DthWSkeleton", "minecraft.killed_by:minecraft.wither_skeleton", "Killed by Wither Skeleton"),
        new Tracked.StatisticType("ts_DthWolf", "minecraft.killed_by:minecraft.wolf", "Killed by Wolf"),
        new Tracked.StatisticType("ts_DthZombie", "minecraft.killed_by:minecraft.zombie", "Killed by Zombie"),
        new Tracked.StatisticType("ts_DthZombieH", "minecraft.killed_by:minecraft.zombie_horse", "Killed by Zombie Horse"),
        new Tracked.StatisticType("ts_DthZombieV", "minecraft.killed_by:minecraft.zombie_villager", "Killed by Zombie Villager"),
        new Tracked.StatisticType("ts_DthWandering", "minecraft.killed_by:minecraft.wandering_trader", "Killed by Wandering Trader"),
        new Tracked.StatisticType("ts_DthTraderLla", "minecraft.killed_by:minecraft.trader_llama", "Killed by Trader Llama"),
        new Tracked.StatisticType("ts_DthFox", "minecraft.killed_by:minecraft.fox", "Killed by Fox"),
        new Tracked.StatisticType("ts_DthPanda", "minecraft.killed_by:minecraft.panda", "Killed by Panda"),
        new Tracked.StatisticType("ts_DthPillager", "minecraft.killed_by:minecraft.pillager", "Killed by Pillager"),
        new Tracked.StatisticType("ts_DthRavager", "minecraft.killed_by:minecraft.ravager", "Killed by Ravager")
    ).collect(Collectors.toMap(Tracked::name, Function.identity(), (o, o1) -> o1, LinkedHashMap::new));

    private RawStats() {
    }

    static void registerStats(final Scoreboard board) {
        final Set<Tracked> toBeUpdated = OBJECTIVE_DATA.values().stream().filter(tracked -> tracked.register(board)).collect(Collectors.toUnmodifiableSet());

        if (!toBeUpdated.isEmpty()) {
            TrackRawStats.LOGGER.info("Detected {} missing stats that need their initial values set from player statistics", toBeUpdated.size());
            try {
                final Path statsFolder = Bukkit.getWorlds().get(0).getWorldFolder().toPath().resolve("stats");
                if (Files.notExists(statsFolder)) {
                    TrackRawStats.LOGGER.info("Could not find the stats folder in {}, skipping", statsFolder.getParent());
                    return;
                }
                TrackRawStats.LOGGER.info("Starting the stat transfer, depending on how many players have played on the server, this could take a while");
                try (final Stream<Path> statFiles = Files.list(statsFolder)) {
                    int playerCount = 0;
                    int statCount = 0;
                    for (final Path path : statFiles.toList()) {
                        if (!path.getFileName().toString().endsWith(".json")) {
                            return;
                        }
                        try {
                            boolean updatedPlayer = false;
                            final UUID uuid = UUID.fromString(path.getFileName().toString().split("\\.json")[0]);
                            final JsonObject stats = GSON.fromJson(Files.readString(path), JsonObject.class).getAsJsonObject("stats");
                            for (final Tracked obj : toBeUpdated) {
                                final @Nullable Objective objective = board.getObjective(obj.name()); // this should always exist, but check anyway
                                if (objective == null) {
                                    throw new IllegalStateException("This shouldn't happen, the objective for " + obj.name() + " could not be found");
                                }
                                final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                                if (player.hasPlayedBefore() && player.getName() != null) {
                                    final int score = obj.constructValue(stats);
                                    if (score > -1) {
                                        objective.getScore(player.getName()).setScore(score);
                                        updatedPlayer = true;
                                        statCount++;
                                    }
                                }
                            }
                            if (updatedPlayer) {
                                playerCount++;
                            }
                        } catch (final IllegalArgumentException ignored) {
                        } catch (final IOException | JsonSyntaxException e) {
                            TrackRawStats.LOGGER.warn("Could not read stats from {}, skipping", path, e);
                        }
                    }
                    TrackRawStats.LOGGER.info("Updated {} stats for {} players. Saving...", statCount, playerCount);
                    Bukkit.getWorlds().get(0).save();
                } catch (final IOException e) {
                    TrackRawStats.LOGGER.error("Something went wrong loading the initial values for stats", e);
                }
            } catch (final Exception e) {
                TrackRawStats.LOGGER.error("Something went wrong loading the initial values for stats", e);
            }
        }
    }

}
