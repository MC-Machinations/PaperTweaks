/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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
package me.machinemaker.vanillatweaks.db;

import com.google.common.io.Resources;
import me.machinemaker.vanillatweaks.VanillaTweaksConfig;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

@DefaultQualifier(NonNull.class)
public enum DatabaseType {
    H2("h2.sql") {
        @Override
        public Jdbi createJdbiInstance(Path dataPath, VanillaTweaksConfig config) {
            return Jdbi.create(JdbcConnectionPool.create("jdbc:h2:file:" + dataPath.resolve("vanillatweaks").toAbsolutePath() + ";TRACE_LEVEL_FILE=0;AUTO_SERVER=TRUE;FILE_LOCK=SOCKET", config.database.user, config.database.password));
        }
    },
    SQLITE("sqlite.sql") {
        @Override
        public Jdbi createJdbiInstance(Path dataPath, VanillaTweaksConfig config) {
            return Jdbi.create("jdbc:sqlite:" + dataPath.resolve("vanillatweaks.sqlite.db").toAbsolutePath(), config.database.user, config.database.password);
        }
    };

    private final String schema;

    DatabaseType(String schema) {
        this.schema = schema;
    }

    @SuppressWarnings("UnstableApiUsage")
    public String readSchema(final ClassLoader classLoader) throws IOException {
        return Resources.toString(Objects.requireNonNull(classLoader.getResource("db/schema/" + this.schema), "Could not find schema for " + this.name() + " database"), StandardCharsets.UTF_8);
    }

    public abstract Jdbi createJdbiInstance(Path dataPath, VanillaTweaksConfig config);

    public static Jdbi installPlugins(Jdbi jdbi) {
        return jdbi.installPlugin(new SqlObjectPlugin());
    }
}
