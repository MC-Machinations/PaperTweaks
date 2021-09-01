package me.machinemaker.vanillatweaks.modules.mobs.largerphantoms;

import me.machinemaker.vanillatweaks.annotations.ModuleInfo;
import me.machinemaker.vanillatweaks.modules.ModuleBase;
import me.machinemaker.vanillatweaks.modules.ModuleLifecycle;
import me.machinemaker.vanillatweaks.modules.ModuleListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

@ModuleInfo(name = "LargerPhantoms", configPath = "mobs.larger-phantoms", description = "Spawns larger phantoms the more you don't sleep")
public class LargerPhantoms extends ModuleBase {

    @Override
    protected @NotNull Class<? extends ModuleLifecycle> lifecycle() {
        return ModuleLifecycle.SimpleLifecycle.class;
    }

    @Override
    protected @NotNull Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(SpawnListener.class);
    }
}
