package me.machinemaker.papertweaks.cloud.dispatchers;

import java.util.Locale;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class FallbackCommandDispatcher extends CommandDispatcher {

    private final UUID uuid;

    protected FallbackCommandDispatcher(final CommandSender bukkitCommandSender) {
        super(bukkitCommandSender);
        if (bukkitCommandSender instanceof final Entity entity) {
            this.uuid = entity.getUniqueId();
        } else {
            this.uuid = UUID.randomUUID();
        }
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public Locale locale() {
        return Locale.US;
    }

    @Override
    public Audience audience() {
        return this.sender();
    }
}
