/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2024-2025 Machine_Maker
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
package me.machinemaker.papertweaks;

import io.papermc.paper.ServerBuildInfo;
import java.time.Instant;
import java.util.Optional;
import java.util.OptionalInt;
import net.kyori.adventure.key.Key;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@SuppressWarnings("NonExtendableApiUsage")
@DefaultQualifier(NonNull.class)
public class DummyBuildInfo implements ServerBuildInfo {

    @Override
    public Key brandId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isBrandCompatible(final Key brandId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String brandName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String minecraftVersionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String minecraftVersionName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OptionalInt buildNumber() {
        return OptionalInt.empty();
    }

    @Override
    public Instant buildTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<String> gitBranch() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<String> gitCommit() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String asString(final ServerBuildInfo.StringRepresentation representation) {
        return "dummy for testing";
    }
}
