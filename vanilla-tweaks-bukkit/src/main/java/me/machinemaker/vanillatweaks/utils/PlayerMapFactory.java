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
package me.machinemaker.vanillatweaks.utils;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import io.leangen.geantyref.TypeToken;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PlayerMapFactory implements Listener {

    private final Map<String, Map<Player, ?>> playerMap = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onPlayerQuit(final PlayerQuitEvent event) {
        this.playerMap.forEach((s, map) -> map.remove(event.getPlayer()));
    }

    public <T> PlayerMap<T> concurrent(final Key<T> key) {
        this.playerMap.put(key.name, Maps.newConcurrentMap());
        return new PlayerMap<>(key);
    }

    public <T> PlayerMap<T> hash(final Key<T> key) {
        this.playerMap.put(key.name, Maps.newHashMap());
        return new PlayerMap<>(key);
    }

    public static class Key<T> {

        private final String name;
        private final TypeToken<T> type;

        private Key(final String name, final TypeToken<T> type) {
            this.name = name;
            this.type = type;
        }

        public static <T> Key<T> of(final String name, final TypeToken<T> type) {
            return new Key<>(name, type);
        }

        public static <T> Key<T> of(final String name, final Class<T> classOfT) {
            return new Key<>(name, TypeToken.get(classOfT));
        }

        public String name() {
            return this.name;
        }

        public TypeToken<T> type() {
            return this.type;
        }

        @Override
        public boolean equals(final @Nullable Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final Key<?> key = (Key<?>) o;
            return this.name.equals(key.name) && this.type.equals(key.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.type);
        }

        @Override
        public String toString() {
            return "Key{" +
                    "name='" + this.name + '\'' +
                    ", type=" + this.type +
                    '}';
        }
    }

    public class PlayerMap<T> extends ForwardingMap<Player, T> {

        private final Key<T> key;

        private PlayerMap(final Key<T> key) {
            this.key = key;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Map<Player, T> delegate() {
            return (Map<Player, T>) PlayerMapFactory.this.playerMap.get(this.key.name);
        }
    }
}
