/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021 Machine_Maker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.machinemaker.vanillatweaks.modules.mobs.moremobheads;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Collection;

class EntityListener implements ModuleListener {

    private final MoreMobHeads moreMobHeads;
    private final Config config;

    @Inject
    EntityListener(MoreMobHeads moreMobHeads, Config config) {
        this.moreMobHeads = moreMobHeads;
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (config.requirePlayerKill && event.getEntity().getKiller() == null) {
            return;
        }
        if (event.getEntity().getKiller() != null && !event.getEntity().getKiller().hasPermission("vanillatweaks.moremobheads")){
            return;
        }

        if (event.getEntity() instanceof Wither) { // Special handling for withers for the moment
            event.getDrops().add(VTUtils.random(this.moreMobHeads.heads.get(Wither.class)).createSkull());
            return;
        }

        int lootingLevel = 0;
        if (event.getEntity().getKiller() != null) {
            lootingLevel = event.getEntity().getKiller().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        }

        Collection<MobHead> heads = this.moreMobHeads.heads.get((Class<? extends LivingEntity>) event.getEntityType().getEntityClass());
        if (heads == null || heads.isEmpty()) {
            return;
        }
        for (MobHead head : heads) {
            if (head.test(event.getEntity())) {
                if (head.chance(lootingLevel)) {
                    event.getDrops().add(head.createSkull());
                }
                break;
            }
        }
    }
}
