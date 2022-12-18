/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
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
package me.machinemaker.vanillatweaks.modules.teleportation.tpa;

import cloud.commandframework.Command;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import com.google.inject.Inject;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import me.machinemaker.vanillatweaks.cloud.SuggestionProviders;
import me.machinemaker.vanillatweaks.cloud.cooldown.CommandCooldown;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@ModuleCommand.Info(value = "tpa", i18n = "tpa", perm = "tpa")
class Commands extends ConfiguredModuleCommand {

    static final CloudKey<Void> TPA_REQUEST_COOLDOWN_KEY = SimpleCloudKey.of("vanillatweaks:tpa_request_cmd_cooldown");

    private final TPAManager tpaManager;
    private final Config config;
    private final BiFunction<CommandContext<CommandDispatcher>, String, List<String>> requestSuggestions;

    @Inject
    Commands(final TPAManager tpaManager, final Config config) {
        this.tpaManager = tpaManager;
        this.config = config;
        this.requestSuggestions = (context, s) -> {
            if (this.tpaManager.requestsByTarget.containsKey(context.getSender().getUUID())) {
                final Collection<Request> requests = this.tpaManager.requestsByTarget.get(context.getSender().getUUID());
                return requests.stream().map(Request::playerFrom).filter(Optional::isPresent).map(Optional::get).map(Player::getName).toList();
            }
            return Collections.emptyList();
        };
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        final CommandCooldown<CommandDispatcher> requestCooldown = CommandCooldown.<CommandDispatcher>builder(context -> Duration.ofSeconds(this.config.cooldown))
            .key(TPA_REQUEST_COOLDOWN_KEY)
            .notifier((context, cooldown, secondsLeft) -> context.getCommandContext().getSender().sendMessage(translatable("modules.tpa.commands.request.cooldown", RED, text(secondsLeft))))
            .build();

        this.manager.command(requestCooldown.applyTo(this.literal(builder, "request"))
            .argument(PlayerArgument.<CommandDispatcher>builder("target").withSuggestionsProvider(SuggestionProviders.playersWithoutSelf()))
            .handler(this.sync((context, player) -> {
                final Player target = context.get("target");
                if (player == target) {
                    context.getSender().sendMessage(translatable("modules.tpa.commands.request.fail.same-player", RED));
                    return;
                }
                this.tpaManager.startRequest(player, target);
            }))
        ).command(this.literal(builder, "cancel")
            .handler(this.sync((context, player) -> this.tpaManager.cancelRequestFrom(player)))
        ).command(this.literal(builder, "accept")
            .argument(PlayerArgument.<CommandDispatcher>builder("from").asOptional().withSuggestionsProvider(this.requestSuggestions))
            .handler(this.sync((context, player) -> {
                this.tpaManager.acceptRequest(player, context.getOrDefault("from", null));
            }))
        ).command(this.literal(builder, "deny")
            .argument(PlayerArgument.<CommandDispatcher>builder("from").asOptional().withSuggestionsProvider(this.requestSuggestions))
            .handler(this.sync((context, player) -> {
                this.tpaManager.denyRequest(player, context.getOrDefault("from", null));
            }))
        );

        this.config.createCommands(this, builder);
    }
}
