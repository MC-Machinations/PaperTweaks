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
package me.machinemaker.vanillatweaks.cloud;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.RichDescription;
import cloud.commandframework.paper.PaperCommandManager;
import cloud.commandframework.tasks.TaskConsumer;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.arguments.ArgumentFactory;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * Various utility methods for commands to utilize
 */
public abstract class VanillaTweaksCommand {

    @Inject protected PaperCommandManager<CommandDispatcher> manager;
    @Inject protected ArgumentFactory argumentFactory;

    protected Command.@NonNull Builder<CommandDispatcher> cmd(@NonNull String name, @NonNull String descriptionKey, @NonNull String @NonNull...aliases) {
        return cmd(name, RichDescription.translatable(descriptionKey), aliases);
    }

    protected Command.@NonNull Builder<CommandDispatcher> cmd(@NonNull String name, @NonNull ArgumentDescription description, @NonNull String @NonNull...aliases) {
        return this.manager
                .commandBuilder(name, description, aliases)
                .meta(CommandMeta.DESCRIPTION, ChatColor.RED + "Use /" + name + " help");
    }

    protected Command.@NonNull Builder<CommandDispatcher> playerCmd(@NonNull String name, @NonNull String descriptionKey, @NonNull String @NonNull...aliases) {
        return playerCmd(name, RichDescription.translatable(descriptionKey), aliases);
    }

    protected Command.@NonNull Builder<CommandDispatcher> playerCmd(@NonNull String name, @NonNull ArgumentDescription description, @NonNull String @NonNull...aliases) {
        return cmd(name, description, aliases)
                .senderType(PlayerCommandDispatcher.class);
    }

    protected <C> Command.@NotNull Builder<C> literal(Command.@NonNull Builder<C> builder, ModuleLifecycle lifecycle, @NonNull String i18nPrefix, @NonNull String permPrefix, @NonNull String name) {
        return builder
                .literal(name, RichDescription.translatable(i18nPrefix + "." + name))
                .permission(ModulePermission.of(lifecycle, permPrefix + "." + name));
    }

    protected <C> CommandExecutionHandler<C> sync(BiConsumer<CommandContext<C>, Player> playerTaskConsumer) {
        return commandContext -> manager.taskRecipe().begin(commandContext).synchronous(context -> {
            Player player = PlayerCommandDispatcher.from(context);
            playerTaskConsumer.accept(context, player);
        }).execute();
    }

    protected <C> CommandExecutionHandler<C> sync(TaskConsumer<CommandContext<C>> taskConsumer) {
        return commandContext -> manager.taskRecipe().begin(commandContext).synchronous(taskConsumer).execute();
    }
}
