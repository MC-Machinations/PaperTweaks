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
package me.machinemaker.vanillatweaks.cloud.arguments;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.compound.ArgumentPair;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.minecraft.extras.RichDescription;
import cloud.commandframework.types.tuples.Pair;
import com.google.common.collect.Lists;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import me.machinemaker.vanillatweaks.settings.ModuleSettings;
import me.machinemaker.vanillatweaks.settings.Setting;
import me.machinemaker.vanillatweaks.settings.types.ConfigSetting;
import me.machinemaker.vanillatweaks.settings.types.PlayerSetting;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import static net.kyori.adventure.text.Component.translatable;

public class SettingArgument<C, S extends Setting<?, C>> extends ArgumentPair<CommandDispatcher, S, Object, SettingArgument.SettingChange<C, S>> {

    public static final String SETTING_CHANGE_KEY_STRING = "setting";
    public static final CloudKey<SettingChange<Player, PlayerSetting<?>>> PLAYER_SETTING_CHANGE_KEY = SimpleCloudKey.of(SETTING_CHANGE_KEY_STRING, new TypeToken<SettingChange<Player, PlayerSetting<?>>>() {});
    private static final TypeToken<PlayerSetting<?>> PLAYER_SETTING_TYPE_TOKEN = new TypeToken<PlayerSetting<?>>() {};

    protected SettingArgument(@NonNull Pair<@NonNull ArgumentParser<CommandDispatcher, S>, @NonNull ArgumentParser<CommandDispatcher, Object>> parsers, @NonNull Class<S> classOfS, @NonNull TypeToken<SettingChange<C, S>> settingChangeTypeToken) {
        super(true, SETTING_CHANGE_KEY_STRING, Pair.of("key", "value"), Pair.of(classOfS, Object.class), parsers, (sender, pair) -> of( pair), settingChangeTypeToken);
    }

    public static <C, S extends Setting<?, C>> Builder<C, S> newBuilder(@NonNull TypeToken<SettingChange<C, S>> settingsChangeTypeToken, @NonNull Map<String, S> settings, @NonNull TypeToken<S> settingsTypeToken) {
        return new Builder<>(settingsChangeTypeToken, settings, settingsTypeToken);
    }

    public static final class Builder<C, S extends Setting<?, C>> extends CommandArgument.Builder<CommandDispatcher, SettingChange<C, S>> {

        private final Map<String, S> settings;
        private final TypeToken<S> settingsTypeToken;
        private boolean hideSuggestions = false;

        private Builder(@NonNull TypeToken<SettingChange<C, S>> settingsChangeTypeToken, @NonNull Map<String, S> settings, @NonNull TypeToken<S> settingsTypeToken) {
            super(settingsChangeTypeToken, SETTING_CHANGE_KEY_STRING);
            this.settings = settings;
            this.settingsTypeToken = settingsTypeToken;
        }

        /**
         * Only affects setting key suggestions. Setting
         * value suggestions are left for editable options.
         */
        public @NonNull Builder<C, S> hideSuggestions() {
            this.hideSuggestions = true;
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public @NonNull SettingArgument<C, S> build() {
            var key = SimpleCloudKey.of("specifiedSetting", this.settingsTypeToken);
            var settingParser = new SettingParser<C, S>(this.settings, key, this.hideSuggestions);
            var parsers = Pair.of((ArgumentParser<CommandDispatcher, S>) settingParser, (ArgumentParser<CommandDispatcher, Object>) new SettingValueParser<>(settingParser, key));
            return new SettingArgument<>(parsers, (Class<S>) GenericTypeReflector.erase(this.settingsTypeToken.getType()), this.getValueType());
        }
    }

    public static SettingArgument<Player, PlayerSetting<?>> playerSettings(@NonNull Map<String, PlayerSetting<?>> settings) {
        return newBuilder(PLAYER_SETTING_CHANGE_KEY.getType(), settings, PLAYER_SETTING_TYPE_TOKEN).hideSuggestions().build();
    }

