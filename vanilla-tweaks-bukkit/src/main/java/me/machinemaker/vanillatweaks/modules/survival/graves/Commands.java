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
package me.machinemaker.vanillatweaks.modules.survival.graves;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.pdc.DataTypes;
import me.machinemaker.vanillatweaks.utils.PTUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@ModuleCommand.Info(value = "graves", i18n = "graves", perm = "graves")
class Commands extends ConfiguredModuleCommand {

    private static final ItemStack GRAVE_KEY = PTUtils.getSkull(text("Grave Key", YELLOW).decoration(TextDecoration.ITALIC, false), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWVjNzA3NjllMzYzN2E3ZWRiNTcwMmJjYzQzM2NjMjQyYzJmMjIzNWNiNzNiOTQwODBmYjVmYWZmNDdiNzU0ZSJ9fX0=");

    static {
        ItemMeta meta = GRAVE_KEY.getItemMeta();
        if (meta == null) {
            throw new IllegalStateException("meta can't be null");
        }
        meta.getPersistentDataContainer().set(PlayerListener.GRAVE_KEY, DataTypes.BOOLEAN, true);
        meta.setLore(List.of(ChatColor.GRAY + "Right click a grave while holding", ChatColor.GRAY + "this to forcibly open it.", ChatColor.GRAY + "Placing this down will break its functionality.")); // ugh... ChatColor
        GRAVE_KEY.setItemMeta(meta);
    }

    private final Config config;

    @Inject
    Commands(Config config) {
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        var builder = this.player();

        manager.command(literal(builder, "locate")
                .handler(sync((context, player) -> {
                    if (!this.config.graveLocating) {
                        context.getSender().sendMessage(translatable("modules.graves.commands.locate.disabled", RED));
                        return;
                    }
                    Location location = player.getPersistentDataContainer().get(PlayerListener.LAST_GRAVE_LOCATION, DataTypes.LOCATION);
                    if (location == null) {
                        context.getSender().sendMessage(translatable("modules.graves.commands.locate.none-found", RED));
                    } else {
                        Component loc = translatable("modules.graves.location-format", YELLOW, text(location.getBlockX()), text(location.getBlockY()), text(location.getBlockZ()));
                        Component world = location.getWorld() != null ? text(location.getWorld().getName(), YELLOW) : text("unknown world");
                        context.getSender().sendMessage(translatable("modules.graves.last-grave-location", GOLD, loc, world));
                    }
                }))
        ).command(literal(builder, "grave-key").handler(sync((context, player) -> player.getInventory().addItem(GRAVE_KEY))));
    }
}
