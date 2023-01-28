package me.machinemaker.vanillatweaks.settings;

public interface ModuleSetting<T, C> extends Setting<T, C> {

    SettingKey<T> settingKey();

    @Override
    default String indexKey() {
        return this.settingKey().key().getKey();
    }
}
