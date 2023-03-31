/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.machinemaker.vanillatweaks.modules.teleportation.homes;

import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.db.dao.teleportation.homes.HomesDAO;
import me.machinemaker.vanillatweaks.db.model.teleportation.homes.Home;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@ModuleInfo(name = "Homes", configPath = "teleportation.homes", description = "Players can set home locations they can teleport to")
public class Homes extends ModuleBase {

    @Override
    protected void configure() {
        super.configure();
        requestStaticInjection(HomeTeleportRunnable.class);
    }

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.Empty.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }

    public static void migrateHomesYmlConfig(Jdbi jdbi, Path configFile) {
        final HomesDAO homesDAO = jdbi.onDemand(HomesDAO.class);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile.toFile());
        ConfigurationSection players = config.getConfigurationSection("players");
        if (players != null) {
            players.getKeys(false).forEach(uuid -> {
                Location location = players.getLocation(uuid + ".location");
                if (location == null || !location.isWorldLoaded()) {
                    return;
                }
                homesDAO.insertHome(new Home(UUID.fromString(uuid), "home", location));
            });
        }
    }
}
