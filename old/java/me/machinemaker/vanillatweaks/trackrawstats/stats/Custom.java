package me.machinemaker.vanillatweaks.trackrawstats.stats;

public enum Custom implements IStat {
    AnimalsBred("AnimalsBred", "minecraft.custom:minecraft.animals_bred", "Animals Bred"),
    ArmorPiecesCleaned("ArmorClean", "minecraft.custom:minecraft.clean_armor", "Armor Cleaned"),
    BannersCleaned("BannerClean", "minecraft.custom:minecraft.clean_banner", "Banners Cleaned"),
    BarrelsOpened("BarrelOpen", "minecraft.custom:minecraft.open_barrel", "Barrels Opened"),
    BellsRung("BellsRung", "minecraft.custom:minecraft_bell_ring", "Bells Rung"),
    CakeEaten("CakeEaten", "minecraft.custom:minecraft.eat_cake_slice", "Cake Eaten"),
    CauldronFill("CauldFill", "minecraft.custom:minecraft.fill_cauldron", "Cauldrons Filled"),
    ChestOpened("ChestOpened", "minecraft.custom:minecraft.open_chest", "Chest Opened"),
    DamageAbsorb("DamageAbsorb", "minecraft.custom:minecraft.damage_absorbed", "Damage Absorbed"),
    DamageBlock("DamageBlock", "minecraft.custom:minecraft.damage_blocked_by_shield", "Damage Blocked"),
    DamageDealt("DamageDealt", "minecraft.custom:minecraft.damage_dealt", "Damage Dealt"),
    DamageDealtAbsorbed("DamDelAbs", "minecraft.custom:minecraft.damage_dealt_absorbed", "Damage Dealt (Absorbed)"),
    DamageDealtResisted("DamDelRes", "minecraft.custom:minecraft.damage_dealt_resisted", "Damage Dealt (Resisted)"),
    DamageTaken("DamageTaken", "minecraft.custom:minecraft.damage_taken", "Damage Taken"),

    Dispenser("DispInsp", "minecraft.custom:minecraft.inspect_dispenser", "Dispensers Searched"),
    /* DISTANCES */
    Boat("Boat", "minecraft.custom:minecraft.boat_one_cm", "Boat"),
    Elytra("Aviate", "minecraft.custom:minecraft.aviate_one_cm", "Aviate"),
    HorseRode("HorseRode", "minecraft.custom:minecraft.horse_one_cm", "Horse Rode"),
    MinecartRide("MinecartRide", "minecraft.custom:minecraft.minecart_one_cm", "Minecart Ride"),
    PigRide("PigRide", "minecraft.custom:minecraft.pig_one_cm", "Pig Ride"),
    StriderRid("StridRide", "minecraft.custom:minecraft.strider_on_cm", "Strider Ride"),
    Climb("Climb", "minecraft.custom:minecraft.climb_one_cm", "Climb"),
    Crouch("Crouch", "minecraft.custom:minecraft.crouch_one_cm", "Crouch"),
    Fall("Fall", "minecraft.custom:minecraft.fall_one_cm", "Fall"),
    Fly("Fly", "minecraft.custom:minecraft.fly_one_cm", "Fly"),
    Sprint("Sprint", "minecraft.custom:minecraft.sprint_one_cm", "Sprint"),
    Swim("Swim", "minecraft.custom:minecraft.swim_one_cm", "Swim"),
    Walk("Walk", "minecraft.custom:minecraft.walk_one_cm", "Walk"),
    WalkOnWater("WalkOnWater", "minecraft.custom:minecraft.walk_on_water_one_cm", "Walk on Water"),
    WalkUnderWater("WalkUWater", "minecraft.custom:minecraft.walk_under_water_one_cm", "Walk under Water"),

