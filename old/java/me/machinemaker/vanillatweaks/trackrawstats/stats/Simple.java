package me.machinemaker.vanillatweaks.trackrawstats.stats;

public enum Simple implements IStat {
    Deaths("Deaths", "deathCount", "Deaths"),
    KillCount("KillCount", "playerKillCount", "Kill Count"),
    TotalKills("TotalKills", "totalKillCount", "Total Kills");

    final String name;
    final String criteria;
    final String displayName;

    Simple(String name, String criteria, String displayName) {
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
