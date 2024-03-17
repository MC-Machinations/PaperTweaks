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
package me.machinemaker.papertweaks.modules;

import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;
import java.util.function.Function;
import me.machinemaker.papertweaks.adventure.TranslationRegistry;
import me.machinemaker.papertweaks.cloud.MetaKeys;
import me.machinemaker.papertweaks.cloud.ModulePermission;
import me.machinemaker.papertweaks.cloud.PaperTweaksCommand;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.ConsoleCommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.menus.AbstractConfigurationMenu;
import me.machinemaker.papertweaks.utils.ChatWindow;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandMeta;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.meta.CommandMeta;
import org.incendo.cloud.minecraft.extras.AudienceProvider;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;
import org.intellij.lang.annotations.Pattern;

import static me.machinemaker.papertweaks.adventure.Components.CLOSE_BRACKET;
import static me.machinemaker.papertweaks.adventure.Components.DOUBLE_SPACE;
import static me.machinemaker.papertweaks.adventure.Components.OPEN_BRACKET;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.kyori.adventure.text.format.TextColor.color;
import static org.incendo.cloud.description.Description.description;
import static org.incendo.cloud.key.CloudKey.cloudKey;

public abstract class ModuleCommand extends PaperTweaksCommand {

    private static final CloudKey<ModuleBase> MODULE_OWNER = cloudKey("papertweaks:commands/module_owner", ModuleBase.class);
    private static final MinecraftHelp.HelpColors MODULE_HELP_COLORS = MinecraftHelp.helpColors(
        color(0x70B3B3),
        AQUA,
        color(0x5290FA),
        GRAY,
        color(0xE66045)
    );
    final Info commandInfo = this.getClass().getAnnotation(Info.class);
    @Inject
    ModuleBase moduleBase;
    private @MonotonicNonNull ModuleLifecycle lifecycle;
    private boolean registered;
    private Command.@MonotonicNonNull Builder<CommandDispatcher> rootBuilder;

    private static <C> CommandExecutionHandler<C> createHelpHandler(final MinecraftHelp<C> help) {
        return context -> help.queryCommands(Objects.requireNonNull(context.getOrDefault("query", ""), "must supply a help query"), context.sender());
    }

    // MiniMessage descriptions will use the MinecraftHelp description decorator to parse
    static Function<String, ? extends Description> translatableDescriptionFactory(final boolean usesMiniMessage) {
        return usesMiniMessage ? Description::description : RichDescription::translatable;
    }

