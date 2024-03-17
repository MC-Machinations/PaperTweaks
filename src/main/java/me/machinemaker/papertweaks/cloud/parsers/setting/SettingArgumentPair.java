/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2024 Machine_Maker
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
package me.machinemaker.papertweaks.cloud.parsers.setting;

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import java.util.Map;
import java.util.function.Supplier;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.modules.MenuModuleConfig;
import me.machinemaker.papertweaks.settings.ModuleSetting;
import me.machinemaker.papertweaks.settings.ModuleSettings;
import me.machinemaker.papertweaks.settings.Setting;
import me.machinemaker.papertweaks.settings.types.ConfigSetting;
import me.machinemaker.papertweaks.settings.types.PlayerSetting;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.compound.ArgumentPair;
import org.incendo.cloud.type.tuple.Pair;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static org.incendo.cloud.key.CloudKey.cloudKey;

@SuppressWarnings("Convert2Diamond")
public class SettingArgumentPair<C, S extends Setting<?, C>> extends ArgumentPair<CommandDispatcher, S, Object, SettingArgumentPair.SettingChange<C, S>> {

    private static final TypeToken<PlayerSetting<?>> PLAYER_SETTING_TYPE_TOKEN = new TypeToken<PlayerSetting<?>>() {};
    public static final String SETTING_CHANGE_KEY_STRING = "setting";
    public static final CloudKey<SettingChange<Player, PlayerSetting<?>>> PLAYER_SETTING_CHANGE_KEY = cloudKey(SETTING_CHANGE_KEY_STRING, new TypeToken<SettingChange<Player, PlayerSetting<?>>>() {});

    @SuppressWarnings("unchecked")
    protected SettingArgumentPair(final TypeToken<SettingArgumentPair.SettingChange<C, S>> settingsChangeTypeToken, final Map<String, S> settings, final TypeToken<S> settingsTypeToken, final boolean hideSuggestions) {
        super(
            Pair.of("key", "value"),
            Pair.of((Class<S>) GenericTypeReflector.erase(settingsTypeToken.getType()), Object.class),
            ((Supplier<Pair<ArgumentParser<CommandDispatcher, S>, ArgumentParser<CommandDispatcher, Object>>>) () -> {
                final CloudKey<S> key = cloudKey("specifiedSetting", settingsTypeToken);
                return Pair.of(
                    new SettingParser<>(settings, key, hideSuggestions),
                    new SettingValueParser<>(key)
                );
            }).get(),
            SettingArgumentPair::mapper,
            settingsChangeTypeToken
        );
    }

    public static SettingArgumentPair<Player, PlayerSetting<?>> playerSettings(final Map<String, PlayerSetting<?>> settings) {
        return new SettingArgumentPair<>(PLAYER_SETTING_CHANGE_KEY.type(), settings, PLAYER_SETTING_TYPE_TOKEN, false);
    }

    public static <C extends MenuModuleConfig<C, ?>> SettingArgumentPair<C, ConfigSetting<?, C>> configSettings(final CloudKey<SettingChange<C, ConfigSetting<?, C>>> settingsChangeCloudKey, final Map<String, ConfigSetting<?, C>> settings) {
        return new SettingArgumentPair<C, ConfigSetting<?, C>>(settingsChangeCloudKey.type(), settings, new TypeToken<ConfigSetting<?, C>>() {}, false);
    }

    public static <C, S extends ModuleSetting<?, C>> Command.Builder<CommandDispatcher> resetPlayerSettings(final Command.Builder<CommandDispatcher> builder, final String translationKey, final ModuleSettings<C, S> settings) {
        return builder
            .literal("reset", RichDescription.translatable(translationKey))
            .handler(context -> {
                final Player player = PlayerCommandDispatcher.from(context);
                for (final S setting : settings.index().values()) {
                    if (setting instanceof final PlayerSetting<?> playerSetting) {
                        playerSetting.reset(player);
                    }
                }
                context.sender().sendMessage(translatable(translationKey + ".success", GREEN));
            });
    }

    private static <C, S extends Setting<?, C>> SettingChange<C, S> mapper(final CommandDispatcher sender, final Pair<S, Object> pair) {
        return new SettingChange<>(pair.first(), pair.second());
    }

    public record SettingChange<C, S extends Setting<?, C>>(S setting, Object value) {

        public void apply(final C holder) {
            this.setting.setObject(holder, this.value);
        }
    }
}
