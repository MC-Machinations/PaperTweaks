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
package me.machinemaker.papertweaks.modules;

import io.leangen.geantyref.TypeToken;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import me.machinemaker.lectern.SectionNode;
import me.machinemaker.lectern.ValueNode;
import me.machinemaker.lectern.annotations.validations.numbers.Max;
import me.machinemaker.lectern.annotations.validations.numbers.Min;
import me.machinemaker.lectern.collection.ConfigField;
import me.machinemaker.papertweaks.adventure.TranslationRegistry;
import me.machinemaker.papertweaks.cloud.MetaKeys;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.cloud.parsers.setting.SettingArgumentFactory;
import me.machinemaker.papertweaks.menus.ConfigurationMenu;
import me.machinemaker.papertweaks.menus.Menu;
import me.machinemaker.papertweaks.menus.config.ConfigMenuOptionBuilder;
import me.machinemaker.papertweaks.menus.config.OptionBuilder;
import me.machinemaker.papertweaks.menus.config.types.BooleanOptionBuilder;
import me.machinemaker.papertweaks.menus.config.types.DoubleOptionBuilder;
import me.machinemaker.papertweaks.menus.config.types.EnumOptionBuilderFactory;
import me.machinemaker.papertweaks.menus.config.types.IntegerOptionBuilder;
import me.machinemaker.papertweaks.menus.options.MenuOption;
import me.machinemaker.papertweaks.menus.parts.MenuPartLike;
import me.machinemaker.papertweaks.settings.types.ConfigSetting;
import me.machinemaker.papertweaks.utils.ChatWindow;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.key.CloudKey;

import static me.machinemaker.papertweaks.adventure.Components.join;
import static me.machinemaker.papertweaks.cloud.parsers.setting.SettingArgumentFactory.configSettings;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static org.incendo.cloud.key.CloudKey.cloudKey;

public abstract class MenuModuleConfig<C extends MenuModuleConfig<C, M>, M extends ConfigurationMenu<C>> extends ModuleConfig {

    public static final Component SEPARATOR = text(" / ", GRAY);
    protected static final Component GLOBAL_SETTINGS = text("Global Settings");
    private static final String CONFIG_COMMAND_NAME = "config";
    private static final Map<Class<?>, ConfigMenuOptionBuilder<?>> OPTION_BUILDERS = new HashMap<>();
    private static final List<OptionBuilder.Factory> FACTORIES = new ArrayList<>();

    static {
        registerOptionBuilder(new BooleanOptionBuilder());
        registerOptionBuilder(new IntegerOptionBuilder());
        registerOptionBuilder(new DoubleOptionBuilder());
        FACTORIES.add(new EnumOptionBuilderFactory());
    }

    private final Map<String, ConfigSetting<?, C>> settings = new HashMap<>();
    @SuppressWarnings("Convert2Diamond")
    private final CloudKey<SettingArgumentFactory.SettingChange<C, ConfigSetting<?, C>>> settingChangeCloudKey = cloudKey(SettingArgumentFactory.SETTING_CHANGE_KEY_STRING, new TypeToken<SettingArgumentFactory.SettingChange<C, ConfigSetting<?, C>>>() {});
    private @MonotonicNonNull M menu;

    private static void registerOptionBuilder(final ConfigMenuOptionBuilder<?> builder) {
        OPTION_BUILDERS.put(builder.typeClass(), builder);
    }

    protected static Component buildDefaultTitle(final String name) {
        return ChatWindow.center(join(text(name), SEPARATOR, GLOBAL_SETTINGS)).append(newline());
    }

