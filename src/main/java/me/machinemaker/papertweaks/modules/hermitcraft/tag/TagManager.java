/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
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
package me.machinemaker.papertweaks.modules.hermitcraft.tag;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.machinemaker.papertweaks.modules.survival.afkdisplay.AFKDisplay;
import me.machinemaker.papertweaks.utils.boards.Scoreboards;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

@Singleton
class TagManager {

    final Team tagTeam = Scoreboards.getTeam("tag/redcolor", RED);
    final Objective tagCounter = Scoreboards.getDummyObjective("tg_timesTagged", text("Times Tagged"));
    private final Config config;

    @Inject
    TagManager(final Config config) {
        this.config = config;
    }

    boolean setAsIt(final CommandSender from, final Player player) {
        if (AFKDisplay.AFK_DISPLAY.has(player)) {
            from.sendMessage(translatable("modules.tag.tag.fail.afk", RED));
            return false;
        }
        if (Tag.IT.has(player)) {
            from.sendMessage(translatable("modules.tag.tag.fail.already-it", RED, text(player.getName())));
            return false;
        }
        this.tagTeam.addEntry(player.getName());
        this.tagCounter.getScore(player.getName()).setScore(this.tagCounter.getScore(player.getName()).getScore() + 1);
        Tag.IT.setTo(player, true);
        Tag.COOLDOWN.setTo(player, System.currentTimeMillis() + (this.config.timeBetweenTags * 1000L));
        player.displayName(player.displayName().color(RED));
        player.playerListName(player.displayName().color(RED));
        final int firstEmpty = player.getInventory().firstEmpty();
        if (firstEmpty > -1) {
            player.getInventory().setItem(firstEmpty, Tag.TAG_ITEM.clone());
        } else {
            final Item item = player.getWorld().dropItem(player.getLocation(), Tag.TAG_ITEM.clone());
            item.setPickupDelay(0);
            item.setOwner(player.getUniqueId());
        }
        return true;
    }

    void removeAsIt(final Player player) {
        this.tagTeam.removeEntry(player.getName());
        player.displayName(null);
        player.playerListName(null);
        Tag.IT.remove(player);
        Tag.COOLDOWN.remove(player);
        player.getInventory().remove(Tag.TAG_ITEM.clone());
    }
}
