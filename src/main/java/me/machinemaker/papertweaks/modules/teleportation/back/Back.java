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
package me.machinemaker.papertweaks.modules.teleportation.back;

import java.util.Collection;
import java.util.Set;
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

@ModuleInfo(name = "Back", configPath = "teleportation.back", description = "Adds a /back command to teleport back to previous locations")
public class Back extends ModuleBase {

    static final PDCKey<Location> BACK_LOCATION = new PDCKey<>(Keys.legacyKey("back/location"), DataTypes.LOCATION);

    public static void setBackLocation(final Player player, final Location location) {
        BACK_LOCATION.setTo(player, location);
    }

    @Override
    protected void configure() {
        super.configure();
        this.requestStaticInjection(BackTeleportRunnable.class);
    }

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.Empty.class;
    }

    @Override
    protected Collection<Class<? extends ModuleCommand>> commands() {
        return Set.of(Commands.class);
    }

    @Override
    protected Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(PlayerListener.class);
    }

    @Override
    protected Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }
}
