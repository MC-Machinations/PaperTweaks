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
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import cloud.commandframework.bukkit.parsers.selector.MultiplePlayerSelectorArgument;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.pdc.DataTypes;
import me.machinemaker.papertweaks.utils.PTUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
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
        final boolean edited = GRAVE_KEY.editMeta(meta -> {
            meta.getPersistentDataContainer().set(PlayerListener.GRAVE_KEY, DataTypes.BOOLEAN, true);
            meta.lore(List.of(
                text("Right click a grave while holding", GRAY),
                text("this to forcibly open it.", GRAY),
                text("Placing this down will break its functionality", GRAY, ITALIC)
            ));
        });
        Preconditions.checkState(edited, "Could not create the grave key itemstack");
    }

    private final Config config;

    @Inject
    Commands(final Config config) {
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        this.register(
            this.literal(builder, "locate")
                .handler(this.sync((context, player) -> {
                    if (!this.config.graveLocating) {
                        context.getSender().sendMessage(translatable("modules.graves.commands.locate.disabled", RED));
                        return;
                    }
                    final @Nullable Location location = player.getPersistentDataContainer().get(PlayerListener.LAST_GRAVE_LOCATION, DataTypes.LOCATION);
                    if (location == null) {
                        context.getSender().sendMessage(translatable("modules.graves.commands.locate.none-found", RED));
                    } else {
                        final Component loc = formatLocation(location, false);
                        final Component world = location.getWorld() != null ? text(location.getWorld().getName(), YELLOW) : text("unknown world");
                        context.getSender().sendMessage(translatable("modules.graves.last-grave-location", GOLD, loc, world));
                    }
                }))
        );
        this.register(this.adminLiteral(builder, "grave-key").handler(this.sync((context, player) -> player.getInventory().addItem(GRAVE_KEY))));

        this.register(this.adminLiteral(builder, "locate")
            .argument(MultiplePlayerSelectorArgument.<CommandDispatcher>builder("targets").allowEmpty(false))
            .handler(this.sync((context) -> {
                final MultiplePlayerSelector selector = context.get("targets");
                final List<@NonNull Player> players = selector.getPlayers();
                for (final Player target : players) {
                    final Map<World, List<Location>> locations = locateGravesFor(target);
                    if (locations.isEmpty()) {
                        context.getSender().sendMessage(translatable("modules.graves.commands.admin.locate.none-found", target.displayName()));
                    } else {
                        locations.forEach((world, locs) -> {
                            context.getSender().sendMessage(translatable("modules.graves.commands.admin.locate.found.header", target.displayName(), text(world.key().asString())));
                            for (final Location loc : locs) {
                                context.getSender().sendMessage(formatLocation(loc, true));
                            }
                        });
                    }
                }
            }))
        );

        this.config.createCommands(this, builder);
    }

    private static Component formatLocation(final Location loc, final boolean hoverAndClick) {
        final TranslatableComponent.Builder builder = translatable().key("modules.graves.location-format").color(YELLOW).args(text(loc.getBlockX()), text(loc.getBlockY()), text(loc.getBlockZ()));
        if (hoverAndClick) {
            final String tpCommand = "/tp %s %s %s".formatted(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            return builder.hoverEvent(HoverEvent.showText(text(tpCommand))).clickEvent(ClickEvent.runCommand(tpCommand)).build();
        }
        return builder.build();
    }

    private static Map<World, List<Location>> locateGravesFor(final Player player) {
        final Map<World, List<Location>> locations = new HashMap<>();
        for (final World world : Bukkit.getWorlds()) {
            for (final ArmorStand stand : world.getEntitiesByClass(ArmorStand.class)) {
                if (stand.getPersistentDataContainer().has(PlayerListener.PLAYER_ALL_CONTENTS) && Objects.requireNonNull(stand.getPersistentDataContainer().get(PlayerListener.PLAYER_UUID, DataTypes.UUID), "Missing UUID pdc on armor stand").equals(player.getUniqueId())) {
                    locations.computeIfAbsent(world, $ -> new ArrayList<>()).add(stand.getLocation().add(0, 2, 0)); // move up a bit, so you aren't in the ground
                }
            }
        }
        return locations;
    }
}
