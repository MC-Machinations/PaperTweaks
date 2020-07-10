package me.machinemaker.vanillatweaks.trackrawstats.stats;

public enum Misc implements IStat {

    CraftBeacon("CraftBeacon", "minecraft.crafted:minecraft.beacon", "Craft Beacon"),
    CraftEndCrystal("CraftEndCryst", "minecraft.crafted:minecraft.end_crystal", "Craft End Crystal"),
    CraftConduit("CraftConduit", "minecraft.crafted:minecraft.conduit", "Craft Conduit"),
    CraftShulkerBox("CraftShulkBox", "minecraft.crafted:minecraft.shulker_box", "Craft Shulker Box"),
    UsePotion("UsePotion", "minecraft.used:minecraft.potion", "Use Potion"),
    UseTotem("UseTotem", "minecraft.used:minecraft.totem_of_undying", "Use Totem"),
    UseTorch("UseTorch", "minecraft.used:minecraft.torch", "Use Torch"),
    UseGoldApple("UseGoldApple", "minecraft.used:minecraft.golden_apple", "Use Golden Apple"),
    UseBonemeal("UseBonemeal", "minecraft.used:minecraft.bone_meal", "Use Bonemeal"),
    UseBucket("UseBucket", "minecraft.used:minecraft.bucket", "Buckets Filled"),
    UseWaterBucket("UseWatrBucket", "minecraft.used:minecraft.water_bucket", "Water Buckets Emptied"),
    UseLavaBucket("UseLavaBucket", "minecraft.used:minecraft.lava_bucket", "Lava Buckets Emptied"),
    UseSnowball("UseSnowball", "minecraft.used:minecraft.snowball", "Thrown Snowball"),
    UseEyeOfEnder("UseEyeOfEnder", "minecraft.used:minecraft.ender_eye", "Thrown Eye Of Ender"),
    UseEnderPearl("UseEnderPearl", "minecraft.used:minecraft.ender_pearl", "Thrown Ender Pearl"),
    UseTrident("UseTrident", "minecraft.used:minecraft.trident", "Thrown Trident"),
    UseBottleEnchanted("UseBottleEnch", "minecraft.used:minecraft.experience_bottle", "Thrown Bottle o' Enchanting"),
    UseFishingRod("UseFishingRod", "minecraft.used:minecraft.fishing_rod", "Cast Fishing Rod"),
    UseHoneyBottle("UseHoneyBottl", "minecraft.used:minecraft.honey_bottle", "Drink Honey Bottle"),
    BreakPick("BreakPick", "minecraft.broken:minecraft.diamond_pickaxe", "Break Diamond Pickaxe"),
    BreakAxe("BreakAxe", "minecraft.broken:minecraft.diamond_axe", "Break Diamond Axe"),
    BreakShovel("BreakShovel", "minecraft.broken:minecraft.diamond_shovel", "Break Diamond Shovel"),
    BreakSword("BreakSword", "minecraft.broken:minecraft.diamond_sword", "Break Diamond Sword"),
    BreakHoe("BreakHoe", "minecraft.broken:minecraft.diamond_hoe", "Break Diamond Hoe"),
    BreakNPick("BreakNPick", "minecraft.broken:minecraft.netherite_pickaxe", "Break Netherite Pickaxe"),
    BreakNAxe("BreakNAxe", "minecraft.broken:minecraft.netherite_axe", "Break Netherite Axe"),
    BreakNShovel("BreakNShovel", "minecraft.broken:minecraft.netherite_shovel", "Break Netherite Shovel"),
    BreakNSword("BreakNSword", "minecraft.broken:minecraft.netherite_sword", "Break Netherite Sword"),
    BreakNHoe("BreakNHoe", "minecraft.broken:minecraft.netherite_hoe", "Break Netherite Hoe"),
    BreakBow("BreakBow", "minecraft.broken:minecraft.bow", "Break Bow"),
    BreakShears("BreakShears", "minecraft.broken:minecraft.shears", "Break Shears"),
    MineDiamond("MineDiamond", "minecraft.mined:minecraft.diamond_ore", "Mine Diamond"),
    MineEmerald("MineEmerald", "minecraft.mined:minecraft.emerald_ore", "Mine Emerald"),
    MineQuartz("MineQuartz", "minecraft.mined:minecraft.nether_quartz_ore", "Mine Quartz"),
    MineRedstone("MineRedstone", "minecraft.mined:minecraft.redstone_ore", "Mine Redstone"),
    MineLapis("MineLapis", "minecraft.mined:minecraft.lapis_ore", "Mine Lapis"),
    MineIron("MineIron", "minecraft.mined:minecraft.iron_ore", "Mine Iron"),
    MineGold("MineGold", "minecraft.mined:minecraft.gold_ore", "Mine Gold"),
    MineNetherite("MineNetherite", "minecraft.mined:minecraft.ancient_debris", "Mine Ancient Debris");

    final String name;
    final String criteria;
    final String displayName;

    Misc(String name, String criteria, String displayName) {
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