    Dropper("DropInsp", "minecraft.custom:minecraft.inspect_dropper", "Droppers Searched"),
    EnderChest("EnderChest", "minecraft.custom:minecraft.open_enderchest", "Ender Chest"),
    FishCaught("FishCaught", "minecraft.custom:minecraft.fish_caught", "Fish Caught"),
    GamesQuit("GamesQuit", "minecraft.custom:minecraft:leave_game", "Games Quit"),
    /* INTERACTIONS */
    Hopper("HopperInsp", "minecraft.custom:minecraft.inspect_hopper", "Hoppers Searched"),
    Beacon("Beacon", "minecraft.custom:minecraft.interact_with_beacon", "Beacons Used"),
    BlastFurnace("BFurnace", "minecraft.custom:minecraft.interact_with_blast_furnace", "Blast Furnaces Used"),
    Brewing("Brewing", "minecraft.custom:minecraft.interact_with_brewingstand", "Brewing"),
    Campfire("Campfire", "minecraft.custom:minecraft.interact_with_campfire", "Campfires Used"),
    Cartography("Cartography", "minecraft.custom:minecraft.interact_with_cartography_table", "Cartography Tables Used"),
    Crafts("Crafts", "minecraft.custom:minecraft.interact_with_crafting_table", "Crafts"),
    FurnaceUsed("FurnaceUsed", "minecraft.custom:minecraft.interact_with_furnace", "Furnace Used"),
    Lectern("Lecterns", "minecraft.custom:minecraft.interact_with_lectern", "Lecterns Used"),
    Loom("Loom", "minecraft.custom:minecraft.interact_with_loom", "Looms Used"),
    SmithingTable("SmithingTable", "minecraft.custom:minecraft.interact_with_smithing_table", "Smithing Tables Used"),
    Smoker("Smoker", "minecraft.custom:minecraft.interact_with_smoker", "Smokers Used"),
    Stonecutter("Stonecutter", "minecraft.custom:minecraft.interact_with_stonecutter", "Stonecutters Used"),

    ItemsDropped("ItemDrop", "minecraft.custom:minecraft:drop", "Items Dropped"),
    EnchantItem("EnchantItem", "minecraft.custom:minecraft.enchant_item", "Items Enchanted"),
    Jump("Jump", "minecraft.custom:minecraft.jump", "Jump"),
    MobKills("MobKills", "minecraft.custom:minecraft.mob_kills", "Mob Kills"),
    RecordsPlayed("RecordsPlayed", "minecraft.custom:minecraft.play_record", "Records Played"),
    PlayNoteblock("PlayNoteblock", "minecraft.custom:minecraft.play_noteblock", "Play Noteblock"),
    TuneNoteblock("TuneNoteblock", "minecraft.custom:minecraft.tune_noteblock", "Tune Noteblock"),
    PlayerDeaths("PlayerDeaths", "minecraft.custom:minecraft:deaths", "Deaths"),
    FlowerPotted("FlowerPotted", "minecraft.custom:minecraft.pot_flower", "Flower Potted"),
    PlayerKills("PlayerKills", "minecraft.custom:minecraft.player_kills", "Player Kills"),
    RaidsTriggered("RaidTrig", "minecraft.custom:minecraft:raid_trigger", "Raids Triggered"),
    RaidsWon("RaidsWon", "minecraft.custom:minecraft:raid_win", "Raids Won"),
    ShulkerCleaned("ShulkClean", "minecraft.custom:minecraft:clean_shulker_box", "Shulker Boxes Cleaned"),
    ShulkerBox("ShulkerBox", "minecraft.custom:minecraft.open_shulker_box", "Shulker Box"),
    LastDeath("LastDeath", "minecraft.custom:minecraft.time_since_death", "Time Since Last Death"),
    Sneak("Sneak", "minecraft.custom:minecraft.sneak_time", "Sneak"),
    TalkToVillager("TalkVillager", "minecraft.custom:minecraft:talked_to_villager", "Talked to Villager"),
    TargetsHit("TargetsHit", "minecraft.custom:minecraft:target_hit", "Targets Hit"),
    PlayedMinutes("PlayedMinutes", "minecraft.custom:minecraft.play_one_minute", "Time Played"),
    TimesSlept("TimesSlept", "minecraft.custom:minecraft:sleep_in_bed", "Times Slept"),
    VillagerTrade("VillagerTrade", "minecraft.custom:minecraft.traded_with_villager", "Villager Trade"),
    TrapChestOpen("TrapChestOpen", "minecraft.custom:minecraft.trigger_trapped_chest", "Trap Chest Open"),
    WaterFromCauldron("WaterFromCaul", "minecraft.custom:minecraft.use_cauldron", "Water taken from Cauldrons");
    final String name;
    final String criteria;
    final String displayName;

    Custom(String name, String criteria, String displayName) {
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
