package me.machinemaker.vanillatweaks.tag;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.Lang;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Team;

import java.io.File;

public class Tag extends BaseModule implements Listener {

    final Config config = new Config();
    final ItemStack tagItem = new ItemStack(Material.NAME_TAG);
    final NamespacedKey tagKey = new NamespacedKey(this.plugin, "tag");
    final NamespacedKey cooldownKey = new NamespacedKey(this.plugin, "cooldown");
    private Team colorTeam;

    private TagRunnable runnable;
    private Commands commands;

    public Tag(VanillaTweaks plugin) {
        super(plugin, config -> config.tag);
        config.init(plugin, new File(plugin.getDataFolder(), "tag"));
        ItemMeta meta = tagItem.getItemMeta();
        meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.YELLOW + "Tag!");
        tagItem.setItemMeta(meta);
        colorTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("tag/redcolor");
        if (colorTeam == null) {
            colorTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("tag/redcolor");
        }
        colorTeam.setColor(ChatColor.RED);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || event.getEntityType() != EntityType.PLAYER) return;
        Player damager = (Player) event.getDamager();
        Player damagee = (Player) event.getEntity();
        if (!damager.getInventory().getItemInMainHand().isSimilar(tagItem)) return;
        if (!damager.getPersistentDataContainer().has(this.tagKey, PersistentDataType.BYTE)) return;
        if (damagee.getPersistentDataContainer().has(this.tagKey, PersistentDataType.BYTE)) {
            damager.sendMessage(Lang.PLAYER_IS_ALREADY_IT.p().replace("%name%", damagee.getDisplayName()));
            return;
        }
        if (damager.getPersistentDataContainer().has(this.cooldownKey, PersistentDataType.LONG) && damager.getPersistentDataContainer().get(this.cooldownKey, PersistentDataType.LONG) > System.currentTimeMillis()) {
            damager.sendMessage(Lang.COOLDOWN_ACTIVE.p().replace("%time%", String.valueOf((damager.getPersistentDataContainer().get(this.cooldownKey, PersistentDataType.LONG)-System.currentTimeMillis())/1000)));
            return;
        }
        setAsIt(damagee);
        removeAsIt(damager);
        if (config.showMessages)
            Bukkit.broadcastMessage(Lang.PLAYER_IS_IT.toString().replace("%name%", damagee.getDisplayName()));
    }

    void setAsIt(Player player) {
        colorTeam.addPlayer(player);
        player.getPersistentDataContainer().set(this.tagKey, PersistentDataType.BYTE, (byte) 1);
        player.getPersistentDataContainer().set(this.cooldownKey, PersistentDataType.LONG, System.currentTimeMillis() + (this.config.timeBetweenTags * 1000L));
        player.setDisplayName(ChatColor.RED + player.getDisplayName());
        player.setPlayerListName(ChatColor.RED + player.getDisplayName());
        int firstEmpty = player.getInventory().firstEmpty();
        if (firstEmpty > -1) player.getInventory().setItem(firstEmpty, tagItem.clone());
        else {
            Item item = player.getWorld().dropItem(player.getLocation(), tagItem.clone());
            item.setPickupDelay(0);
            this.runnable.addPlayerItem(player, item);
        }
    }

    void removeAsIt(Player player) {
        colorTeam.removePlayer(player);
        player.setDisplayName(player.getName() + ChatColor.RESET);
        player.setPlayerListName(player.getName() + ChatColor.RESET);
        player.getPersistentDataContainer().remove(this.tagKey);
        player.getPersistentDataContainer().remove(this.cooldownKey);
        player.getInventory().remove(tagItem.clone());
    }

    @Override
    public void register() {
        this.commands = new Commands(this);
        this.registerCommands(commands);
        this.registerEvents(this);
        this.runnable = new TagRunnable(this);
        this.runnable.runTaskTimer(this.plugin, 0L, 5L);
    }

    @Override
    public void unregister() {
        this.unregisterCommands(commands);
        this.unregisterEvents(this);
        this.runnable.cancel();
    }

    @Override
    public void reload() {
        config.reload();
    }
}
