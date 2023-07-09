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
package me.machinemaker.papertweaks.modules;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.Description;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.common.base.Preconditions;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import static net.kyori.adventure.text.Component.text;

@DefaultQualifier(NonNull.class)
public abstract class ConfiguredModuleCommand extends ModuleCommand {

    private static final String ADMIN = "admin";

    @Override
    void checkValid() {
        Preconditions.checkArgument(!this.commandInfo.i18n().isBlank(), "Must supply an i18n name for this command");
        Preconditions.checkArgument(!this.commandInfo.perm().isBlank(), "Must supply a permissions name for this command");
    }

    protected final <C> Command.Builder<C> literal(final Command.Builder<C> parent, final String name) {
        final ArgumentDescription literalDescription = this.buildSimpleDescription(name);
        final Command.Builder<C> builder = parent
            .literal(name, literalDescription)
            .permission(this.modulePermission(this.permValue(name)));
        return this.addDescription(builder, literalDescription);
    }

    protected final <C> Command.Builder<C> adminLiteral(final Command.Builder<C> parent, final String name) {
        final ArgumentDescription literalDescription = this.buildSimpleDescription(ADMIN + "." + name);
        final Command.Builder<C> builder = parent
            .literal(ADMIN, RichDescription.translatable("commands.admin", text(this.moduleBase.getName())))
            .literal(name, literalDescription)
            .permission(this.modulePermission(this.permValue(ADMIN + "." + name)));
        return this.addDescription(builder, literalDescription);
    }

    protected final Command.Builder<CommandDispatcher> builder(final String name, final String... aliases) {
        final ArgumentDescription literalDescription = this.buildSimpleDescription(name);
        final Command.Builder<CommandDispatcher> builder = this.manager()
            .commandBuilder(name, this.buildRootMeta(), literalDescription, aliases)
            .permission(this.modulePermission(this.permValue(name)));
        return this.addDescription(builder, literalDescription);
    }

    protected final <C> Command.Builder<C> addDescription(final Command.Builder<C> builder, final ArgumentDescription description) {
        builder.meta(CommandMeta.DESCRIPTION, ""); // clear existing description
        if (this.commandInfo.miniMessage()) {
            return builder.meta(CommandMeta.DESCRIPTION, description.getDescription());
        } else {
            return builder.meta(MinecraftExtrasMetaKeys.DESCRIPTION, ((RichDescription) description).getContents());
        }
    }

    protected final Command.Builder<CommandDispatcher> player(final String name, final String... aliases) {
        return this.builder(name, aliases).senderType(PlayerCommandDispatcher.class);
    }

    final ArgumentDescription buildSimpleDescription(final String name) {
        return this.buildDescription(this.i18nValue(name));
    }

    final ArgumentDescription buildDescription(final String i18nKey) {
        return translatableDescriptionFactory(this.commandInfo.miniMessage()).apply(i18nKey);
    }

    @Override
    ArgumentDescription buildRootDescription() {
        return this.buildSimpleDescription("root");
    }

    String i18nValue(final String name) {
        return String.join(".", "modules", this.commandInfo.i18n(), "commands", name);
    }

    String permValue(final String name) {
        return String.join(".", "vanillatweaks", this.commandInfo.perm(), name);
    }
}