    public static <C extends MenuModuleConfig<C, ?>> SettingArgument<C, ConfigSetting<?, C>> configSettings(@NonNull CloudKey<SettingChange<C, ConfigSetting<?, C>>> settingsChangeCloudKey, @NonNull Map<String, ConfigSetting<?, C>> settings) {
        return newBuilder(settingsChangeCloudKey.getType(), settings, new TypeToken<ConfigSetting<?, C>>() {}).hideSuggestions().build(); // the C generic won't be resolved here
    }

    static final record SettingParser<C, S extends Setting<?, C>>(@NonNull Map<String, S> settings, @NonNull CloudKey<S> key, boolean hideSuggestions) implements ArgumentParser<CommandDispatcher, S> {

        @Override
        public @NonNull ArgumentParseResult<@NonNull S> parse(@NonNull CommandContext<@NonNull CommandDispatcher> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
            String string = inputQueue.peek();
            if (string == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(SettingParser.class, commandContext));
            }
            S value = this.settings.get(string.toLowerCase(Locale.ENGLISH));
            if (value == null) {
                return ArgumentParseResult.failure(new IllegalArgumentException(string));
            }
            inputQueue.remove();
            commandContext.store(this.key, value);

            return ArgumentParseResult.success(value);
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<CommandDispatcher> commandContext, @NonNull String input) {
            if (this.hideSuggestions) {
                return Collections.emptyList();
            }
            return this.settings.keySet().stream().toList();
        }
    }

    static final record SettingValueParser<C, S extends Setting<?, C>>(@NonNull SettingParser<C, S> settingParser, @NonNull CloudKey<S> key) implements ArgumentParser<CommandDispatcher, Object> {

        @SuppressWarnings("unchecked")
        @Override
        public @NonNull ArgumentParseResult<@NonNull Object> parse(@NonNull CommandContext<@NonNull CommandDispatcher> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
            String string = inputQueue.peek();
            if (string == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(SettingValueParser.class, commandContext));
            }
            Optional<S> setting = commandContext.getOptional(this.key);
            if (setting.isEmpty()) {
                return ArgumentParseResult.failure(new IllegalStateException(string + " isn't preceded by a setting"));
            }

            return (ArgumentParseResult<Object>) setting.get().argumentParser().parse(commandContext, inputQueue);
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<CommandDispatcher> commandContext, @NonNull String input) {
            List<String> rawInput = commandContext.getRawInput();
            ArgumentParseResult<S> parseResult = this.settingParser.parse(commandContext, Lists.newLinkedList(rawInput.subList(rawInput.size() - 2, rawInput.size() -1)));
            Optional<S> setting = parseResult.getParsedValue();
            if (parseResult.getFailure().isPresent() || setting.isEmpty()) {
                return Collections.emptyList();
            }
            return setting.get().argumentParser().suggestions(commandContext, input);
        }
    }

    public static final record SettingChange<C, S extends Setting<?, C>>(@NonNull S setting, Object value) {

        public void apply(C holder) {
            this.setting.setObject(holder, value);
        }
    }

    private static <C, S extends Setting<?, C>> SettingChange<C, S> of(Pair<S, Object> pair) {
        return new SettingChange<>(pair.getFirst(), pair.getSecond());
    }

    public static <C, S extends Setting<?, C>> Command.Builder<CommandDispatcher> resetPlayerSettings(Command.Builder<CommandDispatcher> builder, String translationKey, ModuleSettings<S> settings) {
        return builder
                .literal("reset", RichDescription.translatable(translationKey))
                .handler(context -> {
                    Player player = PlayerCommandDispatcher.from(context);
                    for (S setting : settings.index().values()) {
                        if (setting instanceof PlayerSetting<?> playerSetting) {
                            playerSetting.reset(player);
                        }
                    }
                    context.getSender().sendMessage(translatable(translationKey + ".success", NamedTextColor.GREEN));
                });
    }
}
