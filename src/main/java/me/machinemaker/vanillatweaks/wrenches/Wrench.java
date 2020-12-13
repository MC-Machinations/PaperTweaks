package me.machinemaker.vanillatweaks.wrenches;

import com.google.common.collect.Lists;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.utils.Tags;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

public class Wrench extends BaseModule implements Listener {

    private final String resourcePackUrl = "https://potrebic.box.com/shared/static/uw4fvii2o8qsjuz6xuant1safwjdnrez.zip";
    private final byte[] hash = new BigInteger("1ACF79C491B3CB9EEE50816AD0CC1FC45AABA147", 16).toByteArray();
    private final NamespacedKey RECIPE_KEY = new NamespacedKey(this.plugin, "redstone_wrench");
    private final List<BlockFace> faces = Lists.newArrayList(BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN);
    final ItemStack wrench = new ItemStack(Material.CARROT_ON_A_STICK, 1);
    final ShapedRecipe recipe;

    Config config = new Config();

    public Wrench(VanillaTweaks plugin) {
        super(plugin, config -> config.redstoneRotationWrench || config.terracottaRotationWrench);
        config.init(plugin, new File(plugin.getDataFolder(), "wrench"));
        ItemMeta meta = Objects.requireNonNull(wrench.getItemMeta());
        meta.setDisplayName(ChatColor.RESET + "Redstone Wrench");
        meta.setUnbreakable(true);
        meta.setCustomModelData(4321);
        wrench.setItemMeta(meta);
        recipe = new ShapedRecipe(RECIPE_KEY, wrench)
                .shape(
                    " # ",
                    " ##",
                    "$  "
                )
                .setIngredient('#', Material.GOLD_INGOT)
                .setIngredient('$', Material.IRON_INGOT);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getItem() != null && event.getItem().equals(wrench)) {
            Block block = event.getClickedBlock();
            if (((this.plugin.modules.redstoneRotationWrench && Tags.REDSTONE_COMPONENTS.isTagged(block.getType()) && event.getPlayer().hasPermission("vanillatweaks.wrench.redstone")) || (this.plugin.modules.terracottaRotationWrench && Tags.GLAZED_TERRACOTTA.isTagged(block.getType())) && event.getPlayer().hasPermission("vanillatweaks.wrench.terracotta")) && block.getBlockData() instanceof Directional) {
                Directional state = (Directional) block.getBlockData();
                int facing = faces.indexOf(state.getFacing());
                BlockFace nextFace = null;
                int i = 0;
                while (nextFace == null || !state.getFaces().contains(nextFace)) {
                    if (i >= 6) throw new IllegalStateException("Infinite loop detected");
                    nextFace = event.getPlayer().isSneaking() ? facing - 1 < 0 ? faces.get(facing + 6 - 1) : faces.get(facing - 1) : faces.get((facing + 1) % 6);
                    facing = faces.indexOf(nextFace);
                    i++;
                }
                event.setUseInteractedBlock(Result.DENY);
                state.setFacing(nextFace);
                block.setBlockData(state);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (config.suggestResourcePack) {
            event.getPlayer().setResourcePack(resourcePackUrl, hash);
        }
    }

    @Override
    public void register() {
        Bukkit.addRecipe(recipe);
        this.registerEvents(this);
        if (config.suggestResourcePack) {
            Bukkit.getOnlinePlayers().forEach(player -> player.setResourcePack(resourcePackUrl, hash));
        }
    }

    @Override
    public void unregister() {
        Bukkit.removeRecipe(RECIPE_KEY);
        this.unregisterEvents(this);
    }

    @Override
    public void reload() {
        config.reload();
    }
}
