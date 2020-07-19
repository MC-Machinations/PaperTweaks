package me.machinemaker.vanillatweaks.durabilityping;

import com.google.common.collect.Maps;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public class DurabilityPing extends BaseModule implements Listener {

    private final Config config = new Config();
    private final Map<UUID, Long> cooldownMap = Maps.newHashMap();
    private Commands commands;
    final NamespacedKey PING = new NamespacedKey(this.plugin, "ping");

    public DurabilityPing(VanillaTweaks plugin) {
        super(plugin, config -> config.durabilityPing);
        config.init(plugin, new File(plugin.getDataFolder(), "durabilityping"));
    }

    @EventHandler
    public void onDurabilityChange(PlayerItemDamageEvent event) {
        if (event.getPlayer().getPersistentDataContainer().get(PING, PersistentDataType.INTEGER) == 1 && event.getItem().getItemMeta() instanceof Damageable && 1 - (((Damageable) event.getItem().getItemMeta()).getDamage() / (double) event.getItem().getType().getMaxDurability()) < config.threshold && cooldownMap.computeIfAbsent(event.getPlayer().getUniqueId(), uuid -> System.currentTimeMillis() + (config.notificationCooldown * 1000)) < System.currentTimeMillis() && event.getPlayer().hasPermission("vanillatweaks.durabilityping")) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1f, 1f);
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("DURABILITY LOW!!").color(ChatColor.RED).create());
            cooldownMap.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + (config.notificationCooldown * 1000));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().getPersistentDataContainer().has(PING, PersistentDataType.INTEGER))
            event.getPlayer().getPersistentDataContainer().set(PING, PersistentDataType.INTEGER, 1);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        cooldownMap.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public void register() {
        this.commands = new Commands(this);
        this.registerCommands(commands);
        this.registerEvents(this);
    }

    @Override
    public void unregister() {
        this.unregisterCommands(commands);
        this.unregisterEvents(this);
    }

    @Override
    public void reload() {
        config.reload();
    }
}
