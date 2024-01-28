/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.db.dao.teleportation.homes;

import me.machinemaker.papertweaks.db.model.teleportation.homes.Home;
import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.LinkedHashMap;
import java.util.UUID;

@RegisterConstructorMapper(Home.class)
public interface HomesDAO {

    @SuppressWarnings("CollectionDeclaredAsConcreteClass") // needed for jdbi
    @KeyColumn("name")
    @SqlQuery("SELECT * FROM homes WHERE player = :playerUUID ORDER BY id")
    LinkedHashMap<String, Home> getHomesForPlayer(UUID playerUUID);

    @SqlQuery("SELECT * FROM homes WHERE player = :playerUUID AND name = :name")
    Home getPlayerHome(UUID playerUUID, String name);

    @SqlUpdate("INSERT INTO homes (player, world, name, x, y, z) VALUES ( :player, :world, :name, :x, :y, :z )")
    void insertHome(@BindBean Home home);

    @SqlUpdate("UPDATE homes SET name = :name WHERE :id = id")
    void updateHome(@BindBean Home home);

    @SqlUpdate("DELETE FROM homes WHERE id = :id")
    void deleteHome(@BindBean Home home);
}
