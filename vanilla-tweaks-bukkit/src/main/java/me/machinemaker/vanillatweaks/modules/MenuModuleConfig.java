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
import io.leangen.geantyref.TypeToken;
import me.machinemaker.lectern.SectionNode;
import me.machinemaker.lectern.ValueNode;
import me.machinemaker.lectern.collection.ConfigField;
import me.machinemaker.vanillatweaks.adventure.translations.TranslationRegistry;
import me.machinemaker.vanillatweaks.cloud.arguments.SettingArgument;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.config.I18nKey;
import me.machinemaker.vanillatweaks.menus.LecternConfigurationMenu;
import me.machinemaker.vanillatweaks.menus.Menu;
import me.machinemaker.vanillatweaks.menus.options.BooleanMenuOption;
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
import java.util.function.Function;

public abstract class MenuModuleConfig<C extends MenuModuleConfig<C>> extends ModuleConfig {

    private @MonotonicNonNull LecternConfigurationMenu<C> menu;
    private final Map<String, ConfigSetting<?, C>> settings = new HashMap<>();
    private final CloudKey<SettingArgument.SettingChange<C, ConfigSetting<?, C>>> settingChangeCloudKey = SimpleCloudKey.of(SettingArgument.SETTING_CHANGE_KEY_STRING, new TypeToken<SettingArgument.SettingChange<C, ConfigSetting<?, C>>>() {});

    @Override
    public void init(Path parentDir) {
        if (!getClass().isAnnotationPresent(Menu.class)) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " is missing the " + Menu.class.getSimpleName() + " annotation");
        }
        super.init(parentDir);
        List<MenuPartLike<C>> menuParts = new ArrayList<>();
        this.rootNode().children().forEach((key, node) -> {
            if (node instanceof ValueNode<?> valueNode) {
                if (valueNode.type().getRawClass().equals(boolean.class)) {
                    var builder = BooleanMenuOption.<C>newBuilder(((I18nKey)valueNode.meta().get("i18n")).value(), toBoolean(valueNode), add(ConfigSetting.ofBoolean(valueNode)));
                    if (valueNode.meta().containsKey("desc")) {
                        builder.extendedDescription((String) valueNode.meta().get("desc"));
                    }
                    menuParts.add(builder);
                }
            } else if (node instanceof SectionNode sectionNode) {
                // TODO
            }
        });
        this.menu = new LecternConfigurationMenu<>(this.title(), getClass().getAnnotation(Menu.class).commandPrefix(), menuParts, (C) this);
    }

    @Override
    protected final <T> ValueNode<T> setupValueNodeSchema(SectionNode sectionNode, ConfigField.Value field, T value, Object configInstance) {
        ValueNode<T> valueNode = super.setupValueNodeSchema(sectionNode, field, value, configInstance);
        String desc = field.description();
        if (desc != null) {
            valueNode.description(TranslationRegistry.translate(desc, Locale.US));
            valueNode.meta().put("desc", desc);
        }
        return valueNode;
    }

    public abstract @NotNull Component title();

    public void createCommands(@NotNull ConfiguredModuleCommand command, Command.@NotNull Builder<CommandDispatcher> builder) {
        var configBuilder = command.literal(builder, "config");
        command.manager()
                .command(configBuilder.handler(context -> this.menu.send(context.getSender())))
                .command(configBuilder.hidden()
                        .argument(SettingArgument.configSettings(this.settings))
                        .handler(context -> {
                            context.get(this.settingChangeCloudKey).apply((C) this);
                            this.save();
                            this.menu.send(context.getSender());
                        })
                );
    }

    private static <C extends MenuModuleConfig<C>> Function<C, Boolean> toBoolean(ValueNode<?> valueNode) {
        return c -> c.rootNode().get(valueNode.path());
    }

    private <T> ConfigSetting<T, C> add(ConfigSetting<T, C> setting) {
        this.settings.put(setting.indexKey(), setting);
        return setting;
    }
}
