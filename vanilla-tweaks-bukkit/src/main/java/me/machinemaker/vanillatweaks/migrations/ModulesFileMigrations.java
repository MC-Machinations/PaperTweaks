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
package me.machinemaker.vanillatweaks.migrations;

import me.machinemaker.vanillatweaks.LoggerFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@DefaultQualifier(NonNull.class)
public final class ModulesFileMigrations {

    public static final String MOB_GRIEFING_ENDERMAN = "PaperTweaks.MobGriefing.Enderman";
    public static final String MOB_GRIEFING_GHAST = "PaperTweaks.MobGriefing.Ghast";
    public static final String MOB_GRIEFING_CREEPER = "PaperTweaks.MobGriefing.Creeper";

    private static final Logger LOGGER = LoggerFactory.getLogger(ModulesFileMigrations.class);
    private static final List<Migration> MIGRATIONS = List.of(
            new MobGriefingMigration("mobs.anti-enderman-grief", MOB_GRIEFING_ENDERMAN),
            new MobGriefingMigration("mobs.anti-ghast-grief", MOB_GRIEFING_GHAST),
            new MobGriefingMigration("mobs.anti-creeper-grief", MOB_GRIEFING_CREEPER),
            Migration.clear("mobs.anti-ghast-grief"),
            Migration.clear("mobs.dragon-drops"),
            Migration.clear("mobs.double-shulker-shells"),
            Migration.section("utilities", "items", "player-head-drops"),
            Migration.section("utilities", "hermitcraft", "thunder-shrine"),
            Migration.section("utilities", "survival", "coordinates-hud"),
            new Migration("utilities.player-graves", "survival.graves"),
            Migration.section("utilities", "survival", "track-raw-stats"),
            new Migration("utilities.persist-head-data", "other.persistent-heads"),
            Migration.section("utilities", "hermitcraft", "tag"),
            new CombiningMigration("utilities.spectator-conduit-power", "utilities.spectator-effects"),
            new CombiningMigration("utilities.spectator-night-vision", "utilities.spectator-effects"),
            Migration.section("utilities", "survival", "multiplayer-sleep"),
            Migration.section("utilities", "survival", "real-time-clock"),
            Migration.section("utilities", "survival", "nether-portal-coords"),
            new Migration("utilities.sethome", "teleportation.homes"),
            Migration.section("utilities", "survival", "track-stats"),
            Migration.section("utilities", "survival", "afk-display"),
            Migration.clear("utilities.classic-fishing-loot"),
            Migration.clear("item-tools.armor-statues"), // TODO change when added
            Migration.section("item-tools", "utilities", "kill-empty-boats"),
            Migration.clear("item-tools.item-averages"), // TODO change when added
            new CombiningMigration("item-tools.redstone-rotation-wrench", "items.rotation-wrenches"),
            new CombiningMigration("item-tools.terracotta-rotation-wrench", "items.rotation-wrenches"),
            Migration.section("item-tools", "survival", "durability-ping"),
            new Migration("villagers.villagerdeathmessage", "mobs.villager-death-messages"),
            new Migration("villagers.wanderingtrades", "hermitcraft.wandering-trades"),
            Migration.clear("villagers.customvillagershops"), // TODO change when added
            new Migration("villagers.pillagertools", "survival.pillager-tools"),
            new Migration("villagers.workstationhighlights", "survival.villager-workstation-highlights"),
            Migration.clear("item-tools"),
            Migration.clear("villagers")
    );


    private ModulesFileMigrations() {
    }

    public static void apply(Path dataPath, Plugin plugin) {
        final Path modulesYml = dataPath.resolve("modules.yml");
        if (Files.exists(modulesYml)) {
            try {
                FileConfiguration configuration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(modulesYml));
                if (configuration.contains("item-tools") && configuration.getInt("version") < 1) { // section "item-tools" should only exists on 0.1.x modules.yml files
                    Files.copy(modulesYml, dataPath.resolve("modules.yml.bak"));
                    LOGGER.warn("Outdated modules.yml detected. It has been copied and backed up to modules.yml.bak");
                    LOGGER.warn("Starting migration of modules.yml");
                    for (Migration migration : MIGRATIONS) {
                        migration.apply(configuration);
                    }
                    configuration.save(modulesYml.toFile());
                }
            } catch (IOException e) {
                LOGGER.error("Error migration to new modules.yml! Please try renaming VanillaTweaks/modules.yml to something else so the plugin can create a new one", e);
                plugin.getPluginLoader().disablePlugin(plugin);
            }
        }
    }

    public static class Migration {

        protected final String oldPath;
        protected final @Nullable String newPath;

        public Migration(String oldPath, @Nullable String newPath) {
            this.oldPath = oldPath;
            this.newPath = newPath;
        }

        @MustBeInvokedByOverriders
        public void apply(FileConfiguration configuration) {
            if (this.newPath != null) {
                configuration.set(newPath, configuration.get(oldPath));
            }
            configuration.set(oldPath, null);
        }

        public static Migration clear(String oldPath) {
            return new Migration(oldPath, null);
        }

        public static Migration section(String oldSection, String newSection, String moduleName) {
            return new Migration(oldSection + "." + moduleName, newSection + "." + moduleName);
        }
    }

    public static class CombiningMigration extends Migration {

        private final String newPath;

        public CombiningMigration(String oldPath, String newPath) {
            super(oldPath, null);
            this.newPath = newPath;
        }

        @Override
        public void apply(FileConfiguration configuration) {
            if (configuration.getBoolean(this.oldPath)) {
                configuration.set(this.newPath, true);
            }
            super.apply(configuration);
        }
    }

    public static class MobGriefingMigration extends CombiningMigration {

        private final String propertyKey;

        public MobGriefingMigration(String oldPath, String propertyKey) {
            super(oldPath, "mobs.mob-griefing");
            this.propertyKey = propertyKey;
        }

        @Override
        public void apply(FileConfiguration configuration) {
            System.setProperty(this.propertyKey, String.valueOf(configuration.getBoolean(this.oldPath)));
            super.apply(configuration);
        }
    }
}
