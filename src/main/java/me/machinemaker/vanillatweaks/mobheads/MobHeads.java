package me.machinemaker.vanillatweaks.mobheads;

import me.machinemaker.vanillatweaks.BaseModule;
import me.machinemaker.vanillatweaks.VanillaTweaks;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class MobHeads extends BaseModule implements Listener {

    public MobHeads(VanillaTweaks vanillaTweaks) {
        super(vanillaTweaks, config -> config.moreMobHeads);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null && !event.getEntity().getKiller().hasPermission("vanillatweaks.moremobheads")) return;
        int lootingLevel = 0;
        if (event.getEntity().getKiller() != null) {
            lootingLevel = event.getEntity().getKiller().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        }

        Head<? extends LivingEntity> head = Head.headMap.get(event.getEntityType());
        if (head != null && head.chance(lootingLevel)) {
            event.getDrops().add(head.getSkull().clone());
        } else if (head == null) {
            List<Head<? extends LivingEntity>> multiHeads = Head.multiHeadMap.get(event.getEntityType());
            if (multiHeads == null) return;
            for (Head<? extends LivingEntity> mobHead : multiHeads) {
                if (mobHead.test(event.getEntity())) {
                    head = mobHead;
                    break;
                }
            }
            if (head != null && head.chance(lootingLevel)) {
                event.getDrops().add(head.getSkull().clone());
            }
        }
    }

    @Override
    public void register() {
        this.registerEvents(this);
    }

    @Override
    public void unregister() {
        this.unregisterEvents(this);
    }
}
