/*
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import io.papermc.lib.PaperLib;
import me.machinemaker.vanillatweaks.adventure.translations.TranslationRegistry;
import me.machinemaker.vanillatweaks.cloud.CloudModule;
import me.machinemaker.vanillatweaks.modules.ModuleManager;
import me.machinemaker.vanillatweaks.modules.ModuleRegistry;
import me.machinemaker.vanillatweaks.adventure.translations.MappedTranslatableComponentRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.TextComponent.ofChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class VanillaTweaks extends JavaPlugin {

    public static final Component PLUGIN_PREFIX = text("[VanillaTweaks] ", WHITE);
    public static final Map<Locale, ResourceBundle> BUNDLE_MAP = Maps.newHashMap();

    static {
        try {
            Field rendererField = Class.forName("net.kyori.adventure.translation.GlobalTranslatorImpl").getDeclaredField("renderer");
            rendererField.trySetAccessible();
            rendererField.set(GlobalTranslator.get(), MappedTranslatableComponentRenderer.GLOBAL_INSTANCE);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(0);

    private Injector pluginInjector;
    @Inject
    private ModuleManager moduleManager;
    @Inject
    private BukkitAudiences audiences;

    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);

        tryBackupOldModulesYml();
        migrateModuleConfigs();

        BukkitAudiences bukkitAudiences = BukkitAudiences.create(this);
        try {
            pluginInjector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(VanillaTweaks.class).toInstance(VanillaTweaks.this);
                    bind(JavaPlugin.class).toInstance(VanillaTweaks.this);
                    bind(Plugin.class).toInstance(VanillaTweaks.this);
                    bind(BukkitAudiences.class).toInstance(bukkitAudiences);
                    bind(Audience.class).annotatedWith(Names.named("server")).toInstance(bukkitAudiences.all());
                    bind(Audience.class).annotatedWith(Names.named("players")).toInstance(bukkitAudiences.players());
                    bind(Path.class).annotatedWith(Names.named("data")).toInstance(VanillaTweaks.this.getDataFolder().toPath());
                }
            }, new ModuleRegistry(this), new CloudModule(this, EXECUTOR_SERVICE));
            pluginInjector.injectMembers(this);
        } catch (CreationException e) {
            throw new RuntimeException("Could not create injector!", e);
        }

        registerBundles().forEach(TranslationRegistry::registerAll);
        // TranslationRegistry translationRegistry = TranslationRegistry.create(LANG_KEY);
        // registerBundles().forEach(bundle -> {
        //     translationRegistry.registerAll(bundle.getLocale(), bundle, false);
        //     BUNDLE_MAP.put(bundle.getLocale(), bundle);
        // });
        // GlobalTranslator.get().addSource(translationRegistry);

        audiences.console().sendMessage(ofChildren(PLUGIN_PREFIX, translatable("plugin-lifecycle.on-enable.loaded-modules", GOLD, text(moduleManager.loadModules(), GRAY))));
        audiences.console().sendMessage(ofChildren(PLUGIN_PREFIX, translatable("plugin-lifecycle.on-enable.enabled-modules", GREEN, text(moduleManager.enableModules(), GRAY))));

        pluginInjector.getInstance(RootCommand.class).registerCommands();
        this.getServer().getPluginManager().registerEvents(pluginInjector.getInstance(GlobalListener.class), this);
    }

    @Override
    public void onDisable() {
        moduleManager.disableModules();
        EXECUTOR_SERVICE.shutdown();
        if (this.audiences != null) {
            audiences.console().sendMessage(ofChildren(PLUGIN_PREFIX, translatable("plugin-lifecycle.on-disable.disabled-modules", YELLOW, text(moduleManager.disableModules(), GRAY))));
            audiences.close();
        }
    }

    private void tryBackupOldModulesYml() {
        File oldFile = new File(this.getDataFolder(), "modules.yml");
        if (oldFile.exists()) {
            FileConfiguration oldModulesConfig = YamlConfiguration.loadConfiguration(oldFile);
            if (oldModulesConfig.contains("item-tools")) { // is old
                if (oldFile.renameTo(new File(this.getDataFolder(), "OLD_modules.yml"))) {
                    this.getLogger().warning("OLD modules.yml detected. It has been backed up to OLD_modules.yml");
                    this.getLogger().warning("You can re-configure your enabled packs with the new modules.yml");
                } else {
                    this.getLogger().severe("Could not rename modules.yml to back it up! Please rename VanillaTweaks/modules.yml to something else so the plugin can create a new one");
                    this.getPluginLoader().disablePlugin(this);
                    return;
                }

            }
        }
    }

    private void migrateModuleConfigs() {
        Path pluginDir = this.getDataFolder().toPath();
        String current = "none";
        try {
            current = "playergraves";
            if (Files.exists(pluginDir.resolve(current)) && !Files.exists(pluginDir.resolve("graves"))) {
                Files.move(pluginDir.resolve(current), pluginDir.resolve("graves"));
                this.getLogger().info("Moved '" + current + "' config to 'graves' folder");
            }
            current = "wrench";
            if (Files.exists(pluginDir.resolve(current)) && !Files.exists(pluginDir.resolve("wrenches"))) {
                Files.move(pluginDir.resolve(current), pluginDir.resolve("wrenches"));
                this.getLogger().info("Moved '" + current + "' config to 'wrenches' folder");
            }
        } catch (IOException e) {
            this.getLogger().severe("Failed to migrate " + current + " configuration!");
        }
    }

    private List<ResourceBundle> registerBundles() {
        var builder = ImmutableList.<ResourceBundle>builder();
        builder.add(ResourceBundle.getBundle("lang", Locale.ENGLISH, UTF8ResourceBundleControl.get()));

        return builder.build();
    }
}
