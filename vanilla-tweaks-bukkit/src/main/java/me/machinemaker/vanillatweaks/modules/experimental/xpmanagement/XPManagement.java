package me.machinemaker.vanillatweaks.modules.experimental.xpmanagement;

import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.modules.ModuleRecipe;
import me.machinemaker.vanillatweaks.utils.Keys;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

@ModuleInfo(name = "XPManagement", configPath = "experimental.xp-management", description = "Store XP in bottles, smelt in furnace to retrieve XP")
public class XPManagement extends ModuleBase {

    private static final NamespacedKey XP_RECIPE_KEY = Keys.key("xp_management_recipe");
    private static final FurnaceRecipe XP_RECIPE = new FurnaceRecipe(XP_RECIPE_KEY, new ItemStack(Material.GLASS_BOTTLE), Material.EXPERIENCE_BOTTLE, 12, 1);

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.SimpleLifecycle.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(PlayerListener.class);
    }

    @Override
    protected @NotNull Collection<ModuleRecipe<?>> recipes() {
        return Set.of(new ModuleRecipe<>(XP_RECIPE));
    }
}
