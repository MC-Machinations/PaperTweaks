/*
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
package me.machinemaker.vanillatweaks.modules.survival.graves;

import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.pdc.DataTypes;
import me.machinemaker.vanillatweaks.cloud.ModulePermission;
import me.machinemaker.vanillatweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class Commands extends ModuleCommand {

    private static final ItemStack GRAVE_KEY = VTUtils.getSkull(ChatColor.YELLOW + "Grave Key", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWVjNzA3NjllMzYzN2E3ZWRiNTcwMmJjYzQzM2NjMjQyYzJmMjIzNWNiNzNiOTQwODBmYjVmYWZmNDdiNzU0ZSJ9fX0=");

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
    protected void registerCommands(ModuleLifecycle lifecycle) {
        var builder = manager.commandBuilder("graves", RichDescription.translatable("modules.graves.commands.root")).senderType(PlayerCommandDispatcher.class);

        manager.command(
                builder.permission(ModulePermission.of(lifecycle, "vanillatweaks.graves.locate"))
                        .literal("locate", RichDescription.translatable("modules.graves.commands.locate"))
                        .handler(context -> {
                            if (!this.config.graveLocating) {
                                context.getSender().sendMessage(translatable("modules.graves.commands.locate.disabled", RED));
                                return;
                            }
                            Player player = PlayerCommandDispatcher.from(context);
                            Location location = player.getPersistentDataContainer().get(PlayerListener.LAST_GRAVE_LOCATION, DataTypes.LOCATION);
                            if (location == null) {
                                context.getSender().sendMessage(translatable("modules.graves.commands.locate.none-found", RED));
                            } else {
                                Component loc = translatable("modules.graves.location-format", YELLOW, text(location.getBlockX()), text(location.getBlockY()), text(location.getBlockZ()));
                                Component world = location.getWorld() != null ? text(location.getWorld().getName(), YELLOW) : text("unknown world");
                                context.getSender().sendMessage(translatable("modules.graves.last-grave-location", GOLD, loc, world));
                            }
                        })
        ).command(
                builder.permission(ModulePermission.of(lifecycle, "vanillatweaks.admin.grave-key"))
                        .literal("grave-key", RichDescription.translatable("modules.graves.commands.admin.grave-key"))
                        .handler(commandContext -> {
                            manager.taskRecipe().begin(commandContext).synchronous(context -> {
                                Player player = PlayerCommandDispatcher.from(context);
                                player.getInventory().addItem(GRAVE_KEY);
                            }).execute();
                        })
        );
    }
}
