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
package me.machinemaker.papertweaks.modules.teleportation.back;

import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.pdc.DataTypes;
import me.machinemaker.papertweaks.pdc.PDCKey;
import me.machinemaker.papertweaks.utils.Keys;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

@ModuleInfo(name = "Back", configPath = "teleportation.back", description = "Adds a /back command to teleport back to previous locations")
public class Back extends ModuleBase {

    static final PDCKey<Location> BACK_LOCATION = new PDCKey<>(Keys.legacyKey("back/location"), DataTypes.LOCATION);

    @Override
    protected void configure() {
        super.configure();
        requestStaticInjection(BackTeleportRunnable.class);
    }

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.Empty.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(PlayerListener.class);
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }

    public static void setBackLocation(@NotNull Player player, @NotNull Location location) {
        BACK_LOCATION.setTo(player, location);
    }
}
