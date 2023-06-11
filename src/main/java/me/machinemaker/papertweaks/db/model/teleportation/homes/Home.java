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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jdbi.v3.core.annotation.Unmappable;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import java.util.Objects;
import java.util.UUID;

public class Home {

    private final long id;
    private final UUID player;
    private String name;
    private final UUID world;
    private final int x;
    private final int y;
    private final int z;

    @JdbiConstructor
    public Home(long id, UUID player, UUID world, String name, int x, int y, int z) {
        this.id = id;
        this.player = player;
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Home(UUID player, String name, Location location) {
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
        return id;
    }

    public UUID getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Unmappable
    public @Nullable Location getLocation() {
        World world = Bukkit.getWorld(this.world);
        if (world != null) {
            return new Location(world, this.x, this.y, this.z);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Home home = (Home) o;
        return id == home.id && x == home.x && y == home.y && z == home.z && player.equals(home.player) && name.equals(home.name) && world.equals(home.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, player, name, world, x, y, z);
    }

    @Override
    public String toString() {
        return "Home{" +
                "id=" + id +
                ", player=" + player +
                ", name='" + name + '\'' +
                ", world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
