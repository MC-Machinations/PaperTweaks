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
package me.machinemaker.vanillatweaks.modules;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import io.leangen.geantyref.TypeToken;
import me.machinemaker.lectern.SectionNode;
import me.machinemaker.lectern.ValueNode;
import me.machinemaker.lectern.annotations.validations.numbers.Max;
import me.machinemaker.lectern.annotations.validations.numbers.Min;
import me.machinemaker.lectern.collection.ConfigField;
import me.machinemaker.vanillatweaks.adventure.TranslationRegistry;
import me.machinemaker.vanillatweaks.cloud.arguments.SettingArgument;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.menus.ConfigurationMenu;
import me.machinemaker.vanillatweaks.menus.Menu;
import me.machinemaker.vanillatweaks.menus.config.ConfigMenuOptionBuilder;
import me.machinemaker.vanillatweaks.menus.config.OptionBuilder;
import me.machinemaker.vanillatweaks.menus.config.types.BooleanOptionBuilder;
import me.machinemaker.vanillatweaks.menus.config.types.EnumOptionBuilderFactory;
import me.machinemaker.vanillatweaks.menus.config.types.IntegerOptionBuilder;
import me.machinemaker.vanillatweaks.menus.options.MenuOption;
import me.machinemaker.vanillatweaks.menus.parts.MenuPartLike;
import me.machinemaker.vanillatweaks.settings.types.ConfigSetting;
import me.machinemaker.vanillatweaks.utils.ChatWindow;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.machinemaker.vanillatweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public abstract class MenuModuleConfig<C extends MenuModuleConfig<C, M>, M extends ConfigurationMenu<C>> extends ModuleConfig {

    public static final Component SEPARATOR = text(" / ", GRAY);
    protected static final Component GLOBAL_SETTINGS = text("Global Settings");
    private static final String CONFIG_COMMAND_NAME = "config";
    private static final Map<Class<?>, ConfigMenuOptionBuilder<?>> OPTION_BUILDERS = new HashMap<>();
    private static final List<OptionBuilder> FACTORIES = new ArrayList<>();

    static {
        registerOptionBuilder(new BooleanOptionBuilder());
        registerOptionBuilder(new IntegerOptionBuilder());
        FACTORIES.add(new EnumOptionBuilderFactory());
    }

    private static void registerOptionBuilder(ConfigMenuOptionBuilder<?> builder) {
        OPTION_BUILDERS.put(builder.typeClass(), builder);
    }

    private @MonotonicNonNull M menu;
    private final Map<String, ConfigSetting<?, C>> settings = new HashMap<>();
    private final CloudKey<SettingArgument.SettingChange<C, ConfigSetting<?, C>>> settingChangeCloudKey = SimpleCloudKey.of(SettingArgument.SETTING_CHANGE_KEY_STRING, new TypeToken<SettingArgument.SettingChange<C, ConfigSetting<?, C>>>() {});

    @Override
    public final void init(Path parentDir) {
        if (!getClass().isAnnotationPresent(Menu.class)) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " is missing the " + Menu.class.getSimpleName() + " annotation");
        }
        super.init(parentDir);
        List<MenuPartLike<C>> menuParts = new ArrayList<>();
        this.collectMenuParts(this.rootNode(), menuParts);
        this.menu = this.createMenu(this.title(), getClass().getAnnotation(Menu.class).commandPrefix(), menuParts);
    }

    protected abstract @NotNull M createMenu(@NotNull Component title, @NotNull String commandPrefix, @NotNull List<MenuPartLike<C>> configMenuParts);

    protected abstract void sendMenu(@NotNull CommandContext<CommandDispatcher> context);

    protected MenuOption.@NotNull Builder<?, ?, C, ?> touchMenuOption(MenuOption.@NotNull Builder<?, ?, C, ?> optionBuilder) {
        return optionBuilder;
    }

    protected @NotNull M menu() {
        if (this.menu == null) {
            throw new IllegalStateException(this + " is not initialized yet");
        }
        return this.menu;
    }

    private void collectMenuParts(SectionNode section, List<MenuPartLike<C>> menuParts) {
        section.children().forEach((key, node) -> {
            if (node instanceof ValueNode<?> valueNode) {
                for (var entry : OPTION_BUILDERS.entrySet()) {
                    if (valueNode.type().getRawClass().equals(entry.getKey())) {
                        menuParts.add(this.touchMenuOption(entry.getValue().buildOption(valueNode, this.settings)));
                        return;
                    }
                }
                for (OptionBuilder factory : FACTORIES) {
                    var menuPart = factory.buildOption(valueNode, this.settings);
                    if (menuPart != null) {
                        menuParts.add(this.touchMenuOption(menuPart));
                        return;
                    }
                }
            } else if (node instanceof SectionNode sectionNode) {
                collectMenuParts(sectionNode, menuParts);
            }
        });
    }

    @Override
    protected final <T> ValueNode<T> setupValueNodeSchema(SectionNode sectionNode, ConfigField.Value field, T value, Object configInstance) {
        ValueNode<T> valueNode = super.setupValueNodeSchema(sectionNode, field, value, configInstance);
        String desc = field.description();
        if (desc != null) {
            valueNode.description(TranslationRegistry.translate(desc, Locale.US).orElse(null));
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

    protected abstract @NotNull Component title();

    protected static @NotNull Component buildDefaultTitle(@NotNull String name) {
        return ChatWindow.center(join(text(name), SEPARATOR, GLOBAL_SETTINGS)).append(newline());
    }

    public void createCommands(@NotNull ConfiguredModuleCommand command, Command.@NotNull Builder<CommandDispatcher> builder) {
        final var configBuilder = command.adminLiteral(builder, CONFIG_COMMAND_NAME)
                .senderType(PlayerCommandDispatcher.class);
        command.manager()
                .command(configBuilder.handler(this::sendMenu))
                .command(configBuilder.hidden()
                        .argument(SettingArgument.configSettings(this.settingChangeCloudKey, this.settings))
                        .handler(context -> {
                            context.get(this.settingChangeCloudKey).apply(self());
                            this.save();
                            this.sendMenu(context);
                        })
                ).command(configBuilder
                        .senderType(CommandDispatcher.class)
                        .literal("reset")
                        .meta(MinecraftExtrasMetaKeys.DESCRIPTION, command.buildComponent(command.i18nValue("admin." + CONFIG_COMMAND_NAME) + ".reset"))
                        .handler(context -> {
                            this.settings.values().forEach(setting -> setting.reset(self()));
                            this.save();
                            context.getSender().sendMessage(translatable(command.i18nValue("admin." + CONFIG_COMMAND_NAME) + ".reset.success", GREEN));
                        })
                );
    }

    @SuppressWarnings("unchecked")
    protected final C self() {
        return (C) this;
    }

}