    final void registerCommands0(final ModuleLifecycle lifecycle) {
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

    @EnsuresNonNull("rootBuilder")
    private void setupRootBuilder() {
        this.rootBuilder = this.manager.commandBuilder(this.commandInfo.value(), this.buildRootMeta(), this.commandInfo.aliases());
        if (this.commandInfo.miniMessage()) {
            // if the command uses minimessage translations, set the verbose description to the minimessage translation key
            this.rootBuilder = this.rootBuilder.commandDescription(this.buildRootDescription(), description(this.buildRootDescription().textDescription()));
        }
    }

    protected CommandMeta buildRootMeta() {
        return CommandMeta.builder()
            .with(MODULE_OWNER, this.moduleBase)
            .with(BukkitCommandMeta.BUKKIT_DESCRIPTION, LegacyComponentSerializer.legacySection().serialize(text("Use \"%s\" for help".formatted(this.commandInfo.value()), RED)))
            .build();
    }

    private void createInfoCommand() {
        Objects.requireNonNull(this.rootBuilder, "Must create info command after root builder");
        Command.Builder<CommandDispatcher> builder = this.rootBuilder.permission(ModulePermission.of(this.lifecycle))
            .commandDescription(RichDescription.translatable("commands.info.hover", text(this.moduleBase.getName(), GOLD)));
        if (!this.commandInfo.infoOnRoot()) {
            builder = builder.literal("info");
        }
        this.manager.command(builder.handler(context -> context.sender().sendMessage(this.buildInfoComponent(context.sender().sender()))));
    }

    @EnsuresNonNull("infoComponent")
    private Component buildInfoComponent(final CommandSender audience) {
        final TextComponent.Builder builder = text()
            .append(AbstractConfigurationMenu.TITLE_LINE)
            .append(ChatWindow.center(text().color(WHITE).append(text(this.moduleBase.getName(), GOLD)).append(MenuModuleConfig.SEPARATOR).append(text("ⓘ")).hoverEvent(HoverEvent.showText(translatable("commands.info.hover", GRAY, text(this.moduleBase.getName(), GOLD))))).append(newline()))
            .append(AbstractConfigurationMenu.TITLE_LINE);

        builder.append(translatable("commands.info.description", GRAY, text(this.moduleBase.getDescription(), WHITE))).append(newline());
        final boolean canDisable = audience.hasPermission("vanillatweaks.main.disable");
        final boolean canReload = audience.hasPermission("vanillatweaks.main.reload");
        final TextComponent.Builder actionsBuilder = text();
        if (canDisable) {
            actionsBuilder.append(text()
                .color(GREEN)
                .append(OPEN_BRACKET, translatable("commands.config.default-value.bool.true"), CLOSE_BRACKET)
                .hoverEvent(HoverEvent.showText(translatable("commands.info.status.hover", RED)))
                .clickEvent(ClickEvent.runCommand("/vanillatweaks disable " + this.moduleBase.getName()))
            );
        }
        if (canDisable && canReload) {
            actionsBuilder.append(DOUBLE_SPACE);
        }
        if (canReload) {
            actionsBuilder.append(text()
                .color(YELLOW)
                .append(OPEN_BRACKET, translatable("commands.info.reload"), CLOSE_BRACKET)
                .hoverEvent(HoverEvent.showText(translatable("commands.info.reload.hover", YELLOW, text(this.moduleBase.getName()))))
                .clickEvent(ClickEvent.runCommand("/vanillatweaks reload module " + this.moduleBase.getName()))
            );
        }

        if (this.commandInfo.help()) {
            if (canDisable || canReload) {
                actionsBuilder.append(DOUBLE_SPACE);
            }
            actionsBuilder.append(text()
                    .color(color(0x5290FA))
                    .append(OPEN_BRACKET, translatable("commands.info.show-help"), CLOSE_BRACKET)
                    .hoverEvent(HoverEvent.showText(translatable("commands.info.show-help.hover", GRAY)))
                    .clickEvent(ClickEvent.runCommand("/" + this.commandInfo.value() + " help")));
        }

        return builder.append(translatable("commands.info.actions", GRAY, actionsBuilder)).append(newline()).append(AbstractConfigurationMenu.END_LINE).build();
    }

    void setupHelp() {
        Objects.requireNonNull(this.rootBuilder, "Must configure help after root builder");
        if (this.commandInfo.help()) {
            final MinecraftHelp<CommandDispatcher> help = MinecraftHelp.<CommandDispatcher>builder()
                .commandManager(this.manager)
                .audienceProvider(AudienceProvider.nativeAudience())
                .commandPrefix("/" + this.commandInfo.value() + " help")
                .colors(MODULE_HELP_COLORS)
                .descriptionDecorator((dispatcher, key) -> TranslationRegistry.translate(key, dispatcher.locale()).map(MiniMessage.miniMessage()::deserialize).orElseThrow())
                .commandFilter(command -> {
                    final boolean isOwnedByThis = command.commandMeta().optional(MODULE_OWNER).map(Functions.forPredicate(module -> module == this.moduleBase)).orElse(false);
                    return isOwnedByThis && !command.commandMeta().contains(MetaKeys.HIDDEN);
                }).build();
            this.manager.command(this.rootBuilder
                .literal("help")
                .permission(ModulePermission.of(this.lifecycle))
                .commandDescription(RichDescription.translatable("commands.help", text(this.moduleBase.getName())))
                .argument(StringParser.stringComponent(StringParser.StringMode.GREEDY).name("query").optional().description(RichDescription.translatable("commands.help.query")))
                .handler(createHelpHandler(help)));
        }
    }

    protected final ModuleLifecycle lifecycle() {
        if (this.lifecycle == null) {
            throw new IllegalStateException("lifecycle hasn't been set on this command yet!");
        }
        return this.lifecycle;
    }

    PaperCommandManager<CommandDispatcher> manager() {
        return this.manager;
    }

    protected final Permission modulePermission(final String permission) {
        return ModulePermission.of(this.lifecycle(), permission);
    }

    boolean isRegistered() {
        return this.registered;
    }

    protected final Command.Builder<CommandDispatcher> builder() {
        return this.rootBuilder;
    }

    protected final Command.Builder<CommandDispatcher> player() {
        return this.rootBuilder.senderType(PlayerCommandDispatcher.class);
    }

    protected final Command.Builder<CommandDispatcher> console() {
        return this.rootBuilder.senderType(ConsoleCommandDispatcher.class);
    }

    Description buildRootDescription() {
        return translatableDescriptionFactory(this.commandInfo.miniMessage()).apply(this.commandInfo.descriptionKey());
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
         * Module makes use of {@link net.kyori.adventure.text.minimessage.MiniMessage} components
         */
        boolean miniMessage() default false;

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
