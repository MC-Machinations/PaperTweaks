/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
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
package me.machinemaker.papertweaks.db.model.teleportation.homes;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jdbi.v3.core.annotation.Unmappable;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

public class Home {

    private final long id;
    private final UUID player;
    private final UUID world;
    private final int x;
    private final int y;
    private final int z;
    private String name;

    @JdbiConstructor
    public Home(final long id, final UUID player, final UUID world, final String name, final int x, final int y, final int z) {
        this.id = id;
        this.player = player;
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Home(final UUID player, final String name, final Location location) {
        Preconditions.checkArgument(location.getWorld() != null, "location must have a world");
        this.id = -1;
        this.player = player;
        this.name = name;
        this.world = location.getWorld().getUID();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public long getId() {
        return this.id;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public UUID getWorld() {
        return this.world;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    @Unmappable
    public @Nullable Location getLocation() {
        final @Nullable World world = Bukkit.getWorld(this.world);
        if (world != null) {
            return new Location(world, this.x, this.y, this.z);
        }
        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Home home = (Home) o;
        return this.id == home.id && this.x == home.x && this.y == home.y && this.z == home.z && this.player.equals(home.player) && this.name.equals(home.name) && this.world.equals(home.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.player, this.name, this.world, this.x, this.y, this.z);
    }

    @Override
    public String toString() {
        return "Home{" +
            "id=" + this.id +
            ", player=" + this.player +
            ", name='" + this.name + '\'' +
            ", world=" + this.world +
            ", x=" + this.x +
            ", y=" + this.y +
            ", z=" + this.z +
            '}';
    }
}
