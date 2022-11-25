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

import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import java.time.Duration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * Builder for {@link CommandCooldown}.
 *
 * @param <C> sender type
 */
@DefaultQualifier(NonNull.class)
public interface CommandCooldownBuilder<C> {

    /**
     * Set a custom {@link CommandCooldownNotifier}.
     *
     * @param notifier custom notifier
     * @return this builder
     */
    CommandCooldownBuilder<C> notifier(CommandCooldownNotifier<C> notifier);

    /**
     * Set the cooldown key.
     *
     * <p>If not configured, a random one will be generated on {@link #build()}.</p>
     *
     * @param cooldownKey cooldown key
     * @return this builder
     */
    CommandCooldownBuilder<C> key(CloudKey<Void> cooldownKey);

    /**
     * Set the cooldown key.
     *
     * <p>If not configured, a random one will be generated on {@link #build()}.</p>
     *
     * @param cooldownKey cooldown key
     * @return this builder
     */
    default CommandCooldownBuilder<C> key(final String cooldownKey) {
        return this.key(SimpleCloudKey.of(cooldownKey));
    }

    /**
     * Create a new {@link CommandCooldown} from the current state of this builder.
     *
     * @return new cooldown
     */
    CommandCooldown<C> build();

    static <C> CommandCooldownBuilder<C> create(final Duration cooldown) {
        return create(CooldownDuration.constant(cooldown));
    }

    static <C> CommandCooldownBuilder<C> create(final CooldownDuration<C> cooldownDuration) {
        return new CommandCooldownBuilderImpl<>(cooldownDuration);
    }

}
