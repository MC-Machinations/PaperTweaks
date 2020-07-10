package me.machinemaker.vanillatweaks.trackrawstats.stats.materials;

import com.google.common.collect.Sets;
import me.machinemaker.vanillatweaks.trackrawstats.stats.IStat;
import me.machinemaker.vanillatweaks.utils.ReflectionUtils;
import me.machinemaker.vanillatweaks.utils.ReflectionUtils.MethodInvoker;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Material;

import java.util.Set;

public class BlockMined implements IStat {

    final String name;
    final String criteria;
    final String displayName;

    final String commandName;

    private BlockMined(String name, String criteria, String displayName, String commandName) {
        this.name = name;
        this.criteria = criteria;
        this.displayName = displayName;
        this.commandName = commandName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getCriteria() {
        return this.criteria;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getCommandName() {
        return this.commandName;
    }

    private static Set<BlockMined> cached;

    public static Set<BlockMined> stats() {
        if (cached != null) return cached;

        Class<?> localeLanguageClass = ReflectionUtils.getMinecraftClass("LocaleLanguage");
        Class<?> nmsItemClass = ReflectionUtils.getMinecraftClass("Item");
        Class<?> craftMagicNumbersClass = ReflectionUtils.getCraftBukkitClass("util.CraftMagicNumbers");
        MethodInvoker getLocale = ReflectionUtils.getMethod(localeLanguageClass, "getInstance");
        MethodInvoker getItem = ReflectionUtils.getTypedMethod(craftMagicNumbersClass, "getItem", nmsItemClass, Material.class);
        MethodInvoker getName = ReflectionUtils.getMethod(nmsItemClass, "getName");
        MethodInvoker translate = ReflectionUtils.getMethod(localeLanguageClass, "translateKey", String.class);
        Object locale = getLocale.invoke(null);

        Set<BlockMined> stats = Sets.newHashSet();
        for (Material material : Material.values()) {
            if (!material.isBlock() || material.isAir()) continue;
            String key = material.getKey().getKey();
            Object nmsItem = getItem.invoke(craftMagicNumbersClass, material);
            if (nmsItem == null) continue;
            String localizationName = (String) getName.invoke(nmsItem);
            stats.add(new BlockMined(RandomStringUtils.randomAlphabetic(16), "minecraft.mined:minecraft." + key,   translate.invoke(locale, localizationName)+ " Mined", key));
        }
        cached = stats;
        return cached;
    }
}
