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
package me.machinemaker.papertweaks.db;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.machinemaker.papertweaks.db.dao.teleportation.homes.HomesDAO;
import org.jdbi.v3.core.Jdbi;

public class DatabaseModule extends AbstractModule {

    private final Jdbi jdbi;

    public DatabaseModule(final Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    protected void configure() {
        this.bind(Jdbi.class).toInstance(this.jdbi);
    }

    @Provides
    @Singleton
    HomesDAO homesDAO(final Jdbi jdbi) {
        return jdbi.onDemand(HomesDAO.class);
    }
}
