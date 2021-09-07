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

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import cloud.commandframework.tasks.TaskConsumer;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public abstract class ModuleCommand {

    @Inject
    protected PaperCommandManager<CommandDispatcher> manager;
    private boolean registered;

    final void registerCommands0(ModuleLifecycle lifecycle) {
        this.registerCommands(lifecycle);
        this.registered = true;
    }

    protected abstract void registerCommands(ModuleLifecycle lifecycle);

    protected final <C> CommandExecutionHandler<C> sync(TaskConsumer<CommandContext<C>> taskConsumer) {
        return commandContext -> {
            manager.taskRecipe().begin(commandContext).synchronous(taskConsumer).execute();
        };
    }

    protected final <C> CommandExecutionHandler<C> sync(BiConsumer<CommandContext<C>, Player> playerTaskConsumer) {
        return commandContext -> {
            manager.taskRecipe().begin(commandContext).synchronous(context -> {
                Player player = PlayerCommandDispatcher.from(context);
                playerTaskConsumer.accept(context, player);
            }).execute();
        };
    }

    boolean isRegistered() {
        return this.registered;
    }
}
