package me.machinemaker.vanillatweaks.sethome;

import com.google.common.collect.Maps;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public class SetHome extends BaseModule {

    final Config config = new Config();
    private Commands commands;
    final Map<UUID, SetHomeInfo> homeMap;

    public SetHome(VanillaTweaks plugin) {
        super(plugin, config -> config.setHome);
        ConfigurationSerialization.registerClass(SetHomeInfo.class);
        File configDir = new File(plugin.getDataFolder(), "sethome");
        config.init(plugin, configDir);
        plugin.configManager.createConfig("sethome/homes", "homes.yml", configDir);
        homeMap = Maps.newHashMap();
    }

    private YamlConfiguration getConfig() {
        return this.plugin.configManager.getConfig("sethome/homes").get();
    }

    @Override
    public void register() {
        this.commands = new Commands(this);
        this.registerCommands(commands);
        ConfigurationSection section = getConfig().getConfigurationSection("players");
        if (section != null) {
            section.getKeys(false).forEach(uuid -> homeMap.put(UUID.fromString(uuid), section.getObject(uuid, SetHomeInfo.class)));
        }
    }

    @Override
    public void unregister() {
        this.plugin.commandManager.unregisterCommand(commands);
        homeMap.forEach((uuid, info) -> getConfig().set("players." + uuid.toString(), info));
    }

    @Override
    public void reload() {
        config.reload();
        this.plugin.configManager.reloadConfig("sethome/homes");
    }

    void save(UUID uuid, SetHomeInfo info) {
        getConfig().set("players." + uuid.toString(), info);
        this.plugin.configManager.getConfig("sethome/homes").save();
    }

}
