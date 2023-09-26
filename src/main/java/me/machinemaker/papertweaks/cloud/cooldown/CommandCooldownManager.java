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
package me.machinemaker.papertweaks.cloud.cooldown;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import cloud.commandframework.execution.postprocessor.CommandPostprocessor;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.services.types.ConsumerService;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Manages {@link CommandCooldown}s.
 *
 * @param <C> command sender type
 * @param <I> identification type
 */
public final class CommandCooldownManager<C, I> {

    private final Function<C, @Nullable I> identificationMapper;
    private final CommandCooldown.Notifier<C> defaultNotifier;
    private final Map<I, Map<CloudKey<Void>, Long>> commandsOnCooldown;
    private final ScheduledExecutorService executorService;

    private CommandCooldownManager(
            final Function<C, @Nullable I> identificationMapper,
            final CommandCooldown.Notifier<C> defaultNotifier,
            final ScheduledExecutorService executorService
    ) {
        this.identificationMapper = identificationMapper;
        this.defaultNotifier = defaultNotifier;
        this.executorService = executorService;
        this.commandsOnCooldown = new ConcurrentHashMap<>();
    }

    /**
     * Create a {@link CommandCooldownManager}.
     *
     * @param identificationMapper function to convert between a command sender type
     *                             and an identifier type that persists across relogs.
     *                             If the mapper returns null for the identifier,
     *                             no cooldown protections will be applied
     * @param defaultNotifier      called when a cooldown prevents a command from being executed
     * @param executorService      schedules removals from the cooldown map
     */
    public static <C, I> CommandCooldownManager<C, I> create(
            final Function<C, @Nullable I> identificationMapper,
            final CommandCooldown.Notifier<C> defaultNotifier,
            final ScheduledExecutorService executorService
    ) {
        return new CommandCooldownManager<>(identificationMapper, defaultNotifier, executorService);
    }

    /**
     * @param manager the command manager
     */
    public void registerCooldownManager(final CommandManager<C> manager) {
        manager.registerCommandPostProcessor(new CommandCooldownPostprocessor());
    }

    public synchronized void invalidate(final I id, final Command<C> command) {
        command.getCommandMeta().get(CommandCooldown.COMMAND_META_KEY)
                .ifPresent(cooldown -> this.invalidate(id, cooldown.key()));
    }

    public synchronized void invalidate(final I id, final CloudKey<Void> cloudKey) {
        final @Nullable Map<CloudKey<Void>, Long> identifiedMap = this.commandsOnCooldown.get(id);
        if (identifiedMap != null) {
            identifiedMap.remove(cloudKey);
            if (identifiedMap.isEmpty()) {
                this.commandsOnCooldown.remove(id);
            }
        }
    }

    private final class CommandCooldownPostprocessor implements CommandPostprocessor<C> {

        @SuppressWarnings("unchecked")
        @Override
        public void accept(final CommandPostprocessingContext<C> context) {
            final I id = this.mapToId(context.getCommandContext().getSender());
            if (id == null) {
                return;
            }
            final Optional<Duration> cooldownDuration = this.cooldownDuration(context);
            if (cooldownDuration.isPresent() && !cooldownDuration.get().isZero()) {
                final CommandCooldown<C> commandCooldown = (CommandCooldown<C>) context.getCommand().getCommandMeta().get(CommandCooldown.COMMAND_META_KEY).orElseThrow();
                final CloudKey<Void> commandCooldownKey = commandCooldown.key();
                final long cooldownMillis = cooldownDuration.get().toMillis();
                final long currentMillis = System.currentTimeMillis();
                if (CommandCooldownManager.this.commandsOnCooldown.containsKey(id)) {
                    final Map<CloudKey<Void>, Long> senderCooldownMap = CommandCooldownManager.this.commandsOnCooldown.getOrDefault(id, Collections.emptyMap());
                    if (senderCooldownMap.containsKey(commandCooldownKey)) {
                        final Long blockedUntil = senderCooldownMap.get(commandCooldownKey);
                        if (currentMillis < blockedUntil) {
                            final CommandCooldown.@Nullable Notifier<C> customNotifier = commandCooldown.notifier();
                            final CommandCooldown.@Nullable Notifier<C> notifier = customNotifier == null
                                    ? CommandCooldownManager.this.defaultNotifier
                                    : customNotifier;
                            notifier.notify(context, cooldownDuration.get(), (blockedUntil - currentMillis) / 1000);
                            ConsumerService.interrupt();
                        }
                    } else {
                        senderCooldownMap.put(commandCooldownKey, currentMillis + cooldownMillis);
                        this.setupEntryRemoval(id, context.getCommand(), cooldownMillis);
                    }
                } else {
                    final Map<CloudKey<Void>, Long> map = new ConcurrentHashMap<>(Map.of(commandCooldownKey, currentMillis + cooldownMillis));
                    CommandCooldownManager.this.commandsOnCooldown.put(id, map);
                    this.setupEntryRemoval(id, context.getCommand(), cooldownMillis);
                }
            }
        }

        private void setupEntryRemoval(final I identity, final Command<C> command, final long cooldown) {
            CommandCooldownManager.this.executorService.schedule(
                    () -> CommandCooldownManager.this.invalidate(identity, command),
                    cooldown,
                    TimeUnit.MILLISECONDS
            );
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private Optional<Duration> cooldownDuration(final CommandPostprocessingContext<C> context) {
            return context.getCommand().getCommandMeta()
                    .get(CommandCooldown.COMMAND_META_KEY)
                    .map(cooldown -> cooldown.duration().getDuration((CommandPostprocessingContext) context));
        }

        private @Nullable I mapToId(final C sender) {
            return CommandCooldownManager.this.identificationMapper.apply(sender);
        }
    }

}
