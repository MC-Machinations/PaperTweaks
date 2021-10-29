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

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.machinemaker.lectern.ConfigurationNode;
import me.machinemaker.vanillatweaks.cloud.VanillaTweaksCommand;
import me.machinemaker.vanillatweaks.cloud.arguments.ModuleArgument;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.menus.ConfigurationMenu;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleManager;
import me.machinemaker.vanillatweaks.modules.ModuleManager.ReloadResult;
import me.machinemaker.vanillatweaks.utils.ChatWindow;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class RootCommand extends VanillaTweaksCommand {

    private static final int PAGE_SIZE = 6;

    private final ModuleManager moduleManager;
    private final ConfigurationNode modulesConfig;
    private final int pageCount;
    private Command.@MonotonicNonNull Builder<CommandDispatcher> builder;

    @Inject
    public RootCommand(ModuleManager moduleManager, @Named("modules") ConfigurationNode modulesConfig) {
        this.moduleManager = moduleManager;
        this.modulesConfig = modulesConfig;
        this.pageCount = (int) Math.ceil(this.moduleManager.getModules().size() / (double) PAGE_SIZE);
    }

    public void registerCommands() {
        this.builder = this.manager.commandBuilder("vanillatweaks", RichDescription.translatable("commands.root"), "vt", "vtweaks");

        this.manager.command(this.simple("reload")
                .handler(sync(this::reloadEverything))
        ).command(this.simple("reload")
                .literal("module")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, translatable("commands.reload.module")) // Override default meta from #simple(String)
                .argument(ModuleArgument.enabled())
                .handler(sync(context -> context.getSender().sendMessage(this.moduleManager.reloadModule(ModuleArgument.getModule(context).getName()))))
        ).command(this.simple("enable")
                .argument(ModuleArgument.disabled())
                .handler(sync(context -> context.getSender().sendMessage(this.moduleManager.enableModule(ModuleArgument.getModule(context).getName()))))
        ).command(this.simple("disable")
                .argument(ModuleArgument.enabled())
                .handler(sync(context -> context.getSender().sendMessage(this.moduleManager.disableModule(ModuleArgument.getModule(context).getName())))
        )).command(this.simple("list")
                .argument(IntegerArgument.<CommandDispatcher>newBuilder("page").withMin(1).withMax(this.pageCount).asOptionalWithDefault(1))
                .handler(this::sendModuleList)
        );
    }

    private Command. @NonNull Builder<CommandDispatcher> simple(@NonNull String name) {
        return this.builder
                .literal(name)
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, translatable("commands." + name))
                .permission("vanillatweaks.main." + name);
    }

    private void reloadEverything(@NonNull CommandContext<CommandDispatcher> context) {
        final Audience audience = context.getSender();
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

    private void sendModuleList(@NonNull CommandContext<CommandDispatcher> context) {
        final boolean showAll = context.getSender().hasPermission("vanillatweaks.main.list.all");
        final int page = context.get("page");
        final var header = createHeader(page);
        final var list = text();
        for (ModuleBase moduleBase : new ArrayList<>(this.moduleManager.getModules().values()).subList((page - 1) * PAGE_SIZE, Math.min(this.moduleManager.getModules().size(), page * PAGE_SIZE))) {
            final var lifecycle = this.moduleManager.getLifecycle(moduleBase.getName());
            if (lifecycle.isEmpty()) continue;
            final var state = lifecycle.get().getState();
            if (showAll || state.isRunning()) {
                list.append(text().color(TextColor.color(0x8F8F8F))
                        .append(text(" - "))
                        .append(text("[" + (state.isRunning() ? "■" : "▶") + "]", state.isRunning() ? RED : GREEN)
                                .hoverEvent(HoverEvent.showText(translatable("commands.config.bool-toggle." + state.isRunning(), state.isRunning() ? RED : GREEN, text(moduleBase.getName(), GOLD))))
                                .clickEvent(ClickEvent.runCommand("/vanillatweaks " + (state.isRunning() ? "disable " : "enable ") + moduleBase.getName()))
                        )
                        .append(space())
                        .append(text(moduleBase.getName(), state.isRunning() ? GREEN : RED).hoverEvent(HoverEvent.showText(text(this.moduleManager.getModules().get(moduleBase.getName()).getDescription(), GRAY))))
                ).append(newline());
            }
        }
        context.getSender().sendMessage(join(JoinConfiguration.noSeparators(), header, list, ConfigurationMenu.END_LINE));
    }

    private @NonNull ComponentLike createHeader(int page) {
        return text()
                .append(ConfigurationMenu.TITLE_LINE)
                .append(ChatWindow.center(text("Modules - Page " + page + "/" + this.pageCount).hoverEvent(HoverEvent.showText(translatable("commands.list.success.header.hover", GRAY)))).append(newline()))
                .append(ConfigurationMenu.TITLE_LINE);
    }

}
