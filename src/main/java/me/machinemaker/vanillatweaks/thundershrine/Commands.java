package me.machinemaker.vanillatweaks.thundershrine;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import me.machinemaker.vanillatweaks.BaseModuleCommand;
import me.machinemaker.vanillatweaks.Lang;
import me.machinemaker.vanillatweaks.utils.DataType;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandAlias("thundershrine|tshrine|ts")
class Commands extends BaseModuleCommand<ThunderShrine> {
    public Commands(ThunderShrine module) {
        super(module);
    }

    @HelpCommand
    @CommandPermission("vanillatweaks.thundershrine.help")
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("create")
    @Description("Creates a Thunder Shrine from an armor stand within 3 blocks")
    @CommandPermission("vanillatweaks.thundershrine.create")
    public void create(Player player) {
        Optional<ArmorStand> optionalArmorStand = player.getNearbyEntities(3, 3, 3).stream().filter(entity -> entity.getType() == EntityType.ARMOR_STAND).map(entity -> (ArmorStand) entity).findFirst();
        if (!optionalArmorStand.isPresent()) {
            player.sendMessage(Lang.NO_STAND_FOUND.p());
            return;
        }
        ArmorStand stand = optionalArmorStand.get();
        player.getWorld().spawnParticle(Particle.TOTEM, stand.getLocation(), 100, 0, 0, 0, 0.5);
        player.getWorld().playSound(stand.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.MASTER, 1.0f, 0.75f);
        AreaEffectCloud cloud = (AreaEffectCloud) player.getWorld().spawnEntity(stand.getLocation(), EntityType.AREA_EFFECT_CLOUD);
        cloud.setDuration(Integer.MAX_VALUE);
        cloud.setWaitTime(0);
        cloud.setRadius(0.01f);
        cloud.setParticle(Particle.SUSPENDED);
        cloud.getPersistentDataContainer().set(this.module.SHRINE, DataType.UUID, player.getUniqueId());
        this.module.shrineLocations.add(cloud);
        stand.remove();
    }

    @Subcommand("remove")
    @Description("Removes a Thunder Shrine from an armor stand within 3 blocks")
    @CommandPermission("vanillatweaks.thundershrine.remove")
    public void remove(Player player) {
        Optional<AreaEffectCloud> optionalAreaEffectCloud = player.getNearbyEntities(3, 3, 3).stream().filter(entity -> entity.getType() == EntityType.AREA_EFFECT_CLOUD && player.getUniqueId().equals(entity.getPersistentDataContainer().get(this.module.SHRINE, DataType.UUID))).map(entity -> (AreaEffectCloud) entity).findFirst();
        if (!optionalAreaEffectCloud.isPresent()) {
            player.sendMessage(Lang.NO_SHRINE.p());
            return;
        }
        optionalAreaEffectCloud.get().remove();
        player.sendMessage(Lang.REMOVED_SHRINE.p());
    }
}
