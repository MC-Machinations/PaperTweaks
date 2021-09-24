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
package me.machinemaker.vanillatweaks.modules.hermitcraft.tag;

import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.execution.CommandExecutionHandler;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.vanillatweaks.utils.boards.DisplaySlot;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ConfiguredModuleCommand {

    private final TagManager tagManager;
    private final Config config;
    private final BukkitAudiences audiences;

    @Inject
    Commands(TagManager tagManager, Config config, BukkitAudiences audiences) {
        super("tag");
        this.tagManager = tagManager;
        this.config = config;
        this.audiences = audiences;
    }

    @Override
    protected void registerCommands() {
        var builder = cmd("tag", "modules.tag.commands.root");

        final var giveTagBuilder = literal(builder, "givetag");
        manager.command(giveTagBuilder
                .senderType(PlayerCommandDispatcher.class)
                .handler(giveTag())
        ).command(giveTagBuilder
                .argument(PlayerArgument.of("player"))
                .handler(giveTag())
        ).command(literal(builder, "reset")
                .handler(sync(context -> {
                    boolean removed = false;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (Tag.IT.has(player)) {
                            removed = true;
                            this.tagManager.removeAsIt(player);
                            context.getSender().sendMessage(translatable("modules.tag.commands.reset.success", GREEN, text(player.getName(), GOLD)));
                        }
                    }
                    if (!removed) {
                        context.getSender().sendMessage(translatable("modules.tag.commands.reset.fail"));
                    }
                }))
        ).command(literal(builder, "counter")
                .argument(EnumArgument.of(DisplaySlot.class, "slot"))
                .handler(sync(context -> {
                    DisplaySlot slot = context.get("slot");
                    if (slot.isDisplayedOn(this.tagManager.tagCounter)) {
                        context.getSender().sendMessage(translatable("modules.tag.commands.counter.fail", RED));
                    } else {
                        slot.changeFor(this.tagManager.tagCounter);
                        context.getSender().sendMessage(translatable("modules.tag.commands.counter.success", GREEN, text(slot.name().toLowerCase(Locale.ENGLISH), GOLD)));
                    }
                }))
        );

        this.config.createCommands(this, builder);
    }

    private CommandExecutionHandler<CommandDispatcher> giveTag() {
        return sync(context -> {
            Player player = context.getOrSupplyDefault("player", () -> PlayerCommandDispatcher.from(context));
            if (this.tagManager.setAsIt(context.getSender().sender(), player) && this.config.showMessages) {
                this.audiences.players().sendMessage(translatable("modules.tag.tag.success", YELLOW, text(context.getSender().sender().getName()), text(player.getName())));
            }
        });
    }
}
