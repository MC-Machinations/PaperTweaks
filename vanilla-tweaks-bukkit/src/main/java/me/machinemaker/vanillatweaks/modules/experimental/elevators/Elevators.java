package me.machinemaker.vanillatweaks.modules.experimental.elevators;

import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleConfig;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import me.machinemaker.vanillatweaks.pdc.PDCKey;
import me.machinemaker.vanillatweaks.utils.Keys;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

@ModuleInfo(name = "Elevators", configPath = "experimental.elevators", description = "Create vertical elevators on wool blocks by throwing an enderpearl on the wool block")
public class Elevators extends ModuleBase {

    static final PDCKey<Boolean> IS_ELEVATOR = PDCKey.bool(Keys.key("is_elevator"));

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(PlayerListener.class, ItemListener.class);
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }
}
