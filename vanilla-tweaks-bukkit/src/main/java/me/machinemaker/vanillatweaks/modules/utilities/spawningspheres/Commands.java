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
package me.machinemaker.vanillatweaks.modules.utilities.spawningspheres;

import cloud.commandframework.arguments.standard.EnumArgument;
import com.google.common.math.IntMath;
import io.papermc.lib.PaperLib;
import me.machinemaker.vanillatweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.pdc.PDCKey;
import me.machinemaker.vanillatweaks.utils.Keys;
import me.machinemaker.vanillatweaks.utils.ReflectionUtils;
import me.machinemaker.vanillatweaks.utils.ReflectionUtils.FieldAccessor;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.util.Collection;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@ModuleCommand.Info(value = "spawningspheres", aliases = {"spawnsphere", "ss"}, i18n = "spawning-spheres", perm = "spawningspheres")
class Commands extends ConfiguredModuleCommand {

    private static final PDCKey<Color> COLOR_KEY = PDCKey.enums(Keys.key("color"), Color.class);

    private static final DespawnDistances VANILLA = new DespawnDistances(32, 128);
    private static final boolean SUPPORTS_CUSTOM_DESPAWN_RANGES;
    private static final boolean IS_PER_CATEGORY_DESPAWN_RANGES;
    private static FieldAccessor<?> CRAFT_WORLD_SERVER_LEVEL_FIELD;
    private static FieldAccessor<?> SERVER_LEVEL_PAPER_WORLD_CONFIG_FIELD;
    // private static FieldAccessor<Integer> PAPER_WORLD_CONFIG_SOFT_DESPAWN_FIELD; // This isn't the same distance as the inner spawn ring
    private static FieldAccessor<Integer> PAPER_WORLD_CONFIG_HARD_DESPAWN_FIELD;
    private static ReflectionUtils.MethodInvoker PAPER_YAML_CONFIG_METHOD;

    static {
        boolean supportsCustomDespawnRanges = false;
        boolean isPerCategoryDespawnRanges = false;
        if (PaperLib.isPaper()) {
            supportsCustomDespawnRanges = true;
            try {
                final Class<?> PAPER_WORLD_CONFIG_CLASS = Class.forName("com.destroystokyo.paper.PaperWorldConfig");
                try {
                    PAPER_WORLD_CONFIG_CLASS.getField("hardDespawnDistances");
                    isPerCategoryDespawnRanges = true;
                    PAPER_YAML_CONFIG_METHOD = ReflectionUtils.getMethod(Bukkit.getServer().spigot().getClass(), "getPaperConfig");
                } catch (NoSuchFieldException ex) {
                    final Class<?> CRAFT_WORLD_CLASS = ReflectionUtils.getCraftBukkitClass("CraftWorld");
                    final Class<?> SERVER_LEVEL_CLASS = ReflectionUtils.findMinecraftClass("server.level.WorldServer", "server.level.ServerLevel", "server.WorldServer");
                    CRAFT_WORLD_SERVER_LEVEL_FIELD = ReflectionUtils.getField(CRAFT_WORLD_CLASS, "world", SERVER_LEVEL_CLASS);
                    SERVER_LEVEL_PAPER_WORLD_CONFIG_FIELD = ReflectionUtils.getField(SERVER_LEVEL_CLASS, "paperConfig", PAPER_WORLD_CONFIG_CLASS);
                    // PAPER_WORLD_CONFIG_SOFT_DESPAWN_FIELD = ReflectionUtils.getField(PAPER_WORLD_CONFIG_CLASS, "softDespawnDistance", int.class);
                    PAPER_WORLD_CONFIG_HARD_DESPAWN_FIELD = ReflectionUtils.getField(PAPER_WORLD_CONFIG_CLASS, "hardDespawnDistance", int.class);
                }
            } catch (ClassNotFoundException | IllegalArgumentException exception) {
                SpawningSpheres.LOGGER.warn("Paper environment detected, but could not hook into any custom spawning ranges. This might be a bug", exception);
                supportsCustomDespawnRanges = false;
            }
        }
        SUPPORTS_CUSTOM_DESPAWN_RANGES = supportsCustomDespawnRanges;
        IS_PER_CATEGORY_DESPAWN_RANGES = isPerCategoryDespawnRanges;
    }

