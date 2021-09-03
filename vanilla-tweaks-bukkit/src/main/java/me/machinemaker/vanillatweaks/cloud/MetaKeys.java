package me.machinemaker.vanillatweaks.cloud;

import cloud.commandframework.meta.CommandMeta;
import org.bukkit.GameMode;

public final class MetaKeys {

    private MetaKeys() {
    }

    public static final CommandMeta.Key<GameMode> GAMEMODE_KEY = CommandMeta.Key.of(GameMode.class, "vanillatweaks:gamemode");
}
