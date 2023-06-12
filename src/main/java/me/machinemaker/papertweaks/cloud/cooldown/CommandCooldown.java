/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2022-2023 Machine_Maker
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
package me.machinemaker.papertweaks.cloud.cooldown;

import cloud.commandframework.Command;
import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.meta.CommandMeta;
import io.leangen.geantyref.TypeToken;
import java.time.Duration;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A command cooldown which can be applied to one or more {@link Command.Builder}s.
 * Applying the same {@link CommandCooldown} instance to multiple commands will result
 * in a shared cooldown.
 *
 * @param <C> sender type
 */
public interface CommandCooldown<C> {

    CommandMeta.Key<CommandCooldown<?>> COMMAND_META_KEY = CommandMeta.Key.of(new TypeToken<>() {}, "papertweaks:command_cooldown");

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
     * Get the {@link DurationFunction} for this cooldown.
     *
     * @return duration
     */
    DurationFunction<C> duration();

    /**
     * Get the {@link Notifier} for this cooldown.
     *
     * @return notifier
     */
    @Nullable Notifier<C> notifier();

    static <C> Builder<C> builder(final Duration cooldown) {
        return builder(DurationFunction.constant(cooldown));
    }

    static <C> Builder<C> builder(final DurationFunction<C> cooldownDuration) {
        return new CommandCooldownImpl.BuilderImpl<>(cooldownDuration);
    }

    @FunctionalInterface
    interface DurationFunction<C> {

        static <C> DurationFunction<C> constant(final Duration duration) {
            return context -> duration;
        }

        @Nullable Duration getDuration(CommandPostprocessingContext<C> context);
    }

    @FunctionalInterface
    interface Notifier<C> {

        static <C> Notifier<C> empty() {
            return (context, cooldown, secondsLeft) -> {};
        }

        void notify(CommandPostprocessingContext<C> context, Duration cooldown, long secondsLeft);
    }

    /**
     * Builder for {@link CommandCooldown}.
     *
     * @param <C> sender type
     */
    interface Builder<C> {

        /**
         * Set a custom {@link Notifier}.
         *
         * @param notifier custom notifier
         * @return this builder
         */
        Builder<C> notifier(Notifier<C> notifier);

        /**
         * Set the cooldown key.
         *
         * <p>If not configured, a random one will be generated on {@link #build()}.</p>
         *
         * @param cooldownKey cooldown key
         * @return this builder
         */
        Builder<C> key(CloudKey<Void> cooldownKey);

        /**
         * Set the cooldown key.
         *
         * <p>If not configured, a random one will be generated on {@link #build()}.</p>
         *
         * @param cooldownKey cooldown key
         * @return this builder
         */
        default Builder<C> key(final String cooldownKey) {
            return this.key(SimpleCloudKey.of(cooldownKey));
        }

        /**
         * Create a new {@link CommandCooldown} from the current state of this builder.
         *
         * @return new cooldown
         */
        CommandCooldown<C> build();

    }
}
