/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules.hermitcraft.tag;

import com.google.inject.Inject;
import java.util.Locale;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.utils.boards.DisplaySlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.execution.CommandExecutionHandler;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;

@ModuleCommand.Info(value = "tag", i18n = "tag", perm = "tag")
class Commands extends ConfiguredModuleCommand {

    private final TagManager tagManager;
    private final Config config;

    @Inject
    Commands(final TagManager tagManager, final Config config) {
        this.tagManager = tagManager;
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.builder();

        final Command.Builder<CommandDispatcher> giveTagBuilder = this.literal(builder, "givetag").handler(this.giveTag());
        this.register(giveTagBuilder.senderType(PlayerCommandDispatcher.class));
        this.register(giveTagBuilder.required("player", playerParser()));
        this.register(
            this.literal(builder, "reset")
                .handler(this.sync(context -> {
                    boolean removed = false;
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        if (Tag.IT.has(player)) {
                            removed = true;
                            this.tagManager.removeAsIt(player);
                            context.sender().sendMessage(translatable("modules.tag.commands.reset.success", GREEN, text(player.getName(), GOLD)));
                        }
                    }
                    if (!removed) {
                        context.sender().sendMessage(translatable("modules.tag.commands.reset.fail"));
                    }
                }))
        );
        this.register(
            this.literal(builder, "counter")
                .required("slot", enumParser(DisplaySlot.class))
                .handler(this.sync(context -> {
                    final DisplaySlot slot = context.get("slot");
                    if (slot.isDisplayedOn(this.tagManager.tagCounter)) {
                        context.sender().sendMessage(translatable("modules.tag.commands.counter.fail", RED));
                    } else {
                        slot.changeFor(this.tagManager.tagCounter);
                        context.sender().sendMessage(translatable("modules.tag.commands.counter.success", GREEN, text(slot.name().toLowerCase(Locale.ENGLISH), GOLD)));
                    }
                }))
        );

        this.config.createCommands(this, builder);
    }

    private CommandExecutionHandler<CommandDispatcher> giveTag() {
        return this.sync(context -> {
            final Player player = context.getOrSupplyDefault("player", () -> PlayerCommandDispatcher.from(context));
            if (this.tagManager.setAsIt(context.sender().sender(), player) && this.config.showMessages) {
                Bukkit.getServer().sendMessage(translatable("modules.tag.tag.success", YELLOW, text(context.sender().sender().getName()), text(player.getName())));
            }
        });
    }
}