    private static DespawnDistances getDespawnDistances(World world) {
        if (SUPPORTS_CUSTOM_DESPAWN_RANGES && IS_PER_CATEGORY_DESPAWN_RANGES && PAPER_YAML_CONFIG_METHOD != null) {
            YamlConfiguration config = (YamlConfiguration) PAPER_YAML_CONFIG_METHOD.invoke(Bukkit.getServer().spigot());
            int hard = config.getInt("world-settings." + world.getName() + ".despawn-ranges.monster.hard", config.getInt("world-settings.default.despawn-ranges.monster.hard", 128));
            return new DespawnDistances(24, hard);
        } else if (SUPPORTS_CUSTOM_DESPAWN_RANGES && CRAFT_WORLD_SERVER_LEVEL_FIELD != null && SERVER_LEVEL_PAPER_WORLD_CONFIG_FIELD != null && PAPER_WORLD_CONFIG_HARD_DESPAWN_FIELD != null) {
            Object serverLevel = CRAFT_WORLD_SERVER_LEVEL_FIELD.get(world);
            Object paperWorldConfig = SERVER_LEVEL_PAPER_WORLD_CONFIG_FIELD.get(serverLevel);
            return new DespawnDistances(24, IntMath.sqrt(PAPER_WORLD_CONFIG_HARD_DESPAWN_FIELD.get(paperWorldConfig), RoundingMode.DOWN));
        } else {
            return VANILLA;
        }
    }


    @Override
    protected void registerCommands() {
        var builder = this.player();

        manager.command(literal(builder, "add")
                .argument(EnumArgument.of(Color.class, "color"))
                .handler(sync((context, player) -> {
                    Color color = context.get("color");
                    Collection<ArmorStand> stands = VTUtils.getEntitiesOfType(ArmorStand.class, player.getWorld(), stand -> color == COLOR_KEY.getFrom(stand));
                    if (!stands.isEmpty()) {
                        context.getSender().sendMessage(translatable("modules.spawning-spheres.commands.add.fail", RED, color));
                        return;
                    }
                    DespawnDistances distances = getDespawnDistances(player.getWorld());
                    Location center = VTUtils.toBlockLoc(player.getLocation()).add(0.5, 0, 0.5);
                    player.getWorld().spawn(center.clone().subtract(0, 1, 0), ArmorStand.class, stand -> {
                        configureStand(stand, context.get("color"), color.center);
                        stand.setCustomName("Center");
                        stand.setCustomNameVisible(true);
                    });
                    display(player.getWorld(), center, distances.hard, 8, color, color.outer);
                    display(player.getWorld(), center, distances.soft, 4, color, color.inner);
                    context.getSender().sendMessage(translatable("modules.spawning-spheres.commands.add.succeed", GREEN, color));
                }))
        ).command(literal(builder, "remove")
                .argument(EnumArgument.of(Color.class, "color"))
                .handler(sync((context, player) -> {
                    Color color = context.get("color");
                    Collection<ArmorStand> sphereStands = VTUtils.getEntitiesOfType(ArmorStand.class, player.getWorld(), stand -> color == COLOR_KEY.getFrom(stand));
                    if (sphereStands.isEmpty()) {
                        context.getSender().sendMessage(translatable("modules.spawning-spheres.commands.remove.fail", RED, color));
                        return;
                    }
                    sphereStands.forEach(Entity::remove);
                    context.getSender().sendMessage(translatable("modules.spawning-spheres.commands.remove.succeed", GREEN, color));
                }))
        );
    }

    private void display(World world, Location center, double radius, double step, Color color, Material helmet) {
        for (double x = -radius; x < radius; x += step) {
            for (double z = -radius; z < radius; z += step) {
                double y = Math.sqrt(radius * radius - x * x - z * z);
                world.spawn(center.clone().subtract(-x, y, -z), ArmorStand.class, stand -> {
                    configureStand(stand, color, helmet);
                    stand.setGlowing(true);
                });
                world.spawn(center.clone().add(-x, y, -z), ArmorStand.class, stand -> {
                    configureStand(stand, color, helmet);
                    stand.setGlowing(true);
                });
            }
        }
    }

    private static void configureStand(ArmorStand stand, Color color, Material helmet) {
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setMarker(true);
        stand.setCollidable(false);
        stand.setArms(false);
        stand.setInvulnerable(true);
        stand.getEquipment().setHelmet(new ItemStack(helmet));
        COLOR_KEY.setTo(stand, color);
    }

    private static record DespawnDistances(int soft, int hard) {}

    enum Color implements ComponentLike {
        RED(Material.REDSTONE_BLOCK, Material.RED_CONCRETE, Material.ORANGE_CONCRETE, NamedTextColor.RED),
        BLUE(Material.LAPIS_BLOCK, Material.BLUE_CONCRETE, Material.CYAN_CONCRETE, NamedTextColor.BLUE),
        GREEN(Material.EMERALD_BLOCK, Material.GREEN_CONCRETE, Material.LIME_CONCRETE, NamedTextColor.GREEN);

        final Material center;
        final Material inner;
        final Material outer;
        final NamedTextColor color;

        Color(Material center, Material inner, Material outer, NamedTextColor color) {
            this.center = center;
            this.inner = inner;
            this.outer = outer;
            this.color = color;
        }

        @Override
        public @NotNull Component asComponent() {
            return text(name(), color, TextDecoration.ITALIC);
        }
    }
}
