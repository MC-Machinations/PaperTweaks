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
package me.machinemaker.papertweaks.modules.utilities.spawningspheres;

import java.util.Collection;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.pdc.PDCKey;
import me.machinemaker.papertweaks.utils.Entities;
import me.machinemaker.papertweaks.utils.Keys;
import me.machinemaker.papertweaks.utils.PTUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.Services;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;

@ModuleCommand.Info(value = "spawningspheres", aliases = {"spawnsphere", "ss"}, i18n = "spawning-spheres", perm = "spawningspheres")
class Commands extends ConfiguredModuleCommand {

    private static final PDCKey<Color> COLOR_KEY = PDCKey.enums(Keys.legacyKey("color"), Color.class);
    private static final double PHI = Math.PI * (3.0 - Math.sqrt(5.0));

    private static final DespawnDistances DESPAWN_DISTANCES = new PaperDespawnDistances();

    private static void configureStand(final ArmorStand stand, final Color color, final Material helmet) {
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setMarker(true);
        stand.setCollidable(false);
        stand.setArms(false);
        stand.setInvulnerable(true);
        stand.getEquipment().setHelmet(new ItemStack(helmet));
        COLOR_KEY.setTo(stand, color);
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        this.register(
            this.literal(builder, "add")
                .required("color", enumParser(Color.class))
                .handler(this.sync((context, player) -> {
                    final Color color = context.get("color");
                    final Collection<ArmorStand> stands = Entities.getEntitiesOfType(ArmorStand.class, player.getWorld(), stand -> color == COLOR_KEY.getFrom(stand));
                    if (!stands.isEmpty()) {
                        context.sender().sendMessage(translatable("modules.spawning-spheres.commands.add.fail", RED, color));
                        return;
                    }
                    final Location center = PTUtils.toBlockLoc(player.getLocation()).add(0.5, 0, 0.5);
                    player.getWorld().spawn(center.clone().subtract(0, 1, 0), ArmorStand.class, stand -> {
                        configureStand(stand, context.get("color"), color.center);
                        stand.customName(text("Center"));
                        stand.setCustomNameVisible(true);
                    });
                    this.fibonacciSphere(player.getWorld(), center, DESPAWN_DISTANCES.hard(player.getWorld()), 1500, color, color.outer);
                    this.fibonacciSphere(player.getWorld(), center, DESPAWN_DISTANCES.soft(player.getWorld()), 200, color, color.inner);
                    context.sender().sendMessage(translatable("modules.spawning-spheres.commands.add.succeed", GREEN, color));
                }))
        );
        this.register(
            this.literal(builder, "remove")
                .required("color", enumParser(Color.class))
                .handler(this.sync((context, player) -> {
                    final Color color = context.get("color");
                    final Collection<ArmorStand> sphereStands = Entities.getEntitiesOfType(ArmorStand.class, player.getWorld(), stand -> color == COLOR_KEY.getFrom(stand));
                    if (sphereStands.isEmpty()) {
                        context.sender().sendMessage(translatable("modules.spawning-spheres.commands.remove.fail", RED, color));
                        return;
                    }
                    sphereStands.forEach(Entity::remove);
                    context.sender().sendMessage(translatable("modules.spawning-spheres.commands.remove.succeed", GREEN, color));
                }))
        );
    }

    private void fibonacciSphere(final World world, final Location center, final double radius, final int count, final Color color, final Material helmet) {
        for (int i = 0; i < count; i++) {
            final double y = radius - ((i / (double) (count - 1)) * (2 * radius));
            final double radiusAtY = Math.sqrt(radius * radius - y * y);

            final double theta = PHI * i;

            final double x = Math.cos(theta) * radiusAtY;
            final double z = Math.sin(theta) * radiusAtY;
            world.spawn(center.clone().add(x, y, z), ArmorStand.class, stand -> {
                configureStand(stand, color, helmet);
                stand.setGlowing(true);
            });
        }
    }

    enum Color implements ComponentLike {
        RED(Material.REDSTONE_BLOCK, Material.RED_CONCRETE, Material.ORANGE_CONCRETE, NamedTextColor.RED),
        BLUE(Material.LAPIS_BLOCK, Material.BLUE_CONCRETE, Material.CYAN_CONCRETE, NamedTextColor.BLUE),
        GREEN(Material.EMERALD_BLOCK, Material.GREEN_CONCRETE, Material.LIME_CONCRETE, NamedTextColor.GREEN);

        final Material center;
        final Material inner;
        final Material outer;
        final NamedTextColor color;

        Color(final Material center, final Material inner, final Material outer, final NamedTextColor color) {
            this.center = center;
            this.inner = inner;
            this.outer = outer;
            this.color = color;
        }

        @Override
        public Component asComponent() {
            return text(this.name(), this.color, ITALIC);
        }
    }
}
