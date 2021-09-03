/*
 * VanillaTweaks, a performant replacement for the VanillaTweaks datapacks.
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
import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import cloud.commandframework.execution.postprocessor.CommandPostprocessor;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.services.types.ConsumerService;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Manages command cooldowns
 *
 * @param <C> command sender type
 * @param <I> identification type
 */
public final class CommandCooldownManager<C, I> {

    static final String COOLDOWN_DURATION_KEY = "vanillatweaks:command_cooldown";
    static final String COOLDOWN_NOTIFIER_KEY = "vanillatweaks:command_cooldown_notifier";


    private final Function<@NonNull C, @Nullable I> identificationMapper;
    private final CommandCooldownNotifier<C> defaultNotifier;
    private final Map<I, Map<Command<C>, Long>> commandsOnCooldown;
    private final ScheduledExecutorService executorService;

    public final CommandMeta.Key<@NonNull CooldownDuration<C>> cooldownKey = CommandMeta.Key.of(new TypeToken<CooldownDuration<C>>() {}, COOLDOWN_DURATION_KEY);
    public final CommandMeta.Key<@NonNull CommandCooldownNotifier<C>> notifierKey = CommandMeta.Key.of(new TypeToken<CommandCooldownNotifier<C>>() {}, COOLDOWN_NOTIFIER_KEY);

    /**
     * Construct a {@link CommandCooldownManager}
     *
     * @param identificationMapper function to convert between a command sender type
     *                             and an identifier type that persists across relogs.
     *                             If the mapper returns null for the identifier,
     *                             no cooldown protections will be applied
     * @param defaultNotifier called when a cooldown prevents a command from being executed
     * @param executorService schedules removals from the cooldown map
     */
    public CommandCooldownManager(Function<@NonNull C, @Nullable I> identificationMapper, CommandCooldownNotifier<C> defaultNotifier, ScheduledExecutorService executorService) {
        this.identificationMapper = identificationMapper;
        this.defaultNotifier = defaultNotifier;
        this.executorService = executorService;
        this.commandsOnCooldown = new ConcurrentHashMap<>();
    }

    /**
     * @param manager the command manager
     */
    public void registerCooldownManager(final @NonNull CommandManager<C> manager) {
        manager.registerCommandPostProcessor(new CommandCooldownPostprocessor());
    }

    public synchronized void invalidate(final @NonNull I id, final @NonNull Command<C> command) {
        Map<Command<C>, Long> identifiedMap = this.commandsOnCooldown.get(id);
        if (identifiedMap != null) {
            identifiedMap.remove(command);
            if (identifiedMap.isEmpty()) {
                this.commandsOnCooldown.remove(id);
            }
        }
    }

    private final class CommandCooldownPostprocessor implements CommandPostprocessor<C> {

        @Override
        public void accept(@NonNull CommandPostprocessingContext<C> context) {
            final I id = mapToId(context.getCommandContext().getSender());
            if (id == null) return;
            final Optional<Duration> cooldown = getCommandCooldown(context);
            if (cooldown.isPresent() && !cooldown.get().isZero()) {
                final long cooldownMillis = cooldown.get().toMillis();
                final long currentMillis = System.currentTimeMillis();
                if (CommandCooldownManager.this.commandsOnCooldown.containsKey(id)) {
                    final Map<Command<C>, Long> senderCooldownMap = CommandCooldownManager.this.commandsOnCooldown.getOrDefault(id, Collections.emptyMap());
                    if (senderCooldownMap.containsKey(context.getCommand())) {
                        final Long blockedUntil = senderCooldownMap.get(context.getCommand());
                        if (currentMillis < blockedUntil) {
                            var notifier = context.getCommand().getCommandMeta().getOrDefault(CommandCooldownManager.this.notifierKey, CommandCooldownManager.this.defaultNotifier);
                            notifier.notify(context, cooldown.get(), (blockedUntil - currentMillis) / 1000);
                            ConsumerService.interrupt();
                        }
                    } else {
                        senderCooldownMap.put(context.getCommand(), currentMillis + cooldownMillis);
                        setupEntryRemoval(id, context.getCommand(), cooldownMillis);
                    }
                } else {
                    Map<Command<C>, Long> map = new ConcurrentHashMap<>(Map.of(context.getCommand(), currentMillis + cooldownMillis));
                    CommandCooldownManager.this.commandsOnCooldown.put(id, map);
                    setupEntryRemoval(id, context.getCommand(), cooldownMillis);
                }
            }
        }

        private void setupEntryRemoval(I identity, Command<C> command, long cooldown) {
            CommandCooldownManager.this.executorService.schedule(() -> {
                CommandCooldownManager.this.invalidate(identity, command);
            }, cooldown, TimeUnit.MILLISECONDS);
        }

        private Optional<Duration> getCommandCooldown(CommandPostprocessingContext<C> context) {
            return context.getCommand().getCommandMeta()
                    .get(CommandCooldownManager.this.cooldownKey)
                    .map(durationFunction -> durationFunction.getDuration(context));
        }


        private @Nullable I mapToId(C sender) {
            return CommandCooldownManager.this.identificationMapper.apply(sender);
        }
    }

}
