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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.machinemaker.lectern.ConfigurationNode;
import me.machinemaker.papertweaks.adventure.Components;
import me.machinemaker.papertweaks.cloud.PaperTweaksCommand;
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
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.Command;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.minecraft.extras.RichDescription;

import static me.machinemaker.papertweaks.cloud.parsers.ParserFactory.moduleDescriptor;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.ClickEvent.copyToClipboard;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.kyori.adventure.text.format.TextColor.color;
import static org.incendo.cloud.key.CloudKey.cloudKey;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

@DefaultQualifier(NonNull.class)
public class RootCommand extends PaperTweaksCommand {

    private static final int PAGE_SIZE = 6;
    private static final CloudKey<ModuleBase> MODULE_BASE_KEY = cloudKey("module", ModuleBase.class);

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
        this.builder = this.builder("vanillatweaks", RichDescription.translatable("commands.root"), "vt", "vtweaks", "pt", "papertweaks", "ptweaks");

        this.register(this.simple("reload")
            .handler(this.sync(this::reloadEverything))
        );
        this.register(this.simple("reload")
            .literal("module")
            .commandDescription(RichDescription.translatable("commands.reload.module")) // Override default meta from #simple(String)
            .required(MODULE_BASE_KEY, moduleDescriptor(this.argumentFactory, true))
            .handler(this.sync(context -> context.sender().sendMessage(this.moduleManager.reloadModule(context.get(MODULE_BASE_KEY).getName()))))
        );
        this.register(this.simple("enable")
            .required(MODULE_BASE_KEY, moduleDescriptor(this.argumentFactory, false))
            .handler(this.sync(context -> {
                final Component enableMsg = this.moduleManager.enableModule(context.get(MODULE_BASE_KEY).getName());
                context.sender().sendMessage(enableMsg);
                this.console.sendMessage(Components.join(PaperTweaks.PLUGIN_PREFIX, enableMsg));
            }))
        );
        this.register(this.simple("disable")
            .required(MODULE_BASE_KEY, moduleDescriptor(this.argumentFactory, true))
            .handler(this.sync(context -> {
                this.moduleManager.disableModule(context.get(MODULE_BASE_KEY).getName(), disableMsg -> {
                    context.sender().sendMessage(disableMsg);
                    this.console.sendMessage(Components.join(PaperTweaks.PLUGIN_PREFIX, disableMsg));
                });
            }))
        );
        this.register(this.simple("list")
            .optional("page", integerParser(1, this.maxPageCount), DefaultValue.constant(1))
            .handler(this::sendModuleList)
        );
        this.register(this.simple("version")
            .handler(this::showVersion)
        );
    }

    private Command.Builder<CommandDispatcher> simple(final String name) {
        return this.builder.literal(name).commandDescription(RichDescription.translatable("commands." + name)).permission("vanillatweaks.main." + name);
    }

    private void reloadEverything(final CommandContext<CommandDispatcher> context) {
        final Audience audience = context.sender();
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
        final boolean showAll = context.sender().hasPermission("vanillatweaks.main.list.all");
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
                final TextComponent.Builder builder = text().color(color(0x8F8F8F)).append(text(" - "));
                if ((state.isRunning() && context.sender().hasPermission("vanillatweaks.main.disable")) || (!state.isRunning() && context.sender().hasPermission("vanillatweaks.main.enable"))) {
                    builder.append(
                        text("[" + (state.isRunning() ? "■" : "▶") + "]", state.isRunning() ? RED : GREEN)
                            .hoverEvent(showText(translatable("commands.config.bool-toggle." + state.isRunning(), state.isRunning() ? RED : GREEN, text(moduleBase.getName(), GOLD))))
                            .clickEvent(runCommand("/vanillatweaks " + (state.isRunning() ? "disable " : "enable ") + moduleBase.getName()))
                    ).append(space());
                }

                builder.append(
                    text(moduleBase.getName(), state.isRunning() ? GREEN : RED)
                        .hoverEvent(showText(text(moduleBase.getDescription(), GRAY)))
                );
                list.append(builder).append(newline());
            }
        }
        context.sender().sendMessage(join(JoinConfiguration.noSeparators(), header, list, AbstractConfigurationMenu.END_LINE));
    }

    private ComponentLike createHeader(final int page, final List<ModuleBase> modules) {
        return textOfChildren(
            AbstractConfigurationMenu.TITLE_LINE,
            ChatWindow.center(
                text("Modules - Page " + page + "/" + ((int) Math.ceil(modules.size() / (double) PAGE_SIZE)))
                    .hoverEvent(showText(translatable("commands.list.success.header.hover", GRAY)))
            ),
            newline(),
            AbstractConfigurationMenu.TITLE_LINE
        );
    }

    private void showVersion(final CommandContext<CommandDispatcher> context) {
        final Component component = textOfChildren(
            PaperTweaks.PLUGIN_PREFIX,
            translatable("commands.version.success", GRAY, text(PaperTweaks.class.getPackage().getImplementationVersion(), GOLD))
                .hoverEvent(showText(translatable("commands.version.success.hover", GRAY)))
                .clickEvent(copyToClipboard(PaperTweaks.class.getPackage().getImplementationVersion()))
        );
        context.sender().sendMessage(component);
    }

}
