package me.machinemaker.vanillatweaks;

import me.machinemaker.configmanager.configs.YamlConfig;
import org.bukkit.ChatColor;

public enum Lang {
    PREFIX("&8[&9V&1T&8] &r"),
    ERROR_PREFIX("&8[&4V&cT&8] &r"),

    RELOAD("&aConfiguration/Modules/Lang reloaded. &cIf you are overriding commands from this plugin with commands from another plugin, you should do a full reload of the server."),
    NOT_ENABLED("&cThis module is not enabled!"),

    SCOREBOARD_ON("&aTurned on %board% scoreboard."),
    SCOREBOARD_OFF("&eTurned off %board% scoreboard."),

    // CoordinatesHUD
    HUD_ON("coordinateshud.", "&aHUD toggled ON."),
    HUD_OFF("coordinateshud.", "&aHUD toggled OFF."),

    // DurabilityPing
    DP_TOGGLED_ON("durabilityping.", "&aDurability ping toggled on"),
    DP_TOGGLED_OFF("durabilityping.", "&aDurability ping toggled off"),

    // MobCounting
    STARTED_COUNT("mobcounting.", "&aStarted counting mob deaths..."),
    STOPPED_COUNT("mobcounting.", "&aStopped counting mob deaths."),
    RESET_COUNT("mobcounting.", "&eMob count reset!"),

    // PlayerGraves
    GRAVE_AT("playergraves.", "&eYour grave is at x=%x% y=%y%, z=%z%"),
    RETRIEVED_ITEMS("playergraves.", "&eRetrieved items."),

    // SetHome
    HOME_LIMIT("sethome.", "&eYou have reached the limit for setting your home."),
    HOME_SET("sethome.", "&aHome set"),
    NO_HOME_SET("sethome.", "&eYou have not set a home yet."),
    HOME_COOLDOWN("sethome.", "&eStill on cooldown: %time% seconds left."),
    NO_DIMENSIONAL("sethome.", "&eYou cannot teleport to your home across dimensions"),
    GO_TO_HOME("sethome.", "&aTeleported to home..."),
    PLAYER_NOT_FOUND("sethome.admin.", "&eSethome info for %player% not found"),
    PLAYER_RESET("sethome.admin.", "&aReset sethome limit for %player%"),

    // SpawningSpheres
    COLOR_ALREADY_DISPLAYED("spawningspheres.", "&eThe %color% sphere is already displayed!"),
    DISPLAYED_SPHERE("spawningspheres.", "&aDisplayed the %color% sphere."),
    COLOR_ALREADY_REMOVED("spawningspheres.", "&eThe %color% sphere has already been removed!"),
    SPHERE_ERROR("spawningspheres.", "&eCould not find a %color% sphere. Make sure you are in the same world and they are loaded."),
    REMOVED_SPHERE("spawningspheres.", "&eThe %color% sphere has been removed."),

    // Thundershrine
    NO_STAND_FOUND("thundershrine.", "&eCould not find a valid armor stand near you!"),
    NO_SHRINE("thundershrine.", "&eCould not find a shrine owned by you!"),
    REMOVED_SHRINE("thundershrine.", "&aRemoved the shrine."),
    STORM_STARTED("thundershrine.", "&cA great storm has been initiated by the ritual."),

    // Kill Empty Boats
    KILL_BOATS("killemptyboats.", "&eRemoved %num% empty boats(s)."),

    // Villager Death Messages
    VILLAGER_DEATH("villagerdeathmessages.", "&cA villager has died! &e(World:%world% X:%x% Y:%y% Z:%z%)"),
    VILLAGER_CONVERSION("villagerdeathmessages.", "&cA villager has turned into a &aZombie Villager!"),

    // Pillager Tools
    PILLAGER_TOGGLE("pillagertools.", "&6%setting% &ehas been set to &6%val%&e."),

    // Workstation Highlights
    NO_VILLAGER("workstationhightlights.", "&eNo villager found within 3 blocks."),
    NO_JOB_SITE("workstationhighlights.", "&eThis villager does not have a job site."),
    HIGHLIGHTED_SITE("workstationhighlights.", "&aHighlighted the workstation!"),

    // Tag
    PLAYER_IS_IT("tag.", "&e%name% &ehas been tagged!"),
    PLAYER_IS_AFK("tag.", "&e%name% &eis AFK!"),
    PLAYER_IS_ALREADY_IT("tag.", "&e%name% &eis already it!"),
    COOLDOWN_ACTIVE("tag.", "&eYou cannot tag anyone yet! &6%time% &eseconds left..."),

    // Nether Portal Coords
    PLAYER_IN_OVERWORLD("portalcoords.", "&eNether: X:%x% | Y:%y% | Z:%z%"),
    PLAYER_IN_NETHER("portalcoords.", "&3eOverworld: X:%x% | Y:%y% | Z:%z%"),
    INVALID_WORLD("portalcoords.", "&eThis world is not configured as an overworld or nether.")
    ;

    String path;
    String msg;

    Lang(String path, String msg) {
        this.msg = msg;
        this.path = path.endsWith(".") ? path + this.name().toLowerCase().replace("_", "-") : path;
    }

    Lang(String msg) {
        this.msg = msg;
        this.path = this.name().toLowerCase().replace("_", "-");
    }

    private String getPath() {
        return path;
    }

    private String getMsg() {
        return msg;
    }

    private void setMsg(String msg) {
        this.msg = msg;
    }

    public String p() {
        return ChatColor.translateAlternateColorCodes('&', PREFIX.toString() + this.msg);
    }

    public String err() {
        return ChatColor.translateAlternateColorCodes('&', ERROR_PREFIX.toString() + this.msg);
    }

    private static YamlConfig config;

    public static void init(YamlConfig config) {
        Lang.config = config;
        loadValues();
        config.save();
    }

    public static void reload() {
        config.reload();
        loadValues();
        config.save();
    }

    private static void loadValues() {
        for (Lang m : Lang.values()) {
            if (!config.isSet(m.getPath()))
                config.set(m.getPath(), m.getMsg());
            else if (!config.get().getString(m.getPath()).equals(m.getMsg()))
                m.setMsg(config.get().getString(m.getPath()));
        }
    }

    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', this.msg);
    }
}
