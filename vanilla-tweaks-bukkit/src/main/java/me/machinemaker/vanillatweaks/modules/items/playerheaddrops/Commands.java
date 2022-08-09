package me.machinemaker.vanillatweaks.modules.items.playerheaddrops;

import com.google.inject.Inject;
import me.machinemaker.vanillatweaks.modules.ConfiguredModuleCommand;
import me.machinemaker.vanillatweaks.modules.ModuleCommand;

@ModuleCommand.Info(value = "playerheaddrops", aliases = {"phd", "pheaddrops"}, i18n = "player-head-drops", perm = "playerheaddrops")
final class Commands extends ConfiguredModuleCommand {

    private final Config config;

    @Inject
    Commands(final Config config) {
        this.config = config;
    }

    @Override
    protected void registerCommands() {
        this.config.createCommands(this, this.player());
    }
}
