/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2024 Machine_Maker
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
package me.machinemaker.papertweaks.modules.survival.coordinateshud;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.machinemaker.papertweaks.pdc.PDCKey;
import me.machinemaker.papertweaks.utils.Keys;
import me.machinemaker.papertweaks.utils.runnables.TimerRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Singleton
class HUDRunnable extends TimerRunnable {

    private static final PDCKey<Boolean> COORDINATES_HUD_KEY = PDCKey.bool(Keys.legacyKey("coordinateshud"));

    private final Set<UUID> enabled = ConcurrentHashMap.newKeySet();
    private final Config config;

    @Inject
    HUDRunnable(final Plugin plugin, final Config config) {
        super(plugin);
        this.config = config;
    }

    public void add(final Player player) {
        if (!COORDINATES_HUD_KEY.has(player)) {
            COORDINATES_HUD_KEY.setTo(player, this.config.enabledByDefault);
        }
        if (Boolean.TRUE.equals(COORDINATES_HUD_KEY.getFrom(player))) {
            this.enabled.add(player.getUniqueId());
        }
    }

    public void setAndAdd(final Player player) {
        COORDINATES_HUD_KEY.setTo(player, true);
        this.enabled.add(player.getUniqueId());
    }

    public boolean remove(final Player player) {
        return this.enabled.remove(player.getUniqueId());
    }

    public void setAndRemove(final Player player) {
        COORDINATES_HUD_KEY.setTo(player, false);
        this.enabled.remove(player.getUniqueId());
    }

    public boolean contains(final Player player) {
        return this.enabled.contains(player.getUniqueId());
    }

    @Override
    protected void start() {
        Bukkit.getOnlinePlayers().forEach(this::add);
    }

    @Override
    public synchronized void cancel() {
        super.cancel();
        this.enabled.clear();
    }

    @Override
    public void run() {
        final Iterator<UUID> iter = this.enabled.iterator();
        while (iter.hasNext()) {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(iter.next());
            if (!offlinePlayer.isOnline()) {
                iter.remove();
                continue;
            }
            final Player player = Objects.requireNonNull(offlinePlayer.getPlayer());
            final long time = (player.getWorld().getTime() + 6000) % 24000;
            final long hours = time / 1000;
            final Long extra = (time - (hours * 1000)) * 60 / 1000;

            final Location loc = player.getLocation();
            final TextComponent.Builder builder = Component.text().content("XYZ: ").color(NamedTextColor.GOLD).append(
                    Component.text(String.format("%d %d %d  ", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), NamedTextColor.WHITE),
                    Component.text(String.format("%2s      %02d:%02d", CoordinatesHUD.getDirection(loc.getYaw()).c, hours, extra))
            );
            player.sendActionBar(builder.build()); // TODO i18n
        }
    }
}
