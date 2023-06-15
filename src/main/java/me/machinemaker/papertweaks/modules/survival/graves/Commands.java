/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.graves;

import cloud.commandframework.Command;
import com.google.inject.Inject;
import java.util.List;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.pdc.DataTypes;
import me.machinemaker.papertweaks.utils.PTUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

@ModuleCommand.Info(value = "graves", i18n = "graves", perm = "graves")
class Commands extends ConfiguredModuleCommand {

    private static final ItemStack GRAVE_KEY = PTUtils.getSkull(text("Grave Key", YELLOW).decoration(ITALIC, false), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWVjNzA3NjllMzYzN2E3ZWRiNTcwMmJjYzQzM2NjMjQyYzJmMjIzNWNiNzNiOTQwODBmYjVmYWZmNDdiNzU0ZSJ9fX0=");

    static {
        final ItemMeta meta = GRAVE_KEY.getItemMeta();
        if (meta == null) {
            throw new IllegalStateException("meta can't be null");
        }
        meta.getPersistentDataContainer().set(PlayerListener.GRAVE_KEY, DataTypes.BOOLEAN, true);
        meta.lore(List.of(
            text("Right click a grave while holding", GRAY),
            text("this to forcibly open it.", GRAY),
            text("Placing this down will break its functionality", GRAY, ITALIC)
        ));
        GRAVE_KEY.setItemMeta(meta);
    }

    private final Config config;

    @Inject
    Commands(final Config config) {
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        this.manager.command(this.literal(builder, "locate")
            .handler(this.sync((context, player) -> {
                if (!this.config.graveLocating) {
                    context.getSender().sendMessage(translatable("modules.graves.commands.locate.disabled", RED));
                    return;
                }
                final @Nullable Location location = player.getPersistentDataContainer().get(PlayerListener.LAST_GRAVE_LOCATION, DataTypes.LOCATION);
                if (location == null) {
                    context.getSender().sendMessage(translatable("modules.graves.commands.locate.none-found", RED));
                } else {
                    final Component loc = translatable("modules.graves.location-format", YELLOW, text(location.getBlockX()), text(location.getBlockY()), text(location.getBlockZ()));
                    final Component world = location.getWorld() != null ? text(location.getWorld().getName(), YELLOW) : text("unknown world");
                    context.getSender().sendMessage(translatable("modules.graves.last-grave-location", GOLD, loc, world));
                }
            }))
        ).command(this.adminLiteral(builder, "grave-key").handler(this.sync((context, player) -> player.getInventory().addItem(GRAVE_KEY))));

        this.config.createCommands(this, builder);
    }
}
