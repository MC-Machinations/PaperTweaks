package me.machinemaker.vanillatweaks.cloud;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import cloud.commandframework.execution.postprocessor.CommandPostprocessor;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.meta.SimpleCommandMeta;
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


    public final CommandMeta.Key<@NonNull Duration> META_COOLDOWN_LENGTH_KEY = CommandMeta.Key.of(Duration.class, "vanillatweaks:command_cooldown");
    public final CommandMeta.Key<Function<@NonNull CommandPostprocessingContext<C>, @NonNull Duration>> META_COOLDOWN_SUPPLIER_LENGTH_KEY = CommandMeta.Key.of(new TypeToken<Function<@NonNull CommandPostprocessingContext<C>, @NonNull Duration>>() {}, "vanillatweaks:command_cooldown_supplier");


    private final Function<@NonNull C, @Nullable I> identificationMapper;
    private final CommandCooldownNotifier<C> notifier;
    private final Map<I, Map<Command<C>, Long>> commandsOnCooldown;
    private final ScheduledExecutorService executorService;

    /**
     * Construct a {@link CommandCooldownManager}
     *
     * @param identificationMapper function to convert between a command sender type
     *                             and an identifier type that persists across relogs.
     *                             If the mapper returns null for the identifier,
     *                             no cooldown protections will be applied
     * @param notifier called when a cooldown prevents a command from being executed
     * @param executorService schedules removals from the cooldown map
     */
    public CommandCooldownManager(Function<@NonNull C, @Nullable I> identificationMapper, CommandCooldownNotifier<C> notifier, ScheduledExecutorService executorService) {
        this.identificationMapper = identificationMapper;
        this.notifier = notifier;
        this.executorService = executorService;
        this.commandsOnCooldown = new ConcurrentHashMap<>();
    }

    /**
     * @param manager the command manager
     */
    public void registerCooldownManager(final @NonNull CommandManager<C> manager) {
        manager.registerCommandPostProcessor(new CommandCooldownPostprocessor());
    }

    public SimpleCommandMeta.@NonNull Builder decorate(final SimpleCommandMeta.@NonNull Builder builder, Duration cooldown) {
        return builder.with(META_COOLDOWN_LENGTH_KEY, cooldown);
    }

    public SimpleCommandMeta.@NonNull Builder decorate(final SimpleCommandMeta.@NonNull Builder builder, Function<@NonNull CommandPostprocessingContext<C>, @NonNull Duration> cooldownSupplier) {
        return builder.with(META_COOLDOWN_SUPPLIER_LENGTH_KEY, cooldownSupplier);
    }

    private final class CommandCooldownPostprocessor implements CommandPostprocessor<C> {

        @Override
        public void accept(@NonNull CommandPostprocessingContext<C> context) {
            final I id = mapToId(context.getCommandContext().getSender());
            if (id == null) return;
            final Optional<Long> cooldown = getCommandCooldown(context);
            if (cooldown.isPresent() && cooldown.get() > 0) {
                if (CommandCooldownManager.this.commandsOnCooldown.containsKey(id)) {
                    final Map<Command<C>, Long> senderCooldownMap = CommandCooldownManager.this.commandsOnCooldown.getOrDefault(id, Collections.emptyMap());
                    if (senderCooldownMap.containsKey(context.getCommand())) {
                        final Long blockedUntil = senderCooldownMap.get(context.getCommand());
                        if (System.currentTimeMillis() > blockedUntil) {
                            CommandCooldownManager.this.notifier.notify(context, cooldown.get(), System.currentTimeMillis() - blockedUntil);
                            ConsumerService.interrupt();
                        }
                    } else {
                        senderCooldownMap.put(context.getCommand(), System.currentTimeMillis() + cooldown.get());
                        CommandCooldownManager.this.executorService.schedule(() -> senderCooldownMap.remove(context.getCommand()), cooldown.get(), TimeUnit.MILLISECONDS);
                    }
                } else {
                    Map<Command<C>, Long> map = new ConcurrentHashMap<>(Map.of(context.getCommand(), System.currentTimeMillis()));
                    CommandCooldownManager.this.commandsOnCooldown.put(id, map);
                    setupEntryRemoval(map, id, context.getCommand(), cooldown.get());
                }
            }
        }

        private void setupEntryRemoval(Map<Command<C>, Long> map, I identity, Command<C> command, long cooldown) {
            CommandCooldownManager.this.executorService.schedule(() -> {
                map.remove(command);
                if (map.isEmpty()) {
                    CommandCooldownManager.this.commandsOnCooldown.remove(identity);
                }
            }, cooldown, TimeUnit.MILLISECONDS);
        }

        private Optional<Long> getCommandCooldown(CommandPostprocessingContext<C> context) {
            return context.getCommand().getCommandMeta()
                    .get(META_COOLDOWN_SUPPLIER_LENGTH_KEY)
                    .map(durationFunction -> durationFunction.apply(context))
                    .or(
                            () -> context.getCommand().getCommandMeta().get(META_COOLDOWN_LENGTH_KEY)
                    )
                    .map(Duration::toMillis);
        }


        private @Nullable I mapToId(C sender) {
            return CommandCooldownManager.this.identificationMapper.apply(sender);
        }
    }

    @FunctionalInterface
    public interface CommandCooldownNotifier<C> {

        void notify(@NonNull CommandPostprocessingContext<C> context, long cooldown, long secondsLeft);
    }
}
