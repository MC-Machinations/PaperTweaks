package me.machinemaker.vanillatweaks.tag;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.Lang;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
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
    private Team colorTeam;

    public Tag(VanillaTweaks plugin) {
        super(plugin, config -> config.tag);
        config.init(plugin, new File(plugin.getDataFolder(), "tag"));
        ItemMeta meta = tagItem.getItemMeta();
        meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.YELLOW + "Tag!");
        tagItem.setItemMeta(meta);
        this.registerCommands(new Commands(this));
        colorTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("tag/redcolor");
        if (colorTeam == null) {
            colorTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("tag/redcolor");
        }
        colorTeam.setColor(ChatColor.RED);
    }

    @EventHandler
    public void onPlayerInteract(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || event.getEntityType() != EntityType.PLAYER) return;
        Player damager = (Player) event.getDamager();
        Player damagee = (Player) event.getEntity();
        if (!damager.getInventory().getItemInMainHand().isSimilar(tagItem)) return;
        if (!damager.getPersistentDataContainer().has(this.tagKey, PersistentDataType.BYTE)) return;
        if (damagee.getPersistentDataContainer().has(this.tagKey, PersistentDataType.BYTE)) {
            damager.sendMessage(Lang.PLAYER_IS_ALREADY_IT.p().replace("%name%", damagee.getDisplayName()));
            return;
        }
        setAsIt(damagee);
        removeAsIt(damager);
        Bukkit.broadcastMessage(Lang.PLAYER_IS_IT.toString().replace("%name%", damagee.getDisplayName()));
    }

    void setAsIt(Player player) {
        colorTeam.addPlayer(player);
        player.getPersistentDataContainer().set(this.tagKey, PersistentDataType.BYTE, (byte) 1);
        player.setDisplayName(ChatColor.RED + player.getDisplayName());
        player.setPlayerListName(ChatColor.RED + player.getDisplayName());
        player.getInventory().addItem(tagItem.clone());
    }

    void removeAsIt(Player player) {
        colorTeam.removePlayer(player);
        player.setDisplayName(player.getName() + ChatColor.RESET);
        player.setPlayerListName(player.getName() + ChatColor.RESET);
        player.getPersistentDataContainer().remove(this.tagKey);
        player.getInventory().remove(tagItem.clone());
    }

    @Override
    public void register() {
        this.registerEvents(this);
    }

    @Override
    public void unregister() {
        this.unregisterEvents(this);
    }

    @Override
    public void reload() {
        config.reload();
    }
}
