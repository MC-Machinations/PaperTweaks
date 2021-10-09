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

import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.machinemaker.lectern.ConfigurationNode;
import me.machinemaker.vanillatweaks.cloud.VanillaTweaksCommand;
import me.machinemaker.vanillatweaks.cloud.arguments.ModuleArgument;
import me.machinemaker.vanillatweaks.modules.ModuleManager;
import me.machinemaker.vanillatweaks.modules.ModuleManager.ReloadResult;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.newline;
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
                .literal("reload")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, translatable("commands.reload"))
                .permission("vanillatweaks.main.reload")
                .handler(sync(context -> this.reloadEverything(context.getSender())))
        ).command(builder
                .literal("reload")
                .literal("module")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, translatable("commands.reload.module"))
                .permission("vanillatweaks.main.reload")
                .argument(ModuleArgument.enabled())
                .handler(sync(context -> context.getSender().sendMessage(this.moduleManager.reloadModule(ModuleArgument.getModule(context).getName()))))
        ).command(builder
                .literal("enable")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, translatable("commands.enable"))
                .permission("vanillatweaks.main.enable")
                .argument(ModuleArgument.disabled())
                .handler(sync(context -> context.getSender().sendMessage(this.moduleManager.enableModule(ModuleArgument.getModule(context).getName()))))
        ).command(builder
                .literal("disable")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, translatable("commands.disable"))
                .permission("vanillatweaks.main.disable")
                .argument(ModuleArgument.enabled())
                .handler(sync(context -> context.getSender().sendMessage(this.moduleManager.disableModule(ModuleArgument.getModule(context).getName())))
        )).command(builder
                .literal("list")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, translatable("commands.list"))
                .permission("vanillatweaks.main.list")
                .handler(context -> {
                    final boolean showAll = context.getSender().hasPermission("vanillatweaks.main.list.all");
                    var component = text()
                            .append(translatable("commands.list.success.header", GRAY));

                    this.moduleManager.getModules().forEach((name, moduleBase) -> {
                        final var lifecycle = this.moduleManager.getLifecycle(name);
                        if (lifecycle.isEmpty()) return;
                        final var state = lifecycle.get().getState();
                        if (showAll || state.isRunning()) {
                            component.append(newline()).append(text().color(TextColor.color(0x8F8F8F))
                                    .append(text(" - "))
                                    .append(text(name, state.isRunning() ? GREEN : RED).hoverEvent(HoverEvent.showText(text(moduleBase.getDescription(), GRAY))))
                            );
                        }
                    });
                    context.getSender().sendMessage(component);
                })
        );
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
