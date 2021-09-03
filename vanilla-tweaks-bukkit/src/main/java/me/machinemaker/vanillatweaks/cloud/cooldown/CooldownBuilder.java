package me.machinemaker.vanillatweaks.cloud.cooldown;

import cloud.commandframework.Command;
import cloud.commandframework.meta.CommandMeta;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;

public class CooldownBuilder<C> {

    private final CommandMeta.Key<@NonNull CooldownDuration<C>> cooldownDurationKey = CommandMeta.Key.of(new TypeToken<CooldownDuration<C>>() {}, CommandCooldownManager.COOLDOWN_DURATION_KEY);
    private final CooldownDuration<C> cooldownDuration;
    private final CommandMeta.Key<@NonNull CommandCooldownNotifier<C>> notifierKey = CommandMeta.Key.of(new TypeToken<CommandCooldownNotifier<C>>() {}, CommandCooldownManager.COOLDOWN_NOTIFIER_KEY);
    private @Nullable CommandCooldownNotifier<C> notifier;

    private CooldownBuilder(@NonNull CooldownDuration<C> cooldownDuration)  {
        this.cooldownDuration = cooldownDuration;
    }

    public static <C> CooldownBuilder<C> builder(@NonNull Duration cooldown) {
        return builder(CooldownDuration.constant(cooldown));
    }

    public static <C> CooldownBuilder<C> builder(@NonNull CooldownDuration<C> cooldownDuration) {
        return new CooldownBuilder<>(cooldownDuration);
    }

    public CooldownBuilder<C> withNotifier(CommandCooldownNotifier<C> notifier) {
        this.notifier = notifier;
        return this;
    }

    public Command.@NonNull Builder<C> applyTo(Command.@NonNull Builder<C> builder) {
        builder = builder.meta(this.cooldownDurationKey, this.cooldownDuration);
        if (this.notifier != null) {
            builder = builder.meta(this.notifierKey, this.notifier);
        }
        return builder;
    }

}