    @Override
    public final void init(final Path parentDir) {
        if (!this.getClass().isAnnotationPresent(Menu.class)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " is missing the " + Menu.class.getSimpleName() + " annotation");
        }
        super.init(parentDir);
        final List<MenuPartLike<C>> menuParts = new ArrayList<>();
        this.collectMenuParts(this.rootNode(), menuParts);
        this.menu = this.createMenu(this.title(), this.getClass().getAnnotation(Menu.class).commandPrefix(), menuParts);
    }

    protected abstract M createMenu(Component title, String commandPrefix, List<MenuPartLike<C>> configMenuParts);

    protected abstract void sendMenu(CommandContext<CommandDispatcher> context);

    protected MenuOption.Builder<?, ?, C, ?> touchMenuOption(final MenuOption.Builder<?, ?, C, ?> optionBuilder) {
        return optionBuilder;
    }

    protected M menu() {
        if (this.menu == null) {
            throw new IllegalStateException(this + " is not initialized yet");
        }
        return this.menu;
    }

    private void collectMenuParts(final SectionNode section, final List<MenuPartLike<C>> menuParts) {
        section.children().forEach((key, node) -> {
            if (node instanceof final ValueNode<?> valueNode) {
                for (final Map.Entry<Class<?>, ConfigMenuOptionBuilder<?>> entry : OPTION_BUILDERS.entrySet()) {
                    if (valueNode.type().getRawClass().equals(entry.getKey())) {
                        menuParts.add(this.touchMenuOption(entry.getValue().<C>buildOption(valueNode, this.settings)));
                        return;
                    }
                }
                for (final OptionBuilder.Factory factory : FACTORIES) {
                    final MenuOption.@Nullable Builder<?, ?, C, ?> menuPart = factory.buildOption(valueNode, this.settings);
                    if (menuPart != null) {
                        menuParts.add(this.touchMenuOption(menuPart));
                        return;
                    }
                }
            } else if (node instanceof final SectionNode sectionNode) {
                this.collectMenuParts(sectionNode, menuParts);
            }
        });
    }

    @Override
    protected final <T> ValueNode<T> setupValueNodeSchema(final SectionNode sectionNode, final ConfigField.Value field, final T value, final Object configInstance) {
        final ValueNode<T> valueNode = super.setupValueNodeSchema(sectionNode, field, value, configInstance);
        final @Nullable String desc = field.description();
        if (desc != null) {
            valueNode.description(TranslationRegistry.translate(desc, Locale.US).orElse(desc));
            valueNode.meta().put("desc", desc);
        }
        if (field.field().isAnnotationPresent(Min.class)) {
            valueNode.meta().put("min", field.field().getAnnotation(Min.class).value());
        }
        if (field.field().isAnnotationPresent(Max.class)) {
            valueNode.meta().put("max", field.field().getAnnotation(Max.class).value());
        }
        return valueNode;
    }

    protected abstract Component title();

    public void createCommands(final ConfiguredModuleCommand command, final Command.Builder<CommandDispatcher> builder) {
        final Command.Builder<CommandDispatcher> configBuilder = command.adminLiteral(builder, CONFIG_COMMAND_NAME).senderType(PlayerCommandDispatcher.class);
        final Description resetDescription = command.buildDescription(command.i18nValue("admin." + CONFIG_COMMAND_NAME) + ".reset");
        command.register(configBuilder.handler(this::sendMenu));
        command.register(configBuilder
                .apply(MetaKeys.hiddenCommand())
                .required(this.settingChangeCloudKey, configSettings(this.settingChangeCloudKey, this.settings))
                .handler(context -> {
                    context.get(this.settingChangeCloudKey).apply(this.self());
                    this.save();
                    this.sendMenu(context);
                })
            );
        command.register(command.addDescription(configBuilder, resetDescription)
                .senderType(CommandDispatcher.class)
                .literal("reset", resetDescription)
                .handler(context -> {
                    this.settings.values().forEach(setting -> setting.reset(this.self()));
                    this.save();
                    context.sender().sendMessage(translatable(command.i18nValue("admin." + CONFIG_COMMAND_NAME) + ".reset.success", GREEN));
                })
            );
    }

    @SuppressWarnings("unchecked")
    protected final C self() {
        return (C) this;
    }

}
