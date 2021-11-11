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
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.common.base.Preconditions;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

import static net.kyori.adventure.text.Component.text;

public abstract class ConfiguredModuleCommand extends ModuleCommand {

    private static final String ADMIN = "admin";

    @Override
    void checkValid() {
        Preconditions.checkArgument(!this.commandInfo.i18n().isBlank(), "Must supply an i18n name for this command");
        Preconditions.checkArgument(!this.commandInfo.perm().isBlank(), "Must supply a permissions name for this command");
    }

    protected final <C> Command.@NonNull Builder<C> literal(Command.@NonNull Builder<C> builder, @NonNull String name) {
        return builder
                .literal(name)
                .permission(this.modulePermission(this.permValue(name)))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, buildDescriptionComponent(name));
    }

    protected final <C> Command.@NonNull Builder<C> adminLiteral(Command.@NonNull Builder<C> builder, @NonNull String name) {
        return builder
                .literal(ADMIN, RichDescription.translatable("commands.admin", text(this.moduleBase.getName())))
                .literal(name)
                .permission(this.modulePermission(this.permValue(ADMIN + "." + name)))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, this.buildDescriptionComponent(ADMIN + "." + name));
    }

    protected final Command.@NonNull Builder<CommandDispatcher> builder(@NonNull String name, @NonNull String @NonNull...aliases) {
        return this.manager()
                .commandBuilder(name, this.buildRootMeta(), RichDescription.of(this.buildDescriptionComponent(name)), aliases)
                .permission(this.modulePermission(this.permValue(name)))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, buildDescriptionComponent(name));
    }

    protected final Command.@NonNull Builder<CommandDispatcher> player(@NonNull String name, @NonNull String @NonNull...aliases) {
        return this.builder(name, aliases).senderType(PlayerCommandDispatcher.class);
    }

    final @NonNull Component buildDescriptionComponent(@NonNull String name) {
        return this.buildComponent(this.i18nValue(name));
    }

    final @NonNull Component buildComponent(@NonNull String i18nKey) {
        return translatableComponentBuilder(this.commandInfo.miniMessage()).apply(i18nKey);
    }

    @Override
    Component buildRootDescriptionComponent() {
        return this.buildDescriptionComponent("root");
    }

    @NonNull String i18nValue(@NonNull String name) {
        return String.join(".", "modules", this.commandInfo.i18n(), "commands", name);
    }

    @NonNull String permValue(@NonNull String name) {
        return String.join(".", "vanillatweaks", this.commandInfo.perm(), name);
    }
}
