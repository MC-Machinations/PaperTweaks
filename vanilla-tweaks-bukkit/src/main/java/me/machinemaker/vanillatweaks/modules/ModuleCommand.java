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

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.minecraft.extras.RichDescription;
import cloud.commandframework.paper.PaperCommandManager;
import cloud.commandframework.permission.CommandPermission;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.adventure.translations.MappedTranslatableComponent;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.VanillaTweaksCommand;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.ConsoleCommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.menus.ConfigurationMenu;
import me.machinemaker.vanillatweaks.utils.ChatWindow;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public abstract class ModuleCommand extends VanillaTweaksCommand {

    private static final CommandMeta.Key<ModuleBase> MODULE_OWNER = CommandMeta.Key.of(ModuleBase.class, "vanillatweaks:commands/module_owner");
    private static final MinecraftHelp.HelpColors MODULE_HELP_COLORS = MinecraftHelp.HelpColors.of(
            TextColor.color(0x70B3B3),
            NamedTextColor.AQUA,
            TextColor.color(0x5290fa),
            NamedTextColor.GRAY,
            TextColor.color(0xE66045)
    );

    private @MonotonicNonNull ModuleLifecycle lifecycle;
    private boolean registered;
    private Command.Builder<CommandDispatcher> rootBuilder;
    @Inject ModuleBase moduleBase;
    final Info commandInfo = this.getClass().getAnnotation(Info.class);
    private Component infoComponent;

    final void registerCommands0(ModuleLifecycle lifecycle) {
        Objects.requireNonNull(this.commandInfo, this + " is not annotated with @ModuleCommand.Info");
        this.checkValid();
        this.lifecycle = lifecycle;
        this.setupRootBuilder();
        this.registerCommands();
        this.createInfoCommand();
        this.setupHelp();
        this.registered = true;
    }

    void checkValid() {
        Preconditions.checkArgument(!this.commandInfo.descriptionKey().isBlank(), "A non-configured module command requires a root description key");
    }

    protected abstract void registerCommands();

    private void setupRootBuilder() {
        this.rootBuilder = this.manager.commandBuilder(this.commandInfo.value(), this.buildRootMeta(), this.commandInfo.aliases());
    }

    protected CommandMeta buildRootMeta() {
        return CommandMeta.simple()
                .with(CommandMeta.DESCRIPTION, "\u00A7cUse \"/" + this.commandInfo.value() + "\" for help")
                .with(MinecraftExtrasMetaKeys.DESCRIPTION, this.buildRootDescriptionComponent())
                .with(MODULE_OWNER, this.moduleBase)
                .build();
    }

    void createInfoCommand() {
        Objects.requireNonNull(this.rootBuilder, "Must create info command after root builder");
        this.createInfoComponent();
        var builder = this.rootBuilder.permission(ModulePermission.of(this.lifecycle));
        if (!this.commandInfo.infoOnRoot()) {
            builder = builder.literal("info");
        }
        this.manager.command(builder.handler(context -> context.getSender().sendMessage(this.infoComponent)));
    }

    private void createInfoComponent() {
        final var builder = text()
                .append(ConfigurationMenu.TITLE_LINE)
                .append(ChatWindow.center(text().color(WHITE).append(text(this.moduleBase.getName(), GOLD)).append(MenuModuleConfig.SEPARATOR).append(text("ⓘ")).hoverEvent(HoverEvent.showText(translatable("commands.info.hover", GRAY, text(this.moduleBase.getName()))))).append(newline()))
                .append(ConfigurationMenu.TITLE_LINE);

        builder.append(translatable("commands.info.description", GRAY, text(this.moduleBase.getDescription(), WHITE))).append(newline());
        final var actionsBuilder = text().append(text()
                        .color(GREEN)
                        .append(text('['))
                        .append(translatable("commands.config.default-value.bool.true"))
                        .append(text(']'))
                        .hoverEvent(HoverEvent.showText(translatable("commands.info.status.hover", RED)))
                        .clickEvent(ClickEvent.runCommand("/vanillatweaks disable " + this.moduleBase.getName()))
                )
                .append(text("  "))
                .append(text()
                        .color(YELLOW)
                        .append(text('['))
                        .append(translatable("commands.info.reload"))
                        .append(text(']'))
                        .hoverEvent(HoverEvent.showText(translatable("commands.info.reload.hover", YELLOW, text(this.moduleBase.getName()))))
                        .clickEvent(ClickEvent.runCommand("/vanillatweaks reload module " + this.moduleBase.getName()))
                );

        if (this.commandInfo.help()) {
            actionsBuilder.append(text("  ")).append(text().color(TextColor.color(0x5290fa))
                    .append(text("["))
                    .append(translatable("commands.info.show-help"))
                    .append(text("]"))
                    .hoverEvent(HoverEvent.showText(translatable("commands.info.show-help.hover", GRAY)))
                    .clickEvent(ClickEvent.runCommand("/" + this.commandInfo.value() + " help")));
        }

        this.infoComponent = builder.append(translatable("commands.info.actions", GRAY, actionsBuilder)).append(newline()).append(ConfigurationMenu.END_LINE).build();
    }

    void setupHelp() {
        Objects.requireNonNull(this.rootBuilder, "Must configure help after root builder");
        if (this.commandInfo.help()) {
            final var help = MinecraftHelp.createNative("/" + this.commandInfo.value() + " help", this.manager);
            help.setHelpColors(MODULE_HELP_COLORS);
            help.commandFilter(command -> {
                final boolean isOwnedByThis = command.getCommandMeta().get(MODULE_OWNER).map(Functions.forPredicate(module -> module == this.moduleBase)).orElse(false);
                return isOwnedByThis && !command.isHidden();
            });
            this.manager.command(this.rootBuilder
                    .literal("help")
                    .permission(ModulePermission.of(this.lifecycle))
                    .argument(StringArgument.<CommandDispatcher>newBuilder("query").greedy().asOptional().withDefaultDescription(RichDescription.translatable("commands.help.query")))
                    .meta(MinecraftExtrasMetaKeys.DESCRIPTION, translatable("commands.help", text(this.moduleBase.getName())))
                    .handler(createHelpHandler(help)));
        }
    }

    protected final @NonNull ModuleLifecycle lifecycle() {
        if (this.lifecycle == null) {
            throw new IllegalStateException("lifecycle hasn't been set on this command yet!");
        }
        return this.lifecycle;
    }

    @NonNull PaperCommandManager<CommandDispatcher> manager() {
        return this.manager;
    }

    protected final @NonNull CommandPermission modulePermission(@NonNull String permission) {
        return ModulePermission.of(lifecycle(), permission);
    }

    boolean isRegistered() {
        return this.registered;
    }

    protected final Command.@NonNull Builder<CommandDispatcher> builder() {
        return this.rootBuilder;
    }

    protected final Command.@NonNull Builder<CommandDispatcher> player() {
        return this.rootBuilder.senderType(PlayerCommandDispatcher.class);
    }

    protected final Command.@NonNull Builder<CommandDispatcher> console() {
        return this.rootBuilder.senderType(ConsoleCommandDispatcher.class);
    }

    Component buildRootDescriptionComponent() {
        return translatableComponentBuilder(this.commandInfo.isMapped()).apply(this.commandInfo.descriptionKey());
    }

    static @NonNull Function<@NonNull String, @NonNull Component> translatableComponentBuilder(boolean isMapped) {
        return isMapped ? MappedTranslatableComponent::mapped : Component::translatable;
    }

    private static  <C> CommandExecutionHandler<C> createHelpHandler(@NonNull MinecraftHelp<C> help) {
        return context -> help.queryCommands(Objects.requireNonNull(context.getOrDefault("query", ""), "must supply a help query"), context.getSender());
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Info {

        /**
         * Main command name
         */
        String value();

        /**
         * I18n key for base description
         */
        @Pattern("[a-zA-Z_\\-\\.]+")
        String descriptionKey() default "";

        /**
         * Description should be a mapped component
         */
        boolean isMapped() default false;

        /**
         * Command aliases
         */
        String[] aliases() default {};

        /**
         * I18n name for command
         */
        String i18n() default "";

        /**
         * Permissions name for command
         */
        String perm() default "";

        /**
         * Auto-generate help
         */
        boolean help() default true;

        /**
         * Adds command at the root to display info about the module
         */
        boolean infoOnRoot() default true;
    }
}
