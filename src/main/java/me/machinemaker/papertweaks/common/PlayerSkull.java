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
package me.machinemaker.papertweaks.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import me.machinemaker.papertweaks.utils.PTUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PlayerSkull {

    private final Component name;
    private final ItemStack skull;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PlayerSkull(final String name, final UUID uuid, final String texture, final @Nullable Integer count) {
        this.name = PTUtils.sanitizeName(name);
        this.skull = PTUtils.getSkull(this.name, uuid, texture, count != null ? count : 1);
    }

    public Component name() {
        return this.name;
    }

    public ItemStack cloneWithAmount(final int amount) {
        final ItemStack clone = this.skull.clone();
        clone.setAmount(amount);
        return clone;
    }

    public ItemStack cloneSingle() {
        return this.cloneWithAmount(1);
    }

    public ItemStack cloneOriginal() {
        return this.skull.clone();
    }
}
