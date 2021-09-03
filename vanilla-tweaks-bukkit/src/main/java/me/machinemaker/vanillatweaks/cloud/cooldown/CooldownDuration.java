package me.machinemaker.vanillatweaks.cloud.cooldown;

import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;

@FunctionalInterface
public interface CooldownDuration<C> {

    static <C> CooldownDuration<C> constant(Duration duration) {
        return context -> duration;
    }

    @NonNull Duration getDuration(@NonNull CommandPostprocessingContext<C> context);
}
