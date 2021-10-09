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
package me.machinemaker.vanillatweaks;

import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.machinemaker.lectern.ConfigurationNode;
import me.machinemaker.vanillatweaks.cloud.VanillaTweaksCommand;
import me.machinemaker.vanillatweaks.cloud.arguments.ModuleArgument;
import me.machinemaker.vanillatweaks.modules.ModuleManager;
import me.machinemaker.vanillatweaks.modules.ModuleManager.ReloadResult;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class RootCommand extends VanillaTweaksCommand {

    private final ModuleManager moduleManager;
    private final ConfigurationNode modulesConfig;

    @Inject
    public RootCommand(ModuleManager moduleManager, @Named("modules") ConfigurationNode modulesConfig) {
        this.moduleManager = moduleManager;
        this.modulesConfig = modulesConfig;
    }

    public void registerCommands() {
        var builder = this.manager.commandBuilder("vanillatweaks", RichDescription.translatable("commands.root"), "vt", "vtweaks");

        manager.command(builder
                .literal("reload", RichDescription.translatable("commands.reload"))
                .handler(sync(context -> this.reloadEverything(context.getSender())))
        ).command(builder
                .literal("reload", RichDescription.translatable("commands.reload"))
                .literal("module", RichDescription.translatable("commands.reload.module"))
                .argument(ModuleArgument.enabled())
                .handler(sync(context -> context.getSender().sendMessage(this.moduleManager.reloadModule(ModuleArgument.getModule(context).getName()))))
        ).command(builder
                .literal("enable", RichDescription.translatable("commands.enable"))
                .argument(ModuleArgument.disabled())
                .handler(sync(context -> context.getSender().sendMessage(this.moduleManager.enableModule(ModuleArgument.getModule(context).getName()))))
        ).command(builder
                .literal("disable", RichDescription.translatable("commands.disable"))
                .argument(ModuleArgument.enabled())
                .handler(sync(context -> context.getSender().sendMessage(this.moduleManager.disableModule(ModuleArgument.getModule(context).getName())))
        ));
    }

    private void reloadEverything(@NotNull Audience audience) {
        this.modulesConfig.reloadOrSave();
        // TODO reload more stuff
        ReloadResult result = moduleManager.reloadModules();
        boolean noModuleChange = true;
        if (result.disableCount() > 0) {
            audience.sendMessage(translatable("commands.reload.all.disabled.success", RED, text(result.disableCount(), GRAY)));
            noModuleChange = false;
        }
        if (result.reloadCount() > 0) {
            audience.sendMessage(translatable("commands.reload.all.reloaded.success", YELLOW, text(result.reloadCount(), GRAY)));
            noModuleChange = false;
        }
        if (result.enableCount() > 0) {
            audience.sendMessage(translatable("commands.reload.all.enabled.success", GREEN, text(result.enableCount(), GRAY)));
            noModuleChange = false;
        }
        if (noModuleChange) {
            audience.sendMessage(translatable("commands.reload.all.no-module-change", GRAY));
        }
    }

}
