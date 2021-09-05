package me.machinemaker.vanillatweaks.trackrawstats.stats.entities;

import me.machinemaker.vanillatweaks.trackrawstats.stats.IStat;

public enum KilledByEntity implements IStat {

    DeathHoglin("DthHoglin", "minecraft.killed_by:minecraft.hoglin", "Killed by Hoglin"),
    DeathPiglin("DthPiglin", "minecraft.killed_by:minecraft.piglin", "Killed by Piglin"),
    DeathStrider("DthStrider", "minecraft.killed_by:minecraft.strider", "Killed by Strider"),
    DeathZoglin("DthZoglin", "minecraft.killed_by:minecraft.zoglin", "Killed by Zoglin"),
    DeathBee("DthBee", "minecraft.killed_by:minecraft.bee", "Killed by Bee"),
    DeathBlaze("DthBlaze", "minecraft.killed_by:minecraft.blaze", "Killed by Blaze"),
    DeathCveSpider("DthCveSpider", "minecraft.killed_by:minecraft.cave_spider", "Killed by Cave Spider"),
    DeathCreeper("DthCreeper", "minecraft.killed_by:minecraft.creeper", "Killed by Creeper"),
    DeathDolphin("DthDolphin", "minecraft.killed_by:minecraft.dolphin", "Killed by Dolphin"),
    DeathDrowned("DthDrowned", "minecraft.killed_by:minecraft.drowned", "Killed by Drowned"),
    DeathEGuardian("DthEGuardian", "minecraft.killed_by:minecraft.elder_guardian", "Killed by Elder Guardian"),
    DeathEDragon("DthEDragon", "minecraft.killed_by:minecraft.ender_dragon", "Killed by Ender Dragon"),
    DeathEnderman("DthEnderman", "minecraft.killed_by:minecraft.enderman", "Killed by Enderman"),
    DeathEndermite("DthEndermite", "minecraft.killed_by:minecraft.endermite", "Killed by Endermite"),
    DeathEvoker("DthEvoker", "minecraft.killed_by:minecraft.evoker", "Killed by Evoker"),
    DeathGhast("DthGhast", "minecraft.killed_by:minecraft.ghast", "Killed by Ghast"),
    DeathGuardian("DthGuardian", "minecraft.killed_by:minecraft.guardian", "Killed by Guardian"),
    DeathHusk("DthHusk", "minecraft.killed_by:minecraft.husk", "Killed by Husk"),
    DeathIllusioner("DthIllusion", "minecraft.killed_by:minecraft.illusioner", "Killed by Illusioner"),
    DeathIronGolem("DthIronGolem", "minecraft.killed_by:minecraft.iron_golem", "Killed by Iron Golem"),
    DeathLlama("DthLlama", "minecraft.killed_by:minecraft.llama", "Killed by Llama"),
    DeathMagmaCube("DthMagmaCube", "minecraft.killed_by:minecraft.magma_cube", "Killed by Magma Cube"),
    DeathPhantom("DthPhantom", "minecraft.killed_by:minecraft.phantom", "Killed by Phantom"),
    DeathPigman("DthPigman", "minecraft.killed_by:minecraft.zombified_piglin", "Killed by Zombified Piglin"),
    DeathPolarBear("DthPolarBear", "minecraft.killed_by:minecraft.polar_bear", "Killed by Polar Bear"),
    DeathPufferfish("DthPuffish", "minecraft.killed_by:minecraft.pufferfish", "Killed by Pufferfish"),
    DeathRabbit("DthRabbit", "minecraft.killed_by:minecraft.rabbit", "Killed by Rabbit"),
    DeathShulker("DthShulker", "minecraft.killed_by:minecraft.shulker", "Killed by Shulker"),
    DeathSilverfish("DthSilvfish", "minecraft.killed_by:minecraft.silverfish", "Killed by Silverfish"),
    DeathSkeleton("DthSkeleton", "minecraft.killed_by:minecraft.skeleton", "Killed by Skeleton"),
    DeathSkeletonH("DthSkeletonH", "minecraft.killed_by:minecraft.skeleton_horse", "Killed by Skeleton Horse"),
    DeathSlime("DthSlime", "minecraft.killed_by:minecraft.slime", "Killed by Slime"),
    DeathSnowGolem("DthSnowGolem", "minecraft.killed_by:minecraft.snow_golem", "Killed by Snow Golem"),
    DeathSpider("DthSpider", "minecraft.killed_by:minecraft.spider", "Killed by Spider"),
    DeathStray("DthStray", "minecraft.killed_by:minecraft.stray", "Killed by Stray"),
    DeathTurtle("DthTurtle", "minecraft.killed_by:minecraft.turtle", "Killed by Turtle"),
    DeathVex("DthVex", "minecraft.killed_by:minecraft.vex", "Killed by Vex"),
    DeathVillager("DthVillager", "minecraft.killed_by:minecraft.villager", "Killed by Villager"),
    DeathVindicator("DthVindicatr", "minecraft.killed_by:minecraft.vindicator", "Killed by Vindicator"),
    DeathWitch("DthWitch", "minecraft.killed_by:minecraft.witch", "Killed by Witch"),
    DeathWither("DthWither", "minecraft.killed_by:minecraft.wither", "Killed by Wither"),
    DeathWSkeleton("DthWSkeleton", "minecraft.killed_by:minecraft.wither_skeleton", "Killed by Wither Skeleton"),
    DeathWolf("DthWolf", "minecraft.killed_by:minecraft.wolf", "Killed by Wolf"),
    DeathZombie("DthZombie", "minecraft.killed_by:minecraft.zombie", "Killed by Zombie"),
    DeathZombieHorse("DthZombieH", "minecraft.killed_by:minecraft.zombie_horse", "Killed by Zombie Horse"),
    DeathZombieVillager("DthZombieV", "minecraft.killed_by:minecraft.zombie_villager", "Killed by Zombie Villager"),
    DeathWandering("DthWandering", "minecraft.killed:minecraft.wandering_trader", "Killed by Wandering Trader"),
    DeathTraderLla("DthTraderLla", "minecraft.killed:minecraft.trader_llama", "Killed by Trader Llama"),
    DeathFox("DthFox", "minecraft.killed:minecraft.fox", "Killed by Fox"),
    DeathPanda("DthPanda", "minecraft.killed:minecraft.panda", "Killed by Panda"),
    DeathPillager("DthPillager", "minecraft.killed:minecraft.pillager", "Killed by Pillager"),
    DeathRavager("DthRavager", "minecraft.killed:minecraft.ravager", "Killed by Ravager");

    final String name;
    final String criteria;
    final String displayName;

    KilledByEntity(String name, String criteria, String displayName) {
        this.name = name;
        this.criteria = criteria;
        this.displayName = displayName;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getCriteria() {
        return this.criteria;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getCommandName() {
        return this.name();
    }


}
