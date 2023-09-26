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
package me.machinemaker.papertweaks.cloud.arguments;

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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.modules.MenuModuleConfig;
import me.machinemaker.papertweaks.settings.ModuleSetting;
import me.machinemaker.papertweaks.settings.ModuleSettings;
import me.machinemaker.papertweaks.settings.Setting;
import me.machinemaker.papertweaks.settings.types.ConfigSetting;
import me.machinemaker.papertweaks.settings.types.PlayerSetting;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.translatable;

@SuppressWarnings("Convert2Diamond")
public class SettingArgument<C, S extends Setting<?, C>> extends ArgumentPair<CommandDispatcher, S, Object, SettingArgument.SettingChange<C, S>> {

    public static final String SETTING_CHANGE_KEY_STRING = "setting";
    public static final CloudKey<SettingChange<Player, PlayerSetting<?>>> PLAYER_SETTING_CHANGE_KEY = SimpleCloudKey.of(SETTING_CHANGE_KEY_STRING, new TypeToken<SettingChange<Player, PlayerSetting<?>>>() {});
    private static final TypeToken<PlayerSetting<?>> PLAYER_SETTING_TYPE_TOKEN = new TypeToken<PlayerSetting<?>>() {};

    protected SettingArgument(final Pair<ArgumentParser<CommandDispatcher, S>, ArgumentParser<CommandDispatcher, Object>> parsers, final Class<S> classOfS, final TypeToken<SettingChange<C, S>> settingChangeTypeToken) {
        super(true, SETTING_CHANGE_KEY_STRING, Pair.of("key", "value"), Pair.of(classOfS, Object.class), parsers, (sender, pair) -> of(pair), settingChangeTypeToken);
    }

    public static <C, S extends Setting<?, C>> Builder<C, S> newBuilder(final TypeToken<SettingChange<C, S>> settingsChangeTypeToken, final Map<String, S> settings, final TypeToken<S> settingsTypeToken) {
        return new Builder<>(settingsChangeTypeToken, settings, settingsTypeToken);
    }

    public static SettingArgument<Player, PlayerSetting<?>> playerSettings(final Map<String, PlayerSetting<?>> settings) {
        return newBuilder(PLAYER_SETTING_CHANGE_KEY.getType(), settings, PLAYER_SETTING_TYPE_TOKEN).hideSuggestions().build();
    }

    public static <C extends MenuModuleConfig<C, ?>> SettingArgument<C, ConfigSetting<?, C>> configSettings(final CloudKey<SettingChange<C, ConfigSetting<?, C>>> settingsChangeCloudKey, final Map<String, ConfigSetting<?, C>> settings) {
        return newBuilder(settingsChangeCloudKey.getType(), settings, new TypeToken<ConfigSetting<?, C>>() {}).hideSuggestions().build(); // the C generic won't be resolved here
    }

    private static <C, S extends Setting<?, C>> SettingChange<C, S> of(final Pair<S, Object> pair) {
        return new SettingChange<>(pair.getFirst(), pair.getSecond());
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
                context.getSender().sendMessage(translatable(translationKey + ".success", NamedTextColor.GREEN));
            });
    }

    public static final class Builder<C, S extends Setting<?, C>> extends CommandArgument.Builder<CommandDispatcher, SettingChange<C, S>> {

        private final Map<String, S> settings;
        private final TypeToken<S> settingsTypeToken;
        private boolean hideSuggestions = false;

        private Builder(final TypeToken<SettingChange<C, S>> settingsChangeTypeToken, final Map<String, S> settings, final TypeToken<S> settingsTypeToken) {
            super(settingsChangeTypeToken, SETTING_CHANGE_KEY_STRING);
            this.settings = settings;
            this.settingsTypeToken = settingsTypeToken;
        }

        /**
         * Only affects setting key suggestions. Setting
         * value suggestions are left for editable options.
         */
        public Builder<C, S> hideSuggestions() {
            this.hideSuggestions = true;
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public SettingArgument<C, S> build() {
            final CloudKey<S> key = SimpleCloudKey.of("specifiedSetting", this.settingsTypeToken);
            final SettingParser<C, S> settingParser = new SettingParser<C, S>(this.settings, key, this.hideSuggestions);
            final Pair<ArgumentParser<CommandDispatcher, S>, ArgumentParser<CommandDispatcher, Object>> parsers = Pair.of(settingParser, new SettingValueParser<>(settingParser, key));
            return new SettingArgument<>(parsers, (Class<S>) GenericTypeReflector.erase(this.settingsTypeToken.getType()), this.getValueType());
        }
    }

    record SettingParser<C, S extends Setting<?, C>>(
        Map<String, S> settings, CloudKey<S> key,
        boolean hideSuggestions
    ) implements ArgumentParser<CommandDispatcher, S> {

        @Override
        public ArgumentParseResult<S> parse(final CommandContext<CommandDispatcher> commandContext, final Queue<String> inputQueue) {
            final @Nullable String string = inputQueue.peek();
            if (string == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(SettingParser.class, commandContext));
            }
            final S value = this.settings.get(string.toLowerCase(Locale.ENGLISH));
            if (value == null) {
                return ArgumentParseResult.failure(new IllegalArgumentException(string));
            }
            inputQueue.remove();
            commandContext.store(this.key, value);

            return ArgumentParseResult.success(value);
        }

        @Override
        public List<String> suggestions(final CommandContext<CommandDispatcher> commandContext, final String input) {
            if (this.hideSuggestions) {
                return Collections.emptyList();
            }
            return this.settings.keySet().stream().toList();
        }
    }

    record SettingValueParser<C, S extends Setting<?, C>>(
        SettingParser<C, S> settingParser,
        CloudKey<S> key
    ) implements ArgumentParser<CommandDispatcher, Object> {

        @SuppressWarnings("unchecked")
        @Override
        public ArgumentParseResult<Object> parse(final CommandContext<CommandDispatcher> commandContext, final Queue<String> inputQueue) {
            final @Nullable String string = inputQueue.peek();
            if (string == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(SettingValueParser.class, commandContext));
            }
            final Optional<S> setting = commandContext.getOptional(this.key);
            return setting.map(s -> (ArgumentParseResult<Object>) s.argumentParser().parse(commandContext, inputQueue)).orElseGet(() -> ArgumentParseResult.failure(new IllegalStateException(string + " isn't preceded by a setting")));
        }

        @Override
        public List<String> suggestions(final CommandContext<CommandDispatcher> commandContext, final String input) {
            final List<String> rawInput = commandContext.getRawInput();
            final ArgumentParseResult<S> parseResult = this.settingParser.parse(commandContext, Lists.newLinkedList(rawInput.subList(rawInput.size() - 2, rawInput.size() - 1)));
            final Optional<S> setting = parseResult.getParsedValue();
            if (parseResult.getFailure().isPresent() || setting.isEmpty()) {
                return Collections.emptyList();
            }
            return setting.get().argumentParser().suggestions(commandContext, input);
        }
    }

    public record SettingChange<C, S extends Setting<?, C>>(S setting, Object value) {

        public void apply(final C holder) {
            this.setting.setObject(holder, this.value);
        }
    }
}
