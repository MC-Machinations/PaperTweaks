package me.machinemaker.vanillatweaks.cloud.cooldown;

import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;

@FunctionalInterface
public interface CommandCooldownNotifier<C> {

    static <C> @NonNull CommandCooldownNotifier<C> empty() {
        return (context, cooldown, secondsLeft) -> {};
    }

    void notify(@NonNull CommandPostprocessingContext<C> context, @NonNull Duration cooldown, long secondsLeft);
}
