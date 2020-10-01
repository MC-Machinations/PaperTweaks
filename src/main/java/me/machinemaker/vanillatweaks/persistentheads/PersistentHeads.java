package me.machinemaker.vanillatweaks.persistentheads;

import io.papermc.lib.PaperLib;
import io.papermc.lib.features.blockstatesnapshot.BlockStateSnapshotResult;
import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import me.machinemaker.vanillatweaks.utils.datatypes.JsonDataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PersistentHeads extends BaseModule implements Listener {

    private final NamespacedKey NAME_KEY = new NamespacedKey(plugin, "head_name");
    private final NamespacedKey LORE_KEY = new NamespacedKey(plugin, "head_lore");
    private final PersistentDataType<String,String[]> LORE_PDT = new JsonDataType<>(String[].class);

    public PersistentHeads(VanillaTweaks plugin) {
        super(plugin, config -> config.persistHeadData);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        @NotNull ItemStack headItem = event.getItemInHand();
        if (headItem.getType() != Material.PLAYER_HEAD) return;
        ItemMeta meta = headItem.getItemMeta();
        if (meta == null) return;
        @NotNull String name = meta.getDisplayName();
        @Nullable List<String> lore = meta.getLore();
        @NotNull Block block = event.getBlockPlaced();
        // NOTE: Not using snapshots is broken: https://github.com/PaperMC/Paper/issues/3913
        BlockStateSnapshotResult blockStateSnapshotResult = PaperLib.getBlockState(block, true);
        TileState skullState = (TileState) blockStateSnapshotResult.getState();
        @NotNull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        skullPDC.set(NAME_KEY, PersistentDataType.STRING, name);
        if (lore != null) skullPDC.set(LORE_KEY, LORE_PDT, lore.toArray(new String[0]));
        if (blockStateSnapshotResult.isSnapshot()) skullState.update();
    }

    @EventHandler
    public void onBlockDropItemEvent(BlockDropItemEvent event) {
        @NotNull BlockState blockState = event.getBlockState();
        Material blockType = blockState.getType();
        if (blockType != Material.PLAYER_HEAD && blockType != Material.PLAYER_WALL_HEAD) return;
        TileState skullState = (TileState) blockState;
        @NotNull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        @Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
        @Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
        if (name == null) return;
        for (Item item: event.getItems()) { // Ideally should only be one...
            @NotNull ItemStack itemstack = item.getItemStack();
            if (itemstack.getType() == Material.PLAYER_HEAD) {
                @Nullable ItemMeta meta = itemstack.getItemMeta();
                if (meta == null) continue; // This shouldn't happen
                meta.setDisplayName(name);
                if (lore != null) meta.setLore(Arrays.asList(lore));
                itemstack.setItemMeta(meta);
            }
        }

    }

    @Override
    public void register() {
        registerEvents(this);
    }

    @Override
    public void unregister() {
        unregisterEvents(this);
    }
}
