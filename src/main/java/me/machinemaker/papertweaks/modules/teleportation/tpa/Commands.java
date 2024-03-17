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
package me.machinemaker.papertweaks.modules.teleportation.tpa;

import com.google.inject.Inject;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import me.machinemaker.papertweaks.cloud.cooldown.CommandCooldown;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import static me.machinemaker.papertweaks.cloud.SuggestionProviders.playersWithoutSelf;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.key.CloudKey.cloudKey;

@ModuleCommand.Info(value = "tpa", i18n = "tpa", perm = "tpa")
class Commands extends ConfiguredModuleCommand {

    static final CloudKey<Void> TPA_REQUEST_COOLDOWN_KEY = cloudKey("papertweaks:tpa_request_cmd_cooldown");

    private final TPAManager tpaManager;
    private final Config config;
    private final BlockingSuggestionProvider.Strings<CommandDispatcher> requestSuggestions;

    @Inject
    Commands(final TPAManager tpaManager, final Config config) {
        this.tpaManager = tpaManager;
        this.config = config;
        this.requestSuggestions = (context, input) -> {
            if (this.tpaManager.requestsByTarget.containsKey(context.sender().getUUID())) {
                final Collection<Request> requests = this.tpaManager.requestsByTarget.get(context.sender().getUUID());
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
            .notifier((context, cooldown, secondsLeft) -> context.commandContext().sender().sendMessage(translatable("modules.tpa.commands.request.cooldown", RED, text(secondsLeft))))
            .build();

        this.register(
            this.literal(builder, "request")
                .apply(requestCooldown)
                .required("target", playerParser(), playersWithoutSelf())
                .handler(this.sync((context, player) -> {
                    final Player target = context.get("target");
                    if (player == target) {
                        context.sender().sendMessage(translatable("modules.tpa.commands.request.fail.same-player", RED));
                        return;
                    }
                    this.tpaManager.startRequest(player, target);
                }))
        );
        this.register(
            this.literal(builder, "cancel")
                .handler(this.sync((context, player) -> this.tpaManager.cancelRequestFrom(player)))
        );
        this.register(
            this.literal(builder, "accept")
                .optional("from", playerParser(), this.requestSuggestions)
                .argument(PlayerParser.<CommandDispatcher>playerComponent().name("from").optional().suggestionProvider(this.requestSuggestions))
                .handler(this.sync((context, player) -> {
                    this.tpaManager.acceptRequest(player, context.getOrDefault("from", null));
                }))
        );
        this.register(
            this.literal(builder, "deny")
                .optional("from", playerParser(), this.requestSuggestions)
                .handler(this.sync((context, player) -> {
                    this.tpaManager.denyRequest(player, context.getOrDefault("from", null));
                }))
        );

        this.config.createCommands(this, builder);
    }
}
