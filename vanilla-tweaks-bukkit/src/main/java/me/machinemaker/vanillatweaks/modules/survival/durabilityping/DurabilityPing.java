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
package me.machinemaker.vanillatweaks.modules.survival.durabilityping;

import com.google.inject.Inject;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@ModuleInfo(name = "DurabilityPing", configPath = "survival.durability-ping", description = "Notifies players when their tools are close to breaking")
public class DurabilityPing extends ModuleBase {

    static final Sound SOUND = Sound.sound(Key.key("block.anvil.land"), Sound.Source.MASTER, 2, 1);

    final NamespacedKey pingKey;

    @Inject
    DurabilityPing(final JavaPlugin plugin) {
        this.pingKey = new NamespacedKey(plugin, "ping");
    }

    @Override
    protected void configure() {
        this.bind(Settings.class).asEagerSingleton();
        super.configure();
    }

    boolean shouldPing(final Player player) {
        return Objects.equals(player.getPersistentDataContainer().get(this.pingKey, PersistentDataType.INTEGER), 1);
    }

    void setToPing(final Player player) {
        player.getPersistentDataContainer().set(this.pingKey, PersistentDataType.INTEGER, 1);
    }

    void stopPinging(final Player player) {
        player.getPersistentDataContainer().set(this.pingKey, PersistentDataType.INTEGER, 0);
    }

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(PlayerListener.class);
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }
}
