/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2026 Machine_Maker
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
package me.machinemaker.papertweaks.modules.other.chunkloader;

import com.google.inject.Singleton;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import me.machinemaker.papertweaks.pdc.DataTypes;
import me.machinemaker.papertweaks.utils.Keys;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;

@Singleton
public final class ChunkLoaderManager {

    private static final NamespacedKey CHUNK_LOADER_KEY = Keys.key("chunk-loader");
    private final Set<Location> chunkLoaders = new HashSet<>();

    public boolean isChunkLoader(Location location) {
        if (this.chunkLoaders.contains(location)) {
            return true;
        }

        if (this.hasChunkLoaderMarker(location)) {
            this.chunkLoaders.add(location);
            return true;
        }

        return false;
    }

    public void addChunkLoader(Location location) {
        this.chunkLoaders.add(location);
        this.setChunkLoaderMarker(location, true);
        location.getChunk().setForceLoaded(true);
        location.getWorld().playSound(location, Sound.BLOCK_CONDUIT_ACTIVATE, 1.0f, 1.0f);
    }

    public void removeChunkLoader(Location location) {
        this.chunkLoaders.remove(location);
        this.setChunkLoaderMarker(location, false);
        location.getChunk().setForceLoaded(false);
        location.getWorld().playSound(location, Sound.BLOCK_CONDUIT_DEACTIVATE, 1.0f, 1.0f);
    }

    public Set<Location> getChunkLoaders() {
        return Collections.unmodifiableSet(this.chunkLoaders);
    }

    private boolean hasChunkLoaderMarker(Location location) {
        if (location.getBlock().getType() != Material.LODESTONE) {
            return false;
        }

        Location stored = location.getChunk().getPersistentDataContainer().get(CHUNK_LOADER_KEY, DataTypes.LOCATION);
        if (stored == null || stored.getWorld() == null) {
            return false;
        }

        return stored.getWorld().equals(location.getWorld())
            && stored.getBlockX() == location.getBlockX()
            && stored.getBlockY() == location.getBlockY()
            && stored.getBlockZ() == location.getBlockZ();
    }

    private void setChunkLoaderMarker(Location location, boolean enabled) {
        if (location.getBlock().getType() != Material.LODESTONE) {
            return;
        }

        if (enabled) {
            location.getChunk().getPersistentDataContainer().set(CHUNK_LOADER_KEY, DataTypes.LOCATION, location.getBlock().getLocation());
        } else {
            location.getChunk().getPersistentDataContainer().remove(CHUNK_LOADER_KEY);
        }
    }
}
