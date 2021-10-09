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
package me.machinemaker.vanillatweaks.modules.teleportation.tpa;

import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.SuggestionProviders;
import me.machinemaker.vanillatweaks.cloud.cooldown.CooldownBuilder;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

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
    Commands(TPAManager tpaManager, Config config) {
        this.tpaManager = tpaManager;
        this.config = config;
        this.requestSuggestions = (context, s) -> {
            if (this.tpaManager.requestsByTarget.containsKey(context.getSender().getUUID())) {
                Collection<Request> requests = this.tpaManager.requestsByTarget.get(context.getSender().getUUID());
                return requests.stream().map(Request::playerFrom).filter(Optional::isPresent).map(Optional::get).map(Player::getName).toList();
            }
            return Collections.emptyList();
        };
    }

    @Override
    protected void registerCommands() {
        var builder = this.player();

        final var requestCooldownBuilder = CooldownBuilder.<CommandDispatcher>builder(context -> Duration.ofSeconds(this.config.cooldown))
                .withKey(TPA_REQUEST_COOLDOWN_KEY)
                .withNotifier((context, cooldown, secondsLeft) -> context.getCommandContext().getSender().sendMessage(translatable("modules.tpa.commands.request.cooldown", RED, text(secondsLeft))));

        manager.command(requestCooldownBuilder.applyTo(literal(builder, "request"))
                .argument(PlayerArgument.<CommandDispatcher>newBuilder("target").withSuggestionsProvider(SuggestionProviders.playersWithoutSelf()))
                .handler(sync((context, player) -> {
                    Player target = context.get("target");
                    if (player == target) {
                        context.getSender().sendMessage(translatable("modules.tpa.commands.request.fail.same-player", RED));
                        return;
                    }
                    this.tpaManager.startRequest(player, target);
                }))
        ).command(literal(builder, "cancel")
                .handler(sync((context, player) -> this.tpaManager.cancelRequestFrom(player)))
        ).command(literal(builder, "accept")
                .argument(PlayerArgument.<CommandDispatcher>newBuilder("from").asOptional().withSuggestionsProvider(this.requestSuggestions))
                .handler(sync((context, player) -> {
                    this.tpaManager.acceptRequest(player, context.getOptional("from"));
                }))
        ).command(literal(builder, "deny")
                .argument(PlayerArgument.<CommandDispatcher>newBuilder("from").asOptional().withSuggestionsProvider(this.requestSuggestions))
                .handler(sync((context, player) -> {
                    this.tpaManager.denyRequest(player, context.getOptional("from"));
                }))
        );

        this.config.createCommands(this, builder);
    }
}
