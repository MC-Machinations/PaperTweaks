/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.cloud;

import com.google.inject.Inject;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.cloud.parsers.ParserFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.meta.CommandMeta;
import org.incendo.cloud.minecraft.extras.ImmutableMinecraftHelp;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.PaperCommandManager;

/**
 * Various utility methods for commands to utilize
 */
public abstract class PaperTweaksCommand {

    @Inject
    private PaperCommandManager<CommandDispatcher> manager;
    @Inject
    protected ParserFactory argumentFactory;

    protected final <C> CommandExecutionHandler<C> sync(final BiConsumer<CommandContext<C>, Player> playerTaskConsumer) {
        return context -> {
            Bukkit.getScheduler().runTask(this.manager.owningPlugin(), () -> {
                final Player player = PlayerCommandDispatcher.from(context);
                playerTaskConsumer.accept(context, player);
            });
        };
    }

    protected final <C> CommandExecutionHandler<C> sync(final Consumer<CommandContext<C>> taskConsumer) {
        return context -> {
            Bukkit.getScheduler().runTask(this.manager.owningPlugin(), () -> taskConsumer.accept(context));
        };
    }

    protected ImmutableMinecraftHelp.AudienceProviderBuildStage<CommandDispatcher> createHelp() {
        return MinecraftHelp.<CommandDispatcher>builder().commandManager(this.manager);
    }

    protected Command.Builder<CommandDispatcher> builder(final String name, final CommandMeta commandMeta, final String ...aliases) {
        return this.manager.commandBuilder(name, commandMeta, aliases);
    }

    protected Command.Builder<CommandDispatcher> builder(final String name, final Description description, final String ...aliases) {
        return this.manager.commandBuilder(name, description, aliases);
    }

    protected Command.Builder<CommandDispatcher> builder(final String name, final CommandMeta commandMeta, final Description description, final String ...aliases) {
        return this.manager.commandBuilder(name, commandMeta, description, aliases);
    }

    public final void register(final Command.Builder<? extends CommandDispatcher> builder) {
        this.register(builder.build());
    }

    public void register(final Command<? extends CommandDispatcher> command) {
        this.manager.command(command);
    }
}
