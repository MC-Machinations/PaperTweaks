/*
 * GNU General Public License v3
 *
 * VanillaTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021 Machine_Maker
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
package me.machinemaker.vanillatweaks;

import me.machinemaker.lectern.BaseConfig;
import me.machinemaker.lectern.annotations.ConfigurationSection;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.YamlConfig;

@YamlConfig
public class VanillaTweaksConfig extends BaseConfig {

    @Key("enable-bstats")
    public boolean metricsEnabled = true;

    public Database database = new Database();

    @ConfigurationSection(path = "database", description = "Settings related to the embedded database. Don't change these, they are just there if you want to look inside the H2 database for yourself")
    public static class Database {

        public String user = "user";

        public String password = "password";
    }
}
