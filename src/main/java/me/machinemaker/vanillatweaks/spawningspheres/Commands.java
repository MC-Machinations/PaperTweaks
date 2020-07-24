package me.machinemaker.vanillatweaks.spawningspheres;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import com.google.common.collect.Sets;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import me.machinemaker.vanillatweaks.utils.ReflectionUtils;
import me.machinemaker.vanillatweaks.utils.ReflectionUtils.FieldAccessor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;
import java.util.stream.Collectors;

@CommandAlias("spawningspheres|spawnsphere|ss")
class Commands extends BaseModuleCommand<SpawningSpheres> {

    private final double SMALL_RADIUS = 24;
    private final NamespacedKey COLOR = new NamespacedKey(this.module.plugin, "color");

    private final Set<Color> enabledColors = Sets.newHashSet();

    private final boolean hasCustomizableSpawningRadii;
    private Class<?> craftWorldClass;
    private FieldAccessor<?> worldServerField;
    private FieldAccessor<?> paperWorldConfigField;
    private FieldAccessor<Integer> softDespawnField;
    private FieldAccessor<Integer> hardDespawnField;

    public Commands(SpawningSpheres module) {
        super(module);
        boolean hasCustomSpawnTemp;
        try {
            Class<?> paperWorldConfigClass = Class.forName("com.destroystokyo.paper.PaperWorldConfig");
            this.craftWorldClass = ReflectionUtils.getCraftBukkitClass("CraftWorld");
            Class<?> worldServerClass = ReflectionUtils.getMinecraftClass("WorldServer");
            this.worldServerField = ReflectionUtils.getField(craftWorldClass, "world", worldServerClass);
            this.paperWorldConfigField = ReflectionUtils.getField(worldServerClass, "paperConfig", paperWorldConfigClass);
            this.softDespawnField = ReflectionUtils.getField(paperWorldConfigClass, "softDespawnDistance", int.class);
            this.hardDespawnField = ReflectionUtils.getField(paperWorldConfigClass, "hardDespawnDistance", int.class);
            hasCustomSpawnTemp = true;
            module.plugin.getLogger().info("This server supports custom despawning ranges. SpawningSpheres will take this into account.");
        } catch (ClassNotFoundException ignored) {
            hasCustomSpawnTemp = false;
        }
        hasCustomizableSpawningRadii = hasCustomSpawnTemp;
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("add")
    @CommandCompletion("@ss/colors")
    @Description("Adds a colored sphere")
    @CommandPermission("vanillatweaks.spawningspheres.add")
    public void add(Player player, Color color) {
        if (enabledColors.contains(color)) {
            player.sendMessage(Lang.COLOR_ALREADY_DISPLAYED.p().replace("%color%", color.name()));
            return;
        }
        DespawnDistances distances = getDistances(player.getWorld());
        World world = player.getWorld();
        Location center = player.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
        ArmorStand centerStand = (ArmorStand) world.spawnEntity(center.clone().subtract(0, 1, 0), EntityType.ARMOR_STAND);
        centerStand.setCustomName("Center");
        centerStand.setCustomNameVisible(true);
        configureStand(centerStand, color.center, false, color.toString());
        display(player.getWorld(), center.clone(), distances.hard, 8, color.outer, color.toString());
        display(player.getWorld(), center.clone(), SMALL_RADIUS, 4, color.inner, color.toString());
        player.sendMessage(Lang.DISPLAYED_SPHERE.p().replace("%color%", color.name()));
        enabledColors.add(color);
    }

    @Subcommand("remove")
    @CommandCompletion("@ss/colors")
    @Description("Removes a colored sphere")
    @CommandPermission("vanillatweaks.spawningspheres.remove")
    public void remove(Player player, Color color) {
        if (!enabledColors.contains(color)) {
            player.sendMessage(Lang.COLOR_ALREADY_REMOVED.p().replace("%color%", color.name()));
            return;
        }
        Set<Entity> toBeRemoved = player.getWorld().getEntitiesByClass(ArmorStand.class).stream().filter(stand -> color.toString().equals(stand.getPersistentDataContainer().get(COLOR, PersistentDataType.STRING))).collect(Collectors.toSet());
        if (toBeRemoved.size() < 1) {
            player.sendMessage(Lang.SPHERE_ERROR.p().replace("%color%", color.name()));
            return;
        }
        toBeRemoved.forEach(Entity::remove);
        player.sendMessage(Lang.REMOVED_SPHERE.p().replace("%color%", color.name()));
        enabledColors.remove(color);
    }

    private void display(World world, Location center, double radius, double step,  Material head, String id) {
        for (double x = -radius; x < radius; x += step) {
            for (double z = -radius; z < radius; z += step) {
                double y = Math.sqrt(radius*radius - x*x - z*z);
                ArmorStand stand1 = (ArmorStand) world.spawnEntity(center.clone().subtract(-x, y, -z), EntityType.ARMOR_STAND);
                ArmorStand stand2 = (ArmorStand) world.spawnEntity(center.clone().add(-x, y, -z), EntityType.ARMOR_STAND);
                configureStand(stand1, head, true, id);
                configureStand(stand2, head, true, id);
            }
        }
    }

    private void configureStand(ArmorStand stand, Material head, boolean glowing, String id) {
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setMarker(true);
        stand.setCollidable(false);
        stand.setArms(false);
        stand.setInvulnerable(true);
        stand.getPersistentDataContainer().set(COLOR, PersistentDataType.STRING, id);
        stand.getEquipment().setHelmet(new ItemStack(head));
        if (glowing) stand.setGlowing(true);
    }

    private DespawnDistances getDistances(World world) {
        if (!hasCustomizableSpawningRadii) return new DespawnDistances(32 * 32, 128 * 128);
        Object craftWorld = craftWorldClass.cast(world);
        Object worldServer = worldServerField.get(craftWorld);
        Object paperWorldConfig = paperWorldConfigField.get(worldServer);
        return new DespawnDistances(softDespawnField.get(paperWorldConfig), hardDespawnField.get(paperWorldConfig));
    }

    private static class DespawnDistances {
        int soft;
        int hard;

        public DespawnDistances(int soft, int hard) {
            this.soft = (int) Math.sqrt(soft);
            this.hard = (int) Math.sqrt(hard);
        }
    }

    enum Color {
        RED(Material.REDSTONE_BLOCK, Material.RED_CONCRETE, Material.ORANGE_CONCRETE),
        BLUE(Material.LAPIS_BLOCK, Material.BLUE_CONCRETE, Material.CYAN_CONCRETE),
        GREEN(Material.EMERALD_BLOCK, Material.GREEN_CONCRETE, Material.LIME_CONCRETE);

        final Material center;
        final Material inner;
        final Material outer;

        Color(Material center, Material inner, Material outer) {
            this.center = center;
            this.inner = inner;
            this.outer = outer;
        }
    }
}
