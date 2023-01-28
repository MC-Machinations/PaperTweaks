/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import cloud.commandframework.tasks.TaskConsumer;
import com.google.inject.Inject;
import java.util.function.BiConsumer;
import me.machinemaker.vanillatweaks.cloud.arguments.ArgumentFactory;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import org.bukkit.entity.Player;

/**
 * Various utility methods for commands to utilize
 */
public abstract class VanillaTweaksCommand {

    @Inject protected PaperCommandManager<CommandDispatcher> manager;
    @Inject protected ArgumentFactory argumentFactory;

    protected final <C> CommandExecutionHandler<C> sync(final BiConsumer<CommandContext<C>, Player> playerTaskConsumer) {
        return commandContext -> this.manager.taskRecipe().begin(commandContext).synchronous(context -> {
            final Player player = PlayerCommandDispatcher.from(context);
            playerTaskConsumer.accept(context, player);
        }).execute();
    }

    protected final <C> CommandExecutionHandler<C> sync(final TaskConsumer<CommandContext<C>> taskConsumer) {
        return commandContext -> this.manager.taskRecipe().begin(commandContext).synchronous(taskConsumer).execute();
    }
}
