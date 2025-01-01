/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules.teleportation.homes;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.db.dao.teleportation.homes.HomesDAO;
import me.machinemaker.papertweaks.db.model.teleportation.homes.Home;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jdbi.v3.core.Jdbi;

@ModuleInfo(name = "Homes", configPath = "teleportation.homes", description = "Players can set home locations they can teleport to")
public class Homes extends ModuleBase {

    public static void migrateHomesYmlConfig(final Jdbi jdbi, final Path configFile) {
        final HomesDAO homesDAO = jdbi.onDemand(HomesDAO.class);
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile.toFile());
        final @Nullable ConfigurationSection players = config.getConfigurationSection("players");
        if (players != null) {
            players.getKeys(false).forEach(uuid -> {
                final @Nullable Location location = players.getLocation(uuid + ".location");
                if (location == null || !location.isWorldLoaded()) {
                    return;
                }
                homesDAO.insertHome(new Home(UUID.fromString(uuid), "home", location));
            });
        }
    }

    @Override
    protected void configure() {
        super.configure();
        this.requestStaticInjection(HomeTeleportRunnable.class);
    }

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }

    @Override
    protected Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }
}
