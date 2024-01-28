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
package me.machinemaker.papertweaks;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.machinemaker.lectern.ConfigurationNode;
import me.machinemaker.papertweaks.adventure.Components;
import me.machinemaker.papertweaks.cloud.PaperTweaksCommand;
import me.machinemaker.papertweaks.cloud.arguments.ModuleArgument;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.menus.AbstractConfigurationMenu;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleManager;
import me.machinemaker.papertweaks.modules.ModuleState;
import me.machinemaker.papertweaks.utils.ChatWindow;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@DefaultQualifier(NonNull.class)
public class RootCommand extends PaperTweaksCommand {

    private static final int PAGE_SIZE = 6;

    private final ModuleManager moduleManager;
    private final ConfigurationNode modulesConfig;
    private final CommandSender console;
    private final int maxPageCount;
    private Command.@MonotonicNonNull Builder<CommandDispatcher> builder;

    @Inject
    public RootCommand(final ModuleManager moduleManager, @Named("modules") final ConfigurationNode modulesConfig, @Named("console") final CommandSender console) {
        this.moduleManager = moduleManager;
        this.modulesConfig = modulesConfig;
        this.console = console;
        this.maxPageCount = (int) Math.ceil(this.moduleManager.getModules().size() / (double) PAGE_SIZE);
    }

    public void registerCommands() {
        this.builder = this.manager.commandBuilder("vanillatweaks", RichDescription.translatable("commands.root"), "vt", "vtweaks", "pt", "papertweaks", "ptweaks");

        this.manager.command(this.simple("reload")
            .handler(this.sync(this::reloadEverything))
        ).command(this.simple("reload")
            .literal("module")
            .meta(MinecraftExtrasMetaKeys.DESCRIPTION, translatable("commands.reload.module")) // Override default meta from #simple(String)
            .argument(this.argumentFactory.module(true))
            .handler(this.sync(context -> context.getSender().sendMessage(this.moduleManager.reloadModule(ModuleArgument.getModule(context).getName()))))
        ).command(this.simple("enable")
            .argument(this.argumentFactory.module(false))
            .handler(this.sync(context -> {
                final Component enableMsg = this.moduleManager.enableModule(ModuleArgument.getModule(context).getName());
                context.getSender().sendMessage(enableMsg);
                this.console.sendMessage(Components.join(PaperTweaks.PLUGIN_PREFIX, enableMsg));
            }))
        ).command(this.simple("disable")
            .argument(this.argumentFactory.module(true))
            .handler(this.sync(context -> {
                final Component disableMsg = this.moduleManager.disableModule(ModuleArgument.getModule(context).getName());
                context.getSender().sendMessage(disableMsg);
                this.console.sendMessage(Components.join(PaperTweaks.PLUGIN_PREFIX, disableMsg));
            }))
        ).command(this.simple("list")
            .argument(IntegerArgument.<CommandDispatcher>builder("page").withMin(1).withMax(this.maxPageCount).asOptionalWithDefault(1))
            .handler(this::sendModuleList)
        ).command(this.simple("version")
            .handler(this::showVersion)
        );
    }

    private Command.Builder<CommandDispatcher> simple(final String name) {
        return this.builder.literal(name).meta(MinecraftExtrasMetaKeys.DESCRIPTION, translatable("commands." + name)).permission("vanillatweaks.main." + name);
    }

    private void reloadEverything(final CommandContext<CommandDispatcher> context) {
        final Audience audience = context.getSender();
        this.modulesConfig.reloadAndSave();
        // TODO reload more stuff
        final ModuleManager.ReloadResult result = this.moduleManager.reloadModules();
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

    private void sendModuleList(final @NonNull CommandContext<CommandDispatcher> context) {
        final boolean showAll = context.getSender().hasPermission("vanillatweaks.main.list.all");
        final int page = context.get("page");
        final TextComponent.Builder list = text();
        final List<ModuleBase> modules = this.moduleManager.getModules().values().stream().filter(module -> showAll || this.moduleManager.getLifecycle(module.getName()).orElseThrow().getState().isRunning()).toList();
        final ComponentLike header = this.createHeader(page, modules);
        final int max = Math.min(modules.size(), page * PAGE_SIZE);
        for (final ModuleBase moduleBase : new ArrayList<>(modules).subList(Math.min(max, (page - 1) * PAGE_SIZE), max)) {
            final Optional<ModuleLifecycle> lifecycle = this.moduleManager.getLifecycle(moduleBase.getName());
            if (lifecycle.isEmpty()) continue;
            final ModuleState state = lifecycle.get().getState();
            if (showAll || state.isRunning()) {
                final TextComponent.Builder builder = text().color(TextColor.color(0x8F8F8F)).append(text(" - "));
                if ((state.isRunning() && context.getSender().hasPermission("vanillatweaks.main.disable")) || (!state.isRunning() && context.getSender().hasPermission("vanillatweaks.main.enable"))) {
                    builder.append(text("[" + (state.isRunning() ? "■" : "▶") + "]", state.isRunning() ? RED : GREEN).hoverEvent(HoverEvent.showText(translatable("commands.config.bool-toggle." + state.isRunning(), state.isRunning() ? RED : GREEN, text(moduleBase.getName(), GOLD)))).clickEvent(ClickEvent.runCommand("/vanillatweaks " + (state.isRunning() ? "disable " : "enable ") + moduleBase.getName()))).append(space());
                }

                builder.append(text(moduleBase.getName(), state.isRunning() ? GREEN : RED).hoverEvent(HoverEvent.showText(text(moduleBase.getDescription(), GRAY))));
                list.append(builder).append(newline());
            }
        }
        context.getSender().sendMessage(join(JoinConfiguration.noSeparators(), header, list, AbstractConfigurationMenu.END_LINE));
    }

    private ComponentLike createHeader(final int page, final List<ModuleBase> modules) {
        return text().append(AbstractConfigurationMenu.TITLE_LINE).append(ChatWindow.center(text("Modules - Page " + page + "/" + ((int) Math.ceil(modules.size() / (double) PAGE_SIZE))).hoverEvent(HoverEvent.showText(translatable("commands.list.success.header.hover", GRAY)))).append(newline())).append(AbstractConfigurationMenu.TITLE_LINE);
    }

    private void showVersion(final CommandContext<CommandDispatcher> context) {
        final TextComponent.Builder component = text().append(PaperTweaks.PLUGIN_PREFIX).append(translatable("commands.version.success", GRAY, text(PaperTweaks.class.getPackage().getImplementationVersion(), GOLD)).hoverEvent(HoverEvent.showText(translatable("commands.version.success.hover", GRAY))).clickEvent(ClickEvent.copyToClipboard(PaperTweaks.class.getPackage().getImplementationVersion())));
        context.getSender().sendMessage(component);
    }

}
