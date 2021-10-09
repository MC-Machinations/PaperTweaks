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
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import cloud.commandframework.minecraft.extras.RichDescription;
import io.leangen.geantyref.TypeToken;
import me.machinemaker.lectern.SectionNode;
import me.machinemaker.lectern.ValueNode;
import me.machinemaker.lectern.annotations.validations.numbers.Max;
import me.machinemaker.lectern.annotations.validations.numbers.Min;
import me.machinemaker.lectern.collection.ConfigField;
import me.machinemaker.vanillatweaks.adventure.translations.TranslationRegistry;
import me.machinemaker.vanillatweaks.cloud.arguments.SettingArgument;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.menus.LecternConfigurationMenu;
import me.machinemaker.vanillatweaks.menus.Menu;
import me.machinemaker.vanillatweaks.menus.config.ConfigMenuOptionBuilder;
import me.machinemaker.vanillatweaks.menus.config.OptionBuilder;
import me.machinemaker.vanillatweaks.menus.config.types.BooleanOptionBuilder;
import me.machinemaker.vanillatweaks.menus.config.types.EnumOptionBuilder;
import me.machinemaker.vanillatweaks.menus.config.types.IntegerOptionBuilder;
import me.machinemaker.vanillatweaks.menus.parts.MenuPartLike;
import me.machinemaker.vanillatweaks.settings.types.ConfigSetting;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public abstract class MenuModuleConfig<C extends MenuModuleConfig<C>> extends ModuleConfig {

    private static final String CONFIG_COMMAND_NAME = "config";
    private static final Map<Class<?>, ConfigMenuOptionBuilder<?>> OPTION_BUILDERS = new HashMap<>();
    private static final List<OptionBuilder> FACTORIES = new ArrayList<>();

    static {
        registerOptionBuilder(new BooleanOptionBuilder());
        registerOptionBuilder(new IntegerOptionBuilder());
        FACTORIES.add(new EnumOptionBuilder.Factory());
    }

    private static void registerOptionBuilder(ConfigMenuOptionBuilder<?> builder) {
        OPTION_BUILDERS.put(builder.typeClass(), builder);
    }

    private @MonotonicNonNull LecternConfigurationMenu<C> menu;
    private final Map<String, ConfigSetting<?, C>> settings = new HashMap<>();
    private final CloudKey<SettingArgument.SettingChange<C, ConfigSetting<?, C>>> settingChangeCloudKey = SimpleCloudKey.of(SettingArgument.SETTING_CHANGE_KEY_STRING, new TypeToken<SettingArgument.SettingChange<C, ConfigSetting<?, C>>>() {});

    @SuppressWarnings("unchecked")
    @Override
    public final void init(Path parentDir) {
        if (!getClass().isAnnotationPresent(Menu.class)) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " is missing the " + Menu.class.getSimpleName() + " annotation");
        }
        super.init(parentDir);
        List<MenuPartLike<C>> menuParts = new ArrayList<>();
        this.collectMenuParts(this.rootNode(), menuParts);
        this.menu = new LecternConfigurationMenu<>(this.title(), getClass().getAnnotation(Menu.class).commandPrefix(), menuParts, (C) this);
    }

    private void collectMenuParts(SectionNode section, List<MenuPartLike<C>> menuParts) {
        section.children().forEach((key, node) -> {
            if (node instanceof ValueNode<?> valueNode) {
                for (var entry : OPTION_BUILDERS.entrySet()) {
                    if (valueNode.type().getRawClass().equals(entry.getKey())) {
                        menuParts.add(entry.getValue().buildOption(valueNode, this.settings));
                        return;
                    }
                }
                for (OptionBuilder factory : FACTORIES) {
                    var menuPart = factory.buildOption(valueNode, this.settings);
                    if (menuPart != null) {
                        menuParts.add(menuPart);
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
            valueNode.description(TranslationRegistry.translate(desc, Locale.US));
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

    public abstract @NotNull Component title();

    @SuppressWarnings("unchecked")
    public void createCommands(@NotNull ConfiguredModuleCommand command, Command.@NotNull Builder<CommandDispatcher> builder) {
        final var configBuilder = command.adminLiteral(builder, CONFIG_COMMAND_NAME)
                .senderType(PlayerCommandDispatcher.class);
        command.manager()
                .command(configBuilder.handler(context -> this.menu.send(context.getSender())))
                .command(configBuilder.hidden()
                        .argument(SettingArgument.configSettings(this.settings))
                        .handler(context -> {
                            context.get(this.settingChangeCloudKey).apply((C) this);
                            this.save();
                            this.menu.send(context.getSender());
                        })
                ).command(configBuilder
                        .senderType(CommandDispatcher.class)
                        .literal("reset")
                        .meta(MinecraftExtrasMetaKeys.DESCRIPTION, command.buildComponent(command.i18nValue("admin." + CONFIG_COMMAND_NAME) + ".reset"))
                        .handler(context -> {
                            this.settings.values().forEach(setting -> setting.reset((C) this));
                            this.save();
                            context.getSender().sendMessage(translatable(command.i18nValue("admin." + CONFIG_COMMAND_NAME) + ".reset.success", GREEN));
                        })
                );
    }

}
