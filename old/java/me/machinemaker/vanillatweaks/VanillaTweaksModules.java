package me.machinemaker.vanillatweaks;

import me.machinemaker.configmanager.BaseConfig;
import me.machinemaker.configmanager.annotations.NewConfig;
import me.machinemaker.configmanager.annotations.Path;
import me.machinemaker.configmanager.configs.ConfigFormat;

@NewConfig(name = "modules", fileName = "modules.yml", format = ConfigFormat.YAML)
public class VanillaTweaksModules extends BaseConfig {
    @Path("mobs.anti-creeper-grief")
    public Boolean antiCreeperGrief = false;

    @Path("mobs.anti-enderman-grief")
    public Boolean antiEndermanGrief = false;

    @Path("mobs.anti-ghast-grief")
    public Boolean antiGhastGrief = false;

    @Path("mobs.double-shulker-shells")
    public Boolean doubleShulkerShells = false;

    @Path("mobs.dragon-drops")
    public Boolean dragonDrops = false;

    @Path("mobs.larger-phantoms")
    public Boolean largerPhantoms = false;

    @Path("mobs.more-mob-heads")
    public Boolean moreMobHeads = false;

    @Path("mobs.silence-mobs")
    public Boolean silenceMobs = false;

    @Path("mobs.count-mob-deaths")
    public Boolean countMobDeaths = false;

    // Item Tools
    @Path("item-tools.armor-statues")
    public Boolean armorStatues = false;

    @Path("item-tools.item-averages")
    public Boolean itemAverages = false;

    @Path("item-tools.kill-empty-boats")
    public Boolean killEmptyBoats = false;

    @Path("item-tools.redstone-rotation-wrench")
    public Boolean redstoneRotationWrench = false;

    @Path("item-tools.terracotta-rotation-wrench")
    public Boolean terracottaRotationWrench = false;

    @Path("item-tools.durability-ping")
    public Boolean durabilityPing = false;

    // Utilities
    @Path("utilities.afk-display")
    public Boolean afkDisplay = false;

    @Path("utilities.sethome")
    public Boolean setHome = false;

    @Path("utilities.multiplayer-sleep")
    public Boolean multiplayerSleep = false;

    @Path("utilities.player-graves")
    public Boolean playerGraves = false;

    @Path("utilities.tag")
    public Boolean tag = false;

    @Path("utilities.thunder-shrine")
    public Boolean thunderShrine = false;

    @Path("utilities.track-raw-stats")
    public Boolean trackRawStats = false;

    @Path("utilities.track-stats")
    public Boolean trackStats = false;

    @Path("utilities.nether-portal-coords")
    public Boolean netherPortalCoords = false;

    @Path("utilities.coordinates-hud")
    public Boolean coordinatesHud = false;

    @Path("utilities.spawning-spheres")
    public Boolean spawningSpheres = false;

    @Path("utilities.player-head-drops")
    public Boolean playerHeadDrops = false;

    @Path("utilities.classic-fishing-loot")
    public Boolean classicFishingLoot = false;

    @Path("utilities.spectator-night-vision")
    public Boolean spectatorNightVision = false;

    @Path("utilities.spectator-conduit-power")
    public Boolean spectatorConduitPower = false;

    @Path("utilities.real-time-clock")
    public Boolean realTimeClock = false;

    @Path("utilities.persist-head-data")
    public Boolean persistHeadData = false;

    @Path("villagers.villagerdeathmessage")
    public Boolean villagerDeathMsgs = false;

    @Path("villagers.pillagertools")
    public Boolean pillagerTools = false;

    @Path("villagers.customvillagershops")
    public Boolean customVillagerShops = false;

    @Path("villagers.workstationhighlights")
    public Boolean workstationHighlights = false;

    @Path("villagers.wanderingtrades")
    public Boolean wanderingTrades = false;

    @Path("survival.unlock-all-recipes")
    public Boolean unlockAllRecipes = false;

    @Path("experimental.confetti-creepers")
    public Boolean confettiCreepers = false;

    @Path("experimental.xp-management")
    public Boolean xpManagement = false;
}
