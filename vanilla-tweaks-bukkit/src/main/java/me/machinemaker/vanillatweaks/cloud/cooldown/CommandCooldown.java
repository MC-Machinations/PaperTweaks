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
package me.machinemaker.vanillatweaks.cloud.cooldown;

import cloud.commandframework.Command;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.meta.CommandMeta;
import io.leangen.geantyref.TypeToken;
import java.time.Duration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * A command cooldown which can be applied to one or more {@link Command.Builder}s.
 * Applying the same {@link CommandCooldown} instance to multiple commands will result
 * in a shared cooldown.
 *
 * @param <C> sender type
 */
@DefaultQualifier(NonNull.class)
public interface CommandCooldown<C> {

    CommandMeta.Key<CommandCooldown<?>> COMMAND_META_KEY = CommandMeta.Key.of(new TypeToken<>() {}, "vanillatweaks:command_cooldown");

    /**
     * Apply this {@link CommandCooldown} to a {@link Command.Builder} instance.
     *
     * @param builder command builder
     * @return modified copy of builder
     */
    default Command.Builder<C> applyTo(final Command.Builder<C> builder) {
        return builder.meta(COMMAND_META_KEY, this);
    }

    /**
     * Get the {@link CloudKey} for this cooldown.
     *
     * @return key
     */
    CloudKey<Void> key();

    /**
     * Get the {@link CooldownDuration} for this cooldown.
     *
     * @return duration
     */
    CooldownDuration<C> cooldownDuration();

    /**
     * Get the {@link CommandCooldownNotifier} for this cooldown.
     *
     * @return notifier
     */
    @Nullable CommandCooldownNotifier<C> notifier();

    static <C> CommandCooldownBuilder<C> builder(final Duration cooldown) {
        return CommandCooldownBuilder.create(cooldown);
    }

    static <C> CommandCooldownBuilder<C> builder(final CooldownDuration<C> cooldownDuration) {
        return CommandCooldownBuilder.create(cooldownDuration);
    }

}
