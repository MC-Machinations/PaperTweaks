package me.machinemaker.vanillatweaks.settings;

import me.machinemaker.vanillatweaks.utils.Keys;
import org.bukkit.NamespacedKey;

public record SettingKey<T>(NamespacedKey key) {

    public SettingKey(final String key) {
        this(Keys.key(key));
    }
}
