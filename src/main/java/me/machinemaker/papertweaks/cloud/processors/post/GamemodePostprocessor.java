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
package me.machinemaker.papertweaks.cloud.processors.post;

import org.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessor;
import java.util.Optional;
import me.machinemaker.papertweaks.cloud.MetaKeys;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.incendo.cloud.services.type.ConsumerService;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class GamemodePostprocessor implements CommandPostprocessor<CommandDispatcher> {

    @Override
    public void accept(final CommandPostprocessingContext<CommandDispatcher> context) {
        final Optional<GameMode> gameMode = context.command().commandMeta().optional(MetaKeys.GAMEMODE_KEY);
        if (gameMode.isPresent()) {
            if (!(context.commandContext().sender() instanceof PlayerCommandDispatcher)) {
                ConsumerService.interrupt();
            }
            final Player player = PlayerCommandDispatcher.from(context.commandContext());
            if (player.getGameMode() != gameMode.get()) {
                context.commandContext().sender().sendMessage(translatable("commands.condition.gamemode", RED, text(gameMode.get().name())));
                ConsumerService.interrupt();
            }
        }
    }
}
