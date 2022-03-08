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
package me.machinemaker.vanillatweaks.cloud.processors.post;

import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import cloud.commandframework.execution.postprocessor.CommandPostprocessor;
import cloud.commandframework.services.types.ConsumerService;
import me.machinemaker.vanillatweaks.cloud.MetaKeys;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class GamemodePostprocessor implements CommandPostprocessor<CommandDispatcher> {

    @Override
    public void accept(@NonNull CommandPostprocessingContext<CommandDispatcher> context) {
        Optional<GameMode> gameMode = context.getCommand().getCommandMeta().get(MetaKeys.GAMEMODE_KEY);
        if (gameMode.isPresent()) {
            if (!(context.getCommandContext().getSender() instanceof PlayerCommandDispatcher)) {
                ConsumerService.interrupt();
            }
            Player player = PlayerCommandDispatcher.from(context.getCommandContext());
            if (player.getGameMode() != gameMode.get()) {
                context.getCommandContext().getSender().sendMessage(translatable("commands.condition.gamemode", RED, text(gameMode.get().name())));
                ConsumerService.interrupt();
            }
        }
    }
}
