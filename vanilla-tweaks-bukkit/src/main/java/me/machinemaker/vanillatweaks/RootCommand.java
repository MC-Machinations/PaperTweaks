/*
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
package me.machinemaker.vanillatweaks;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.minecraft.extras.RichDescription;
import cloud.commandframework.paper.PaperCommandManager;
import cloud.commandframework.tasks.TaskConsumer;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.machinemaker.lectern.LecternConfig;
import me.machinemaker.vanillatweaks.cloud.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.arguments.ModuleArgument;
import me.machinemaker.vanillatweaks.modules.ModuleManager;
import me.machinemaker.vanillatweaks.modules.ModuleManager.ReloadResult;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class RootCommand {

    private final PaperCommandManager<CommandDispatcher> commandManager;
    private final ModuleManager moduleManager;
    private final LecternConfig modulesConfig;

    @Inject
    public RootCommand(PaperCommandManager<CommandDispatcher> commandManager, ModuleManager moduleManager, @Named("modules") LecternConfig modulesConfig) {
        this.commandManager = commandManager;
        this.moduleManager = moduleManager;
        this.modulesConfig = modulesConfig;
    }

    public void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = commandManager.commandBuilder("vanillatweaks", ArgumentDescription.of("Base command for VanillaTweaks"), "vt", "vtweaks");

        commandManager
                .command(builder
                        .literal("reload", RichDescription.translatable("commands.reload"))
                        .handler(context -> sync(context, c -> this.reloadEverything(c.getSender()))))
                .command(builder
                        .literal("reload", RichDescription.translatable("commands.reload"))
                        .literal("module", RichDescription.translatable("commands.reload.module"))
                        .argument(ModuleArgument.enabled())
                        .handler(context -> sync(context, c -> c.getSender().audience().sendMessage(this.moduleManager.reloadModule(ModuleArgument.getModule(c).getName())))))
                .command(builder
                        .literal("enable", RichDescription.translatable("commands.enable"))
                        .argument(ModuleArgument.disabled())
                        .handler(context -> sync(context, c -> c.getSender().audience().sendMessage(this.moduleManager.enableModule(ModuleArgument.getModule(c).getName())))))
                .command(builder
                        .literal("disable", RichDescription.translatable("commands.disable"))
                        .argument(ModuleArgument.enabled())
                        .handler(context -> sync(context, c -> c.getSender().audience().sendMessage(this.moduleManager.disableModule(ModuleArgument.getModule(c).getName())))));
    }

    private <I> void sync(I begin, TaskConsumer<I> taskConsumer) {
        this.commandManager.taskRecipe().begin(begin).synchronous(taskConsumer).execute();
    }

    private void reloadEverything(CommandDispatcher dispatcher) {
        Audience sender = dispatcher.audience();
        this.modulesConfig.reloadOrSave();
        // TODO reload more stuff
        ReloadResult result = moduleManager.reloadModules();
        boolean noModuleChange = true;
        if (result.disableCount() > 0) {
            sender.sendMessage(translatable("commands.reload.all.disabled.success", NamedTextColor.RED, text(result.disableCount(), NamedTextColor.GRAY)));
            noModuleChange = false;
        }
        if (result.reloadCount() > 0) {
            sender.sendMessage(translatable("commands.reload.all.reloaded.success", NamedTextColor.YELLOW, text(result.reloadCount(), NamedTextColor.GRAY)));
            noModuleChange = false;
        }
        if (result.enableCount() > 0) {
            sender.sendMessage(translatable("commands.reload.all.enabled.success", NamedTextColor.GREEN, text(result.enableCount(), NamedTextColor.GRAY)));
            noModuleChange = false;
        }
        if (noModuleChange) {
            sender.sendMessage(translatable("commands.reload.all.no-module-change", NamedTextColor.GRAY));
        }
    }

}
